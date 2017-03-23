
package org.seckill.service;

import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

/**
 * 业务接口：站在"使用者"角度设计借口 三个方面：方法定义粒度（业务明确），参数（越少越简单），返回类型（return的类型 可以返回类型/异常）
 * 
 * @author yjl
 *
 */
public interface SeckillService
{
	
	/**
	 * 查询秒杀列表
	 * 
	 * @return
	 */
	List< Seckill > getSeckillList();
	
	/**
	 * 查询单个秒杀记录
	 * 
	 * @param seckillId
	 * @return
	 */
	Seckill getById( long seckillId );
	
	/**
	 * 秒杀开启是输出秒杀借口地址，否则输出系统时间和秒杀开启时间
	 * 
	 * @param seckillId
	 */
	Exposer exportSeckillUrl( long seckillId );
	
	/**
	 * 执行秒杀
	 * @param seckillId
	 * @param phone
	 * @param md5
	 */
	SeckillExecution executeSeckill (long seckillId, long phone ,String md5) throws SeckillException,RepeatKillException,SeckillCloseException;
	
}
