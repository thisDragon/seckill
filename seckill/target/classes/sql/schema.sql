--连接数据库控制台
mysql -uroot -p

-- 数据库初始化脚本

--创建数据库
CREATE DATABASE seckill;
--使用数据库
use seckill;
--创建秒杀库存表
CREATE TABLE seckill(
`seckill_id` BIGINT NOT NUll AUTO_INCREMENT COMMENT '商品库存id',
`name` VARCHAR(120) NOT NULL COMMENT '商品名称',
`number` int NOT NULL COMMENT '库存数量',
`start_time` timestamp NOT NULL COMMENT '秒杀开启时间',
`end_time`timestamp NOT NULL COMMENT '秒杀结束时间',
`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
PRIMARY KEY (seckill_id),
key idx_statrt_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)ENGINE =InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT ='秒杀库存表';

--初始化数据
insert into
    seckill (name ,number,start_time,end_time)
values
    ('1元秒杀苹果6',100,'2017-3-3 00:00:00','2017-3-7 00:00:00'),
    ('10元秒杀苹果6s',50,'2017-3-4 00:00:00','2017-3-7 00:00:00'),
    ('100元秒杀苹果7',100,'2017-3-5 00:00:00','2017-3-7 00:00:00'),
    ('1000元秒杀苹果7s',200,'2017-3-6 00:00:00','2017-3-7 00:00:00'),
    ('2000元秒杀苹果7sPlus',100,'2017-3-5 00:00:00','2017-3-8 00:00:00');

-- 秒杀成功明细表
--用户登录认证相关信息
create table success_killed(
`seckill_id` bigint NOT NULL COMMENT '商品库存id',
`user_phone` bigint NOT NULL COMMENT '用户手机号',
`state` tinyint NOT NULL DEFAULT -1 COMMENT '状态表示：-1：无效， 0：成功 ，1：已付款',
`create_time` timestamp NOT NULL COMMENT '创建时间',
PRIMARY KEY (seckill_id,user_phone),/*联合主键*/
key idx_create_time(create_time)
)ENGINE =InnoDB DEFAULT CHARSET=utf8 COMMENT ='秒杀成功明细表';
