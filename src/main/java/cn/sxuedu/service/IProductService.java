package cn.sxuedu.service;

import cn.sxuedu.common.ServerResponse;
import cn.sxuedu.pojo.Product;
import cn.sxuedu.pojo.ProductWithBLOBs;

public interface IProductService {

        /**
         * 更新或添加商品
         * */
         ServerResponse saveOrUpdate(ProductWithBLOBs product);

         /**
          * 产品上下架
          * */
         ServerResponse set_sale_status(Integer productId,Integer status);

         /**
          * 分页查询商品数据
          * */
         ServerResponse findProductByPage(Integer pageNo,Integer pageSize);


         /**
          * 产品详情
          * */
         ServerResponse findProductDetail(Integer productId);

         /**
          * 搜索商品
          * */
         ServerResponse searchProductsByProductIdOrProductName(Integer productId,String productName,Integer pageNo,Integer pageSize);


         /**
          * 前台商品搜索
          * */
         ServerResponse searchProduct(String keyword,Integer categoryId, Integer pageNo, Integer pageSize, String orderBy);

         /**
          * 前台获取商品详情
          * */
         ServerResponse productDetail(Integer productId);

}
