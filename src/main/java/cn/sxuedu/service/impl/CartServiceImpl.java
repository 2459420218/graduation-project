package cn.sxuedu.service.impl;

import cn.sxuedu.common.Const;
import cn.sxuedu.common.ServerResponse;
import cn.sxuedu.dao.CartMapper;
import cn.sxuedu.dao.ProductMapper;
import cn.sxuedu.pojo.Cart;
import cn.sxuedu.pojo.Product;
import cn.sxuedu.pojo.ProductWithBLOBs;
import cn.sxuedu.service.ICartService;
import cn.sxuedu.utils.BigDecimalUtil;
import cn.sxuedu.utils.PropertiesUtil;
import cn.sxuedu.vo.CartProductVO;
import cn.sxuedu.vo.CartVO;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Spliterator;

@Service
public class CartServiceImpl implements ICartService {


        @Autowired
        private CartMapper cartMapper;
        @Autowired
        private ProductMapper productMapper;
        /**
         * 获取购物车高可用对象CartVO
         * */
        private CartVO getCartVOLimit(Integer userId){

            CartVO cartVO=new CartVO();
            //1.userId-->List<Cart>
            List<Cart> cartList=cartMapper.findCartsByUserId(userId);

            //2.List<Cart>-->List<CartProductVO>
            List<CartProductVO> cartProductVOList= Lists.newArrayList();
            BigDecimal totalPrice=new BigDecimal("0");
            if (cartList!=null){
                for (Cart cart:cartList){
                    //cart-->cartProductVO
                    CartProductVO cartProductVO=new CartProductVO();
                    cartProductVO.setId(cart.getId());
                    cartProductVO.setUserId(userId);
                    cartProductVO.setProductId(cart.getProductId());
                    //根据productId-->product
                    ProductWithBLOBs product=productMapper.selectByPrimaryKey(cart.getProductId());
                    if (product!=null){
                        cartProductVO.setProductName(product.getName());
                        cartProductVO.setProductSubtitle(product.getSubtitle());
                        cartProductVO.setProductPrice(product.getPrice());
                        cartProductVO.setProductStatus(product.getStatus());
                        cartProductVO.setProductStock(product.getStock());
                        cartProductVO.setProductChecked(cart.getChecked());

                        //判断库存
                        int buyLimitCount=0;
                        if (product.getStock()>cart.getQuantity()){
                            //库存充足
                            cartProductVO.setLimitQuantity(Const.Cart.LIMIT_MUM_SUCCESS);
                            buyLimitCount=cart.getQuantity();
                        }else {
                            //库存不足
                            cartProductVO.setLimitQuantity(Const.Cart.LIMIT_MUM_FALL);
                            buyLimitCount=product.getStock();
                            Cart cart1=new Cart();
                            cart1.setId(cart.getId());
                            cart1.setQuantity(product.getStock());
                            cartMapper.updateByPrimaryKeySelective(cart1);
                        }

                        cartProductVO.setQuantity(buyLimitCount);
                        BigDecimal cartProductTotalPrice=BigDecimalUtil.mul(product.getPrice().doubleValue(),cart.getQuantity().doubleValue());
                        cartProductVO.setProductTotalPrice(cartProductTotalPrice);
                        if (cart.getChecked()==Const.Cart.CHECKED){
                        totalPrice=BigDecimalUtil.add(totalPrice.doubleValue(),cartProductTotalPrice.doubleValue());
                        }
                    }

                    cartProductVOList.add(cartProductVO);
                }
            }
            //3.组装cartVO

            cartVO.setCartProductVOList(cartProductVOList);
            cartVO.setAllChecked(cartMapper.isAllChecked(userId)==0);
            cartVO.setCartTotalPrice(totalPrice);
            cartVO.setImageHost(PropertiesUtil.getProperty("imagehost").toString());
            return cartVO;
        }

        /**
         * 商品添加到购物车
         * */
    @Override
    public ServerResponse add(Integer userId, Integer productId, Integer count) {
        //1.非空判断
        if (productId==null||count==null){
            return ServerResponse.creatByError("参数错误");
        }
        //2.根据productId查询cart
        Cart cart=cartMapper.findCartByProductUserId(productId,userId);
        if (cart==null){
            //购物车中没有该商品
            Cart cart1=new Cart();
            cart1.setQuantity(count);
            cart1.setChecked(Const.Cart.CHECKED);
            cart1.setProductId(productId);
            cart1.setUserId(userId);
            cartMapper.insert(cart1);
        }else {
            //购物车已经存在该商品，需要更新商品数量
            cart.setQuantity(cart.getQuantity()+count);
            cartMapper.updateByPrimaryKeySelectActive(cart);
        }
        CartVO cartVO=getCartVOLimit(userId);

        return ServerResponse.createBySuccess(cartVO);
    }

    /**
     * 购物车列表
     * */
    @Override
    public ServerResponse list(Integer userId) {

       CartVO cartVO=getCartVOLimit(userId);
        return ServerResponse.createBySuccess(cartVO);
    }

    /**
     * 更改购物车中商品数量
     * */
    @Override
    public ServerResponse update(Integer userId, Integer productId, Integer count) {
        //1.非空判断
        if (productId==null||count==null){
            return ServerResponse.creatByError("参数错误");
        }
        Cart cart=cartMapper.findCartByProductUserId(productId,userId);
        if (cart!=null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelectActive(cart);
        CartVO cartVO=this.getCartVOLimit(userId);
        return ServerResponse.createBySuccess(cartVO);
    }

    /**
     * 删除商品
     * */
    @Override
    public ServerResponse delete(Integer userId, String productIds) {
        //1.非空判断
        if (productIds==null||productIds.equals("")){
            return ServerResponse.creatByError("参数错误");
        }
        List<String> productIdsList= Splitter.on(",").splitToList(productIds);
        if (productIdsList==null||productIdsList.size()<=0){
            return ServerResponse.creatByError("参数错误");
        }
        cartMapper.deleteByUserIdAndProductIds(productIdsList,userId);
        CartVO cartVO=getCartVOLimit(userId);
        return ServerResponse.createBySuccess(cartVO);
    }

    @Override
    public ServerResponse selectOrUnSelectAll(Integer userId,Integer productId, Integer checked) {
        cartMapper.checkedOrUncheckedAllProduct(userId,productId,checked);
        CartVO cartVO=getCartVOLimit(userId);

        return ServerResponse.createBySuccess(cartVO);
    }

    @Override
    public ServerResponse<Integer> get_cart_product_count(Integer userId) {
        int count=cartMapper.getCartProductCount(userId);

        return ServerResponse.createBySuccess(count);
    }
}
