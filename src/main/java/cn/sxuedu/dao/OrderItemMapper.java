package cn.sxuedu.dao;

import cn.sxuedu.pojo.OrderItem;
import com.google.common.collect.Lists;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    /**
     * 订单批量插入
     * */
    int batchInsertOrderItem(List<OrderItem> orderItemList);

    /**
     * 根据userId和orderNo查询订单明细
     * */
    List<OrderItem> selectAllByUserIdAndOrderNo(@Param("userId") Integer userId,
                                                @Param("orderNo") Long orderNo);
}