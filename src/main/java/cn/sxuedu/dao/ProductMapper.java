package cn.sxuedu.dao;

import cn.sxuedu.pojo.Category;
import cn.sxuedu.pojo.Product;
import cn.sxuedu.pojo.ProductWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ProductWithBLOBs record);

    int insertSelective(ProductWithBLOBs record);

    ProductWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProductWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(ProductWithBLOBs record);

    int updateByPrimaryKey(Product record);



    List<ProductWithBLOBs> selectAll();


    /**
     * 后台商品搜索
     * */
    List<ProductWithBLOBs> searchProducts(@Param("productId") Integer productId,@Param("productName") String productName);

    /**
     * 前台搜索商品
     * */
    List<ProductWithBLOBs> findProductByCategoryIdsAndKeyWord(@Param("categorySet") Set<Category> categoryIdSet,
                                                              @Param("keyword") String keyword);


}