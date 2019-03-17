package cn.sxuedu.dao;

import cn.sxuedu.pojo.Order;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.ibatis.annotations.Param;
import org.omg.CORBA.ORB;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order findOrderByUseridAndOrderNo(@Param("userId") Integer userId,
                                      @Param("orderNo") Long orderNo);

    List<Order> selectAllByUserId(Integer userId);

    /**
     * 根据userId和orderNo获取订单
     * */
    Order getOrderByUserIdAndOrderNo(@Param("userId") Integer userId,
                                     @Param("orderNo") Long orderNo);

    /**
     * 查询所有
     * */
    List<Order> selectAll();

    /**
     * 按照订单号查询订单
     * */
    Order selectOrderByOrderNo(Long orderNo);

}