package cn.sxuedu.service;

import cn.sxuedu.common.ServerResponse;

public interface ICartService {

    /**
     *商品添加到购物车
     * */
    ServerResponse add(Integer userId,Integer productId,Integer count);

    /**
     *购物车列表
     * */
    ServerResponse list(Integer userId);

    /**
     * 更新购物车数量
     * */
    ServerResponse update(Integer userId,Integer productId,Integer count);

    /**
     * 删除购物车商品
     * */
    ServerResponse delete(Integer userId,String productIds);

    /**
     *全选或全不选
     * @param  userId
     * @param checked 1:选中  0：取消选中
     *                productId!=null  单选/取消单选
     * */
    ServerResponse selectOrUnSelectAll(Integer userId,Integer productId,Integer checked);

    /**
     *查询购物车中商品数量
     * */
    ServerResponse<Integer> get_cart_product_count(Integer userId);
}
