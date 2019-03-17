package cn.sxuedu.dao;

import cn.sxuedu.pojo.Shopping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShoppingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shopping record);

    int insertSelective(Shopping record);

    Shopping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shopping record);

    int updateByPrimaryKey(Shopping record);

    /**
     * 添加地址
     * */
    int add(Shopping shopping);

    /**
     * 删除地址
     * */
    int delete(@Param("shoppingId") Integer shoppingId,
               @Param("userId") Integer userId);

    /**
     * 更新地址
     * */
    int update(Shopping shopping);

    /**
     * 查询地址
     * */
    Shopping selectByIdAndUserId(@Param("shoppingId") Integer shoppingId,
                                 @Param("userId") Integer userId);

    /**
     * 查询全部
     * */
    List<Shopping> selectAll(Integer userId);

}