package cn.sxuedu.controller.portal;

import cn.sxuedu.common.Const;
import cn.sxuedu.common.ResponseCode;
import cn.sxuedu.common.ServerResponse;
import cn.sxuedu.pojo.UserInfo;
import cn.sxuedu.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/order")
public class OrderController {
    @Autowired
    private IOrderService orderService;
    /**
     * 创建订单
     * */
    @RequestMapping(value = "/create.do")
    public ServerResponse createOrder(HttpSession session,Integer shippingId){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

        }
        return orderService.createOrder(userInfo.getId(),shippingId);

    }

    /**
     * 获取购物车选中商品信息
     * */
    @RequestMapping(value = "/get_order_cart_product.do")
    public ServerResponse get_order_cart_product(HttpSession session){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

        }
        return orderService.get_order_cart_product(userInfo.getId());

    }

    /**
     * 获取商订单列表
     * */
    @RequestMapping(value = "/list.do")
    public ServerResponse list(HttpSession session,
                               @RequestParam(required = false,defaultValue = "1")Integer pageNo,
                               @RequestParam(required = false,defaultValue = "10")Integer pageSize){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

        }
        return orderService.list(userInfo.getId(),pageNo,pageSize);

    }

    /**
     * 订单详情
     * */
    @RequestMapping(value = "/detail.do")
    public ServerResponse detail(HttpSession session,Long orderNo){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

        }
        return orderService.detail(userInfo.getId(),orderNo);

    }

    /**
     * 取消订单
     * */
    @RequestMapping(value = "/cancel.do")
    public ServerResponse cancel(HttpSession session,Long orderNo){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

        }
        return orderService.cancel(userInfo.getId(),orderNo);

    }

}
