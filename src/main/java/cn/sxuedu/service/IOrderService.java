package cn.sxuedu.service;

import cn.sxuedu.common.ServerResponse;
import org.apache.ibatis.annotations.Param;

public interface IOrderService {
    /**
     * 创建订单
     * */
    ServerResponse createOrder(Integer userId,Integer shippingId);

    /**
     * 获取购物车选中商品信息
     * */
    ServerResponse get_order_cart_product(Integer userId);

    /**
     * 前台-订单列表
     * */
    ServerResponse list(Integer userId,
                        @Param("pageNo") Integer pageNo,
                        @Param("pageSize") Integer pageSize);
    /**
     * 获取订单详情
     * */

    ServerResponse detail(Integer userId,Long orderNo);

    /**
     * 取消订单
     * */
    ServerResponse cancel(Integer userId,Long orderNo);

    /**
     * 后台--订单号查询
     * */
    ServerResponse search(Long orderNo);

    /**
     * 订单发货
     * */
    ServerResponse send(Long orderNo);
}
