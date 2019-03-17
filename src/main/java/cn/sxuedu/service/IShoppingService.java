package cn.sxuedu.service;


import cn.sxuedu.common.ServerResponse;
import cn.sxuedu.pojo.Shopping;
public interface IShoppingService {

    /**
     * 添加地址
     * */
    ServerResponse add(Integer userId, Shopping shopping);

    /**
     * 删除地址
     * */
    ServerResponse delete(Integer userId,Integer shoppingId);

    /**
     * 更新地址
     * */
    ServerResponse update(Integer userId,Shopping shopping);

    /**
     * 查询地址详情
     * */
    ServerResponse select(Integer userId,Integer shoppingId);

    /**
     * 分页查询
     * */
    ServerResponse list(Integer userId,Integer pageNo,Integer pageSize);
}
