package cn.sxuedu.service.impl;

import cn.sxuedu.common.Const;
import cn.sxuedu.common.ServerResponse;
import cn.sxuedu.dao.CategoryMapper;
import cn.sxuedu.dao.ProductMapper;
import cn.sxuedu.pojo.Category;
import cn.sxuedu.pojo.Product;
import cn.sxuedu.pojo.ProductWithBLOBs;
import cn.sxuedu.service.ICategoryService;
import cn.sxuedu.service.IProductService;
import cn.sxuedu.utils.DateUtil;
import cn.sxuedu.utils.PropertiesUtil;
import cn.sxuedu.vo.ProductDetailVO;
import cn.sxuedu.vo.ProductListVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    CategoryMapper categoryMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    ICategoryService categoryService;
    /**
     * 商品添加与更新
     * */
    @Override
    public ServerResponse saveOrUpdate(ProductWithBLOBs product) {
       if (product==null){
           return ServerResponse.creatByError("参数错误");
       }
       //图片封装进sub_images 1.主图 2.jpg 3.jpg
       String subimages=product.getSubImages();
       if (subimages!=null&&subimages.equals("")){
           String[] subimagesArr=subimages.split(",");
           if (subimagesArr!=null&&subimagesArr.length>0){
               //为商品主图字段赋值
               product.setMainImage(subimagesArr[0]);
           }
       }
       //判断添加商品还是更新商品
        Integer productId=product.getId();
       if (productId==null){
           //添加商品
           int result=productMapper.insert(product);
           if (result>0){
               return ServerResponse.createBySuccess("商品添加成功");
           }else{
               return ServerResponse.creatByError("商品添加失败");
           }
       }else {
           int result=productMapper.updateByPrimaryKeyWithBLOBs(product);
           if (result>0){
               return ServerResponse.createBySuccess("商品更新成功");
           }else{
               return ServerResponse.creatByError("商品更新失败");
           }
       }

    }

    /**
     * 产品上下架
     * */
    @Override
    public ServerResponse set_sale_status(Integer productId, Integer status) {

        if (productId==null){
            return ServerResponse.creatByError("商品id必须传递");
        }
        if (status==null){
            return ServerResponse.creatByError("商品状态信息必须传");
        }
        ProductWithBLOBs product=new ProductWithBLOBs();
        product.setId(productId);
        product.setStatus(status);

        int result=productMapper.updateByPrimaryKeySelective(product);
        if (result>0){
            return ServerResponse.createBySuccess("修改产品成功");
        }

        return ServerResponse.creatByError("修改产品失败");
    }

    /**
     * 后台产品列表
     * */
    @Override
    public ServerResponse findProductByPage(Integer pageNo, Integer pageSize) {

        PageHelper.startPage(pageNo,pageSize);
        List<ProductWithBLOBs> productList=productMapper.selectAll();
        List<ProductListVO> productListVOList=new ArrayList<>();
        for (Product product:productList){
            ProductListVO productListVO=assembleProductListVO(product);
            productListVOList.add(productListVO);
        }

        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productListVOList);
        return ServerResponse.createBySuccess("成功",pageInfo);
    }
    /**
     * 方法封装
     * */
    private ProductListVO assembleProductListVO(Product product){
        ProductListVO productListVO=new ProductListVO();
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setId(product.getId());
        productListVO.setMainImage(product.getMainImage());
        productListVO.setName(product.getName());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus());
        productListVO.setSubtitle(product.getSubtitle());
        return productListVO;
    }

    /**
     * 后台产品详情
     * */
    @Override
    public ServerResponse findProductDetail(Integer productId) {
        if (productId==null){
            return ServerResponse.creatByError("商品id必须传");
        }
        ProductWithBLOBs product=productMapper.selectByPrimaryKey(productId);
        ProductDetailVO productDetailVO=assembleProductDetailVO(product);
        if (productDetailVO!=null){
            return ServerResponse.createBySuccess("成功",productDetailVO);
        }

        return ServerResponse.creatByError("商品不存在");
    }


    //封装
    private ProductDetailVO assembleProductDetailVO(ProductWithBLOBs product){
        if (product!=null){
            ProductDetailVO productDetailVO=new ProductDetailVO();

            productDetailVO.setCategoryId(product.getCategoryId());
            productDetailVO.setId(product.getId());
            productDetailVO.setDetail(product.getDetail());
            productDetailVO.setMainImage(product.getMainImage());
            productDetailVO.setPrice(product.getPrice());
            productDetailVO.setStock(product.getStock());
            productDetailVO.setSubImages(product.getSubImages());
            productDetailVO.setSubtitle(product.getSubtitle());
            productDetailVO.setName(product.getName());
            productDetailVO.setStatus(product.getStatus());

            Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
            if (category==null){
                productDetailVO.setParentCategoryId(0);
            }else {
                productDetailVO.setParentCategoryId(category.getParentId());
            }

            //imageHost
            productDetailVO.setImageHost ((String)PropertiesUtil.getProperty("imagehost"));
            productDetailVO.setCreateTime(DateUtil.dateToStr(product.getCreateTime()));
            productDetailVO.setUpdateTime(DateUtil.dateToStr(product.getUpdateTime()));
            return productDetailVO;
        }
        return null;
    }

    /**
     * 后台商品搜索
     * */
    @Override
    public ServerResponse searchProductsByProductIdOrProductName(Integer productId, String productName, Integer pageNo, Integer pageSize) {
        if (productName!=null&&!productName.equals("")){
            productName="%"+productName+"%";
        }
        PageHelper.startPage(pageNo,pageSize);
        List<ProductWithBLOBs> productList=productMapper.searchProducts(productId,productName);
        PageInfo pageInfo=new PageInfo(productList);
        List<ProductListVO> productListVOList=new ArrayList<>();
        for (Product product:productList){
            ProductListVO productListVO=assembleProductListVO(product);
            productListVOList.add(productListVO);
        }

        pageInfo.setList(productListVOList);
        return ServerResponse.createBySuccess("成功",pageInfo);
    }

    /**
     * 前台-产品搜索及排序
     * */
    @Override
    public ServerResponse searchProduct(String keyword, Integer categoryId, Integer pageNo, Integer pageSize, String orderBy) {

        //非空判断
        if (keyword==null&&categoryId==null){
            return ServerResponse.creatByError("参数错误");
        }
        Set<Category> categorySet=new HashSet<>();
        if (categoryId!=null){
            Category category=categoryMapper.selectByPrimaryKey(categoryId);
            if (category==null&&keyword==null){
                //没有商品
                PageHelper.startPage(pageNo,pageSize);
                List<ProductListVO> productListVOList=new ArrayList<>();
                PageInfo pageInfo=new PageInfo(productListVOList);
                return ServerResponse.createBySuccess("没有商品",pageInfo);
            }
            //递归查询后代节点

            categorySet=categoryService.findChildCategory(categorySet,categoryId);


        }
        if (keyword!=null&&!keyword.equals("")){
            keyword="%"+keyword+"%";
        }
        PageHelper.startPage(pageNo,pageSize);

        //产品排序
        if (orderBy!=null&&!orderBy.equals("")){
            String[] orderByArr=orderBy.split("_");
            if (orderByArr!=null&&orderByArr.length>1){
                String orderby_fileld=orderByArr[0];
                String sort=orderByArr[1];
                PageHelper.orderBy(orderby_fileld+" "+sort);
            }

        }
        List<ProductWithBLOBs> productList=productMapper.findProductByCategoryIdsAndKeyWord(categorySet,keyword);
        PageInfo pageInfo=new PageInfo(productList);
        List<ProductListVO> productListVOList=new ArrayList<>();
        for (ProductWithBLOBs product:productList){
            productListVOList.add(assembleProductListVO(product));
        }
        pageInfo.setList(productListVOList);


        return ServerResponse.createBySuccess("成功",pageInfo);
    }

    /**
     * 前台产品详情
     * */
    @Override
    public ServerResponse productDetail(Integer productId) {
        //step1:非空判断
        if (productId==null){
            return ServerResponse.creatByError("商品id必须传递");
        }
        //step2：根据productId去查新商品信息
        ProductWithBLOBs product=productMapper.selectByPrimaryKey(productId);
        if (product==null){
            return ServerResponse.creatByError("商品已下架");
        }
        if (product.getStatus()!= Const.ProductStatusEnum.PRODUCT_ONLLINE.getStatus()){
            return ServerResponse.creatByError("商品已下架");
        }

        ProductDetailVO productDetailVO=assembleProductDetailVO(product);

        return ServerResponse.createBySuccess(productDetailVO);
    }


}
