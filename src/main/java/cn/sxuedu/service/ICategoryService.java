package cn.sxuedu.service;

import cn.sxuedu.common.ServerResponse;
import cn.sxuedu.pojo.Category;

import java.util.Set;


public interface ICategoryService {

   /**
    * 添加类别
    * */
   ServerResponse addCategory(int parentId,String categoryName);


   /**
    * 获取子节点
    * */
   ServerResponse getCategory(int categoryId);


   /**
    * 修改子节点
    * */
   ServerResponse set_category_name(Integer categoryId,String categoryName);

    /**
     * 递归查询子节点和后代节点
     * */

    ServerResponse get_deep_category(Integer categoryId);

    Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId);
}
