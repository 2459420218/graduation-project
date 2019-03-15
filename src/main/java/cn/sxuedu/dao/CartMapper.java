package cn.sxuedu.dao;

import cn.sxuedu.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);
    /**
     * 查询用户的购物信息
     * */

    List<Cart> findCartsByUserId(Integer userId);

    int updateByPrimaryKeySelectActive(Cart cart);

    /**
     * 判断购物车是否全选
     * @return  0:全选
     * */
    int isAllChecked(Integer userId);

    Cart findCartByProductUserId(@Param("productId") Integer productId,
                                 @Param("userId") Integer userId);

    /**
     * 批量删除
     * */
    int deleteByUserIdAndProductIds(@Param("productIdsList") List<String> productIdsList,
                                    @Param("userId") Integer userId);

    /**
     * 选中/取消
     * */
    int checkedOrUncheckedAllProduct(@Param("userId") Integer userId,
                                     @Param("productId") Integer productId,
                                     @Param("checked") Integer checked);

    /**
     * 查询购物车中商品数量
     * */
    int getCartProductCount(Integer userId);
}