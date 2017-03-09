package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

/**
 * Created by Administrator on 2017/3/5.
 */
public interface SuccessKilledDao {

    /**
     * 鎻掑叆璐拱鏄庣粏锛屽彲杩囨护閲嶅
     * @param seckillId1
     * @param userphone
     * @return 鎻掑叆鐨勭粨鏋滈泦鏁伴噺锛堟彃鍏ョ殑琛屾暟锛�
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId,@Param("userPhone")long userPhone);

    /**
     * 鏍规嵁id鏌ヨ鏌ヨ SuccessKilled 骞舵惡甯�绉掓潃浜у搧瀵硅薄
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId,@Param("userPhone")long userPhone);
}
