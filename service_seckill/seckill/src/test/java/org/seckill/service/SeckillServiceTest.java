
package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * 
 * @author yjl
 *
 */
@RunWith( SpringJUnit4ClassRunner.class )
// 告诉junit spring的配置文件
@ContextConfiguration(
{ "classpath:spring/spring-dao.xml", "classpath:spring/spring-service.xml" } )
public class SeckillServiceTest
{
	private final Logger logger = LoggerFactory.getLogger( this.getClass() );
	
	@Autowired
	private SeckillService seckillService;
	
	@Test
	public void getSeckillList() throws Exception
	{
		List< Seckill > seckills = seckillService.getSeckillList();
		
		logger.info( "seckillsList={}", seckills );
	}
	
	@Test
	public void getById() throws Exception
	{
		
		long seckillId = 1000;
		Seckill seckill = seckillService.getById( seckillId );
		
		logger.info( "seckill={}", seckill );
	}
	
	@Test
	// 完整逻辑代码测试，注意可重复执行
	public void testSeckillLogic() throws Exception
	{
		long seckillId = 1000;
		
		Exposer exposer = seckillService.exportSeckillUrl( seckillId );
		if( exposer.isExposed() )
		{
			logger.warn( "exposer={}" + exposer );
			
			long userPhone = 13476191876L;
			String md5 = exposer.getMd5();
			
			try
			{
				SeckillExecution seckillExecution = seckillService.executeSeckill( seckillId, userPhone, md5 );
				
				logger.info( "result={}", seckillExecution );
			}
			catch (RepeatKillException e)
			{
				logger.error( e.getMessage() );
			}
			catch (SeckillCloseException e)
			{
				logger.error( e.getMessage() );
			}
		}
		else
		{
			// 秒杀未开启
			logger.warn( "exposer={}" + exposer );
		}
	}
}