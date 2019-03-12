package cn.sxuedu.dao;

import cn.sxuedu.pojo.Category;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    /**
     * 查询子节点
     * */

    List<Category> findChildCategoryByCategoryId(int categoryId);

}