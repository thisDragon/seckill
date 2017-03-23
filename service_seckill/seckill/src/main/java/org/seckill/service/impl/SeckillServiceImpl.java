
package org.seckill.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

/**
 * 秒杀服务实现类
 * 
 * @author yjl
 *
 */
@Service
public class SeckillServiceImpl implements SeckillService
{
	// lf4j日志
	private Logger logger = LoggerFactory.getLogger( this.getClass() );
	
	@Resource
	private SeckillDao seckillDao;
	
	@Resource
	private SuccessKilledDao successKilledDao;
	
	// md5盐值字符串，用于混淆MD5
	private final String randomValue = "this is random!@#";
	
	/**
	 * 分页查询秒杀产品列表
	 */
	public List< Seckill > getSeckillList()
	{
		return seckillDao.queryAll( 0, 4 );
	}
	
	/**
	 * 通过id查看秒杀产品详情
	 */
	public Seckill getById( long seckillId )
	{
		return seckillDao.queryById( seckillId );
	}
	
	/**
	 * 获取秒杀地址（也就是获取正确的md5值）
	 */
	public Exposer exportSeckillUrl( long seckillId )
	{
		Seckill seckill = seckillDao.queryById( seckillId );
		
		// 【1】秒杀对象不存在
		if( seckill == null )
		{
			return new Exposer( false, seckillId );
		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		// 当前系统时间
		Date nowTime = new Date();
		
		// 【2】秒杀时间有误
		if( nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime() )
		{
			return new Exposer( false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime() );
		}
		
		// 【3】可以秒杀，返回md5,md5不可逆
		String md5 = getMD5( seckillId );
		
		return new Exposer( true, md5, seckillId );
	}
	
	/**
	 * 生成md5
	 * 
	 * @return md5
	 */
	private String getMD5( long seckillId )
	{
		String base = seckillId + "/" + randomValue;
		String md5 = DigestUtils.md5DigestAsHex( base.getBytes() );
		
		return md5;
		
	}
	
	/**
	 * 执行秒杀
	 */
	@Transactional
    /**
     * 使用注解控制事务方法的优点:
     * 1.开发团队达成一致约定，明确标注事务方法的编程风格
     * 2.保证事务方法的执行时间尽可能短，不要穿插其他网络操作RPC/HTTP请求或者剥离到事务方法外部
     * 3.不是所有的方法都需要事务，如只有一条修改操作、只读操作不要事务控制
     */
	public SeckillExecution executeSeckill( long seckillId, long phone, String md5 ) throws SeckillException, RepeatKillException, SeckillCloseException
	{
		// 【1】md5有误
		if( md5 == null || md5.equals( getMD5( seckillId ) ) == false )
		{
			throw new SeckillException( "seckill data rewrite" );
		}
		// 执行秒杀逻辑：减库存+记录购买行为
		Date nowTime = new Date();
		
		try
		{
			int updateCount = seckillDao.reduceNumber( seckillId, nowTime );
			// 是否进行了秒杀
			if( updateCount <= 0 )
			{
				// 没有更新数据，秒杀结束
				throw new SeckillCloseException( "seckill is closed" );
			}
			else
			{
				// 秒杀成功，记录购买行为
				int insertCount = successKilledDao.insertSuccessKilled( seckillId, phone );
				
				// 唯一：seckillId ，phone
				if( insertCount <= 0 )
				{
					// 插入失败，重复秒杀
					throw new RepeatKillException( "seckill repeated" );
				}
				else
				{
					// 秒杀成功
					SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill( seckillId, phone );
					return new SeckillExecution( seckillId, SeckillStatEnum.SUCCESS.getState(), SeckillStatEnum.SUCCESS.getInfo(), successKilled );
				}
			}
		}
		catch (SeckillCloseException e1)
		{
			logger.error( e1.getMessage(), e1 );
			throw e1;
		}
		catch (RepeatKillException e2)
		{
			logger.error( e2.getMessage(), e2 );
			throw e2;
		}
		catch (Exception e)
		{
			logger.error( e.getMessage(), e );
			// 所有编译器编译期异常，转换为运行期异常，可以在出现异常的时候能及时rollback
			throw new SeckillException( "seckill inner error:" + e.getMessage() );
		}
	}
	
}
