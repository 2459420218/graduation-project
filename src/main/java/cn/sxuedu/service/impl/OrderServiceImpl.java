package cn.sxuedu.service.impl;

import cn.sxuedu.common.Const;
import cn.sxuedu.common.ServerResponse;
import cn.sxuedu.dao.*;
import cn.sxuedu.pojo.*;
import cn.sxuedu.service.IOrderService;
import cn.sxuedu.utils.BigDecimalUtil;
import cn.sxuedu.utils.DateUtil;
import cn.sxuedu.utils.PropertiesUtil;
import cn.sxuedu.vo.OrderItemVO;
import cn.sxuedu.vo.OrderProductVO;
import cn.sxuedu.vo.OrderVO;
import cn.sxuedu.vo.ShippingVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    CartMapper cartMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    ShoppingMapper shoppingMapper;
    @Override
    public ServerResponse createOrder(Integer userId, Integer shippingId) {

        //1.参数校验
        if (shippingId==null){
            return ServerResponse.creatByError("地址必须选择");
        }

        //2.查询购物车中勾选的商品-->List<Cart>
        List<Cart> cartList=cartMapper.selectCheckedCartByUserId(userId);


        //3.List<Cart>-->List<OrderItem>
        ServerResponse serverResponse=getCartOrderItem(userId,cartList);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        //4.创建Order并保存订单
        List<OrderItem> orderItemList=(List<OrderItem>)serverResponse.getData();
        BigDecimal orderTotalPrice=getOrderTotalPrice(orderItemList);
        Order order=assembleOrder(userId,shippingId,orderTotalPrice);
        if (order==null){
            return ServerResponse.creatByError("生成订单失败");
        }
        //5.批量插入List<OrderItem>
        if (orderItemList==null||orderItemList.size()==0){
            return ServerResponse.creatByError("购物车中没有商品");
        }
        for (OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
          //订单明细批量插入mybatis
        orderItemMapper.batchInsertOrderItem(orderItemList);
        //6.减商品库存
        reduceProductStock(orderItemList);
        //7.清空已购买的购物车商品
        cleanCart(cartList);
        //8.返回OrderVO
        OrderVO orderVO=assembleOrderVO(order,orderItemList);


        return ServerResponse.createBySuccess(orderVO);
    }

    /**
     * 取消订单
     * */
    @Override
    public ServerResponse cancel(Integer userId, Long orderNo) {

        //1.参数非空校验
        if (orderNo==null){
            return ServerResponse.creatByError("参数不能为空");
        }
        //2.根据userId，orderNo查询订单
        Order order=orderMapper.findOrderByUseridAndOrderNo(userId,orderNo);
        if (order==null){
            return ServerResponse.creatByError("订单不存在");
        }
        //3.判断订单状态并取消
        if (order.getStatus()!=Const.OrderStatusEnum.ORDER_UN_PAY.getStatus()){
            return ServerResponse.creatByError("订单不可取消");
        }
        //4.返回结果
        order.setStatus(Const.OrderStatusEnum.ORDER_CANCELLED.getStatus());
        int result=orderMapper.updateByPrimaryKey(order);
        if (result>0){
            return ServerResponse.createBySuccess("取消订单成功");
        }
        return ServerResponse.creatByError("订单取消失败");
    }

    /**
     * 后台-按照订单号查询
     * */
    @Override
    public ServerResponse search(Long orderNo) {
        if (orderNo==null){
            return ServerResponse.creatByError("订单号必须传递");
        }
        Order order=orderMapper.selectOrderByOrderNo(orderNo);
        if (order==null){
            return ServerResponse.creatByError("订单不存在");
        }
        List<OrderItem> orderItemList=orderItemMapper.selectAllByUserIdAndOrderNo(order.getUserId(),orderNo);
        OrderVO orderVO=assembleOrderVO(order,orderItemList);
        return ServerResponse.createBySuccess(orderVO);
    }

    /**
     * 订单发货
     * */
    @Override
    public ServerResponse send(Long orderNo) {
        if (orderNo==null){
            return ServerResponse.creatByError("订单号必须传递");
        }
        Order order=orderMapper.selectOrderByOrderNo(orderNo);
        if (order==null){
            return ServerResponse.creatByError("订单不存在");
        }
        if (order.getStatus()==Const.OrderStatusEnum.ORDER_PAYED.getStatus()){
            order.setStatus(Const.OrderStatusEnum.ORDER_SEND.getStatus());
            order.setSendTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        return ServerResponse.creatByError();
    }


    /**
     * 获取购物车选中商品信息
     * */

    @Override
    public ServerResponse get_order_cart_product(Integer userId) {

        OrderProductVO orderProductVO=new OrderProductVO();
        //购物车中获取购物信息
        List<Cart> cartList=cartMapper.selectCheckedCartByUserId(userId);
        ServerResponse serverResponse=this.getCartOrderItem(userId,cartList);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        List<OrderItem> orderItemList=(List<OrderItem>)serverResponse.getData();
        List<OrderItemVO> orderItemVOList=Lists.newArrayList();
        BigDecimal totalPrice=new BigDecimal(0);
        for (OrderItem orderItem:orderItemList){
            totalPrice=BigDecimalUtil.add(totalPrice.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVOList.add(assembleOrderItemVO(orderItem));

        }
        orderProductVO.setImageHost((String)PropertiesUtil.getProperty("imagehost"));
        orderProductVO.setOrderItemVOList(orderItemVOList);
        orderProductVO.setProductTotalPrice(totalPrice);
        return ServerResponse.createBySuccess(orderProductVO);
    }

    /**
     * 获取商订单列表
     * */
    @Override
    public ServerResponse list(Integer userId, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<Order> orderList=null;
        if (userId==null){
            //管理员
            orderList=orderMapper.selectAll();
        }else {
            orderList=orderMapper.selectAllByUserId(userId);
        }
        List<OrderVO> orderVOList=Lists.newArrayList();
        for (Order order:orderList){
            if (order==null){
                return ServerResponse.creatByError("订单不存在");
            }
            List<OrderItem> orderItemList=orderItemMapper.selectAllByUserIdAndOrderNo(userId,order.getOrderNo());
            OrderVO orderVO=assembleOrderVO(order,orderItemList);
            orderVOList.add(orderVO);
        }


        PageInfo pageInfo=new PageInfo(orderVOList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 获取订单详情
     * */
    @Override
    public ServerResponse detail(Integer userId, Long orderNo) {
        if (orderNo==null){
            return ServerResponse.creatByError("订单号必须传递");
        }
        Order order=orderMapper.getOrderByUserIdAndOrderNo(userId,orderNo);
        if (order==null){
            return ServerResponse.creatByError("订单不存在");
        }
        List<OrderItem> orderItemList=orderItemMapper.selectAllByUserIdAndOrderNo(userId,orderNo);
        OrderVO orderVO=assembleOrderVO(order,orderItemList);


        return ServerResponse.createBySuccess(orderVO);
    }


    private OrderVO assembleOrderVO(Order order,List<OrderItem> orderItemList){

        OrderVO orderVO=new OrderVO();
        orderVO.setOrderNo(order.getOrderNo());
        orderVO.setPryment(order.getPayment());
        orderVO.setPaymentType(order.getPaymentType());
        orderVO.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getDesc());
        orderVO.setPostage(0);
        orderVO.setStatus(order.getStatus());
        orderVO.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getDesc());
        orderVO.setShippingId(order.getShippingId());
        Shopping shopping=shoppingMapper.selectByIdAndUserId(order.getShippingId(),order.getUserId());
        if (shopping!=null){
            orderVO.setReceiverName(shopping.getReceiverName());
            orderVO.setShippingVO(assembleShipping(shopping));
        }
        orderVO.setPaymentTime(DateUtil.dateToStr(order.getPaymentTime()));
        orderVO.setSendTime(DateUtil.dateToStr(order.getSendTime()));
        orderVO.setEndTime(DateUtil.dateToStr(order.getEndTime()));
        orderVO.setCreatTime(DateUtil.dateToStr(order.getCreateTime()));
        orderVO.setCloseTime(DateUtil.dateToStr(order.getCloseTime()));
        orderVO.setImageHost((String) PropertiesUtil.getProperty("imagehost"));

        List<OrderItemVO> orderItemVOList=Lists.newArrayList();
        if (orderItemList!=null){
            for (OrderItem orderItem:orderItemList){
            OrderItemVO orderItemVO=assembleOrderItemVO(orderItem);
            orderItemVOList.add(orderItemVO);
            }
        }
        orderVO.setOrderItemVOList(orderItemVOList);

        return orderVO;
    }

    /**
     * orderItem-->orderItemVO
     * */
    private OrderItemVO assembleOrderItemVO(OrderItem orderItem){
        OrderItemVO orderItemVO=new OrderItemVO();
        orderItemVO.setOrderNo(orderItem.getOrderNo());
        orderItemVO.setProductId(orderItem.getProductId());
        orderItemVO.setProductName(orderItem.getProductName());
        orderItemVO.setProductImage(orderItem.getProductImage());
        orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVO.setQuantity(orderItem.getQuantity());
        orderItemVO.setTotalPrice(orderItem.getTotalPrice());
        orderItemVO.setCerateTime(DateUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVO;
    }

    /**
     * shipping-->shippingVO
     * */
    private ShippingVO assembleShipping(Shopping shopping){
        ShippingVO shippingVO=new ShippingVO();
        shippingVO.setReceiverAddress(shopping.getReceiverAddress());
        shippingVO.setReceiverCity(shopping.getReceiverCity());
        shippingVO.setReceiverDistrict(shopping.getReceiverDistrict());
        shippingVO.setReceiverMobile(shopping.getReceiverMobile());
        shippingVO.setReceiverName(shopping.getReceiverName());
        shippingVO.setReceiverPhone(shopping.getReceiverPhone());
        shippingVO.setReceiverProvince(shopping.getReceiverProvince());
        shippingVO.setReceiverZip(shopping.getReceiverZip());
        return shippingVO;
    }

    /**
     * 清空购物车
     * */
    private void cleanCart(List<Cart> cartList){
        for (Cart cart:cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    /**
     * 商品减少库存
     * */
    private void reduceProductStock(List<OrderItem> orderItemList){
        for (OrderItem orderItem:orderItemList){
            ProductWithBLOBs product=productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    /**
     * 生成订单
     * */
    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal payment){
        Order order=new Order();
        order.setOrderNo(generateOrderNo());
        order.setStatus(Const.OrderStatusEnum.ORDER_UN_PAY.getStatus());
        order.setPostage(0);
        order.setPayment(payment);
        order.setPaymentType(Const.PaymentTypeEnum.PAY_ON_LINE.getStatus());
        order.setUserId(userId);
        order.setShippingId(shippingId);
        //订单支付时间 发货时间
        int result=orderMapper.insert(order);
        if (result>0){
            return order;
        }
        return null;
    }
    /**
     * 生成订单号
     * */
    private Long generateOrderNo(){
        long currentTime=System.currentTimeMillis();
        return currentTime+new Random().nextInt(100);
    }

    /**
     * 计算订单的价格
     * */
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
        BigDecimal totalPrice=new BigDecimal("0");
        for (OrderItem orderItem:orderItemList){
            totalPrice=BigDecimalUtil.add(totalPrice.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return totalPrice;
    }

    /**
     * 将购物车的购物信息转成订单明细信息
     * */
    private ServerResponse<List<OrderItem>> getCartOrderItem(Integer userId,List<Cart> cartList){
        List<OrderItem> orderItemList= Lists.newArrayList();
        if (cartList==null||cartList.size()==0){
            //购物车空
            return ServerResponse.creatByError("购物车空");
        }
        for (Cart cartItem:cartList){
            OrderItem orderItem=new OrderItem();
            ProductWithBLOBs product=productMapper.selectByPrimaryKey(cartItem.getProductId());
            if (product==null){
                return ServerResponse.creatByError("商品不存在");
            }
            Integer productStatus=product.getStatus();
            if (productStatus!= Const.ProductStatusEnum.PRODUCT_ONLLINE.getStatus()){

                //商品下架
                return ServerResponse.creatByError("商品"+product.getName()+"已经被下架");
            }
            Integer productStock=product.getStock();
            if (productStock<cartItem.getQuantity()){
                //商品库存不足
                return ServerResponse.creatByError("商品"+product.getName()+"库存不足");
            }
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity().doubleValue()));
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);

    }
}
