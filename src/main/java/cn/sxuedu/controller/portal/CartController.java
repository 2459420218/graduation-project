package cn.sxuedu.controller.portal;

import cn.sxuedu.common.Const;
import cn.sxuedu.common.ResponseCode;
import cn.sxuedu.common.ServerResponse;
import cn.sxuedu.pojo.UserInfo;
import cn.sxuedu.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/cart")
public class CartController {

    @Autowired
    private ICartService cartService;

    /**
     * 购物车添加商品
     */
    @RequestMapping(value = "/add.do")
    public ServerResponse add(Integer productId, Integer count, HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            //用户未登录或登录过期,当前端收到status=10，就会跳转到登录页面
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return cartService.add(userInfo.getId(), productId, count);
    }

    /**
     * 购物车列表
     */
    @RequestMapping(value = "/list.do")
    public ServerResponse list(HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            //用户未登录或登录过期,当前端收到status=10，就会跳转到登录页面
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return cartService.list(userInfo.getId());
    }

    /**
     * 更新购物车中商品数量
     */
    @RequestMapping(value = "/update.do")
    public ServerResponse update(HttpSession session, Integer productId, Integer count) {
        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            //用户未登录或登录过期,当前端收到status=10，就会跳转到登录页面
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        return cartService.update(userInfo.getId(), productId, count);
    }

    /**
     * 删除购物车中商品
     */
    @RequestMapping(value = "/delete_product.do")
    public ServerResponse delete(HttpSession session, String productIds) {
        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            //用户未登录或登录过期,当前端收到status=10，就会跳转到登录页面
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.delete(userInfo.getId(), productIds);

    }

    /**
     * 购物车全选
     */
    @RequestMapping(value = "/select_all.do")
    public ServerResponse select_all(HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            //用户未登录或登录过期,当前端收到status=10，就会跳转到登录页面
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.selectOrUnSelectAll(userInfo.getId(),null,Const.Cart.CHECKED);

    }

    /**
     * 购物车全不选
     */
    @RequestMapping(value = "/un_select_all.do")
    public ServerResponse un_select_all(HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            //用户未登录或登录过期,当前端收到status=10，就会跳转到登录页面
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.selectOrUnSelectAll(userInfo.getId(),null,Const.Cart.UNCHECKED);

    }

    /**
     * 购物车单选
     */
    @RequestMapping(value = "/select.do")
    public ServerResponse select(HttpSession session,Integer productId) {
        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            //用户未登录或登录过期,当前端收到status=10，就会跳转到登录页面
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.selectOrUnSelectAll(userInfo.getId(),productId,Const.Cart.CHECKED);

    }

    /**
     * 购物车取消单选
     */
    @RequestMapping(value = "/un_select.do")
    public ServerResponse un_select(HttpSession session,Integer productId) {
        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            //用户未登录或登录过期,当前端收到status=10，就会跳转到登录页面
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return cartService.selectOrUnSelectAll(userInfo.getId(),productId,Const.Cart.UNCHECKED);

    }

    /**
     * 查询购物车中商品数量
     */
    @RequestMapping(value = "/get_cart_product_count.do")
    public ServerResponse<Integer> get_cart_product_count(HttpSession session) {
        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            //用户未登
            return ServerResponse.createBySuccess(0);
        }
        return cartService.get_cart_product_count(userInfo.getId());

    }


}


