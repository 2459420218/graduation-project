package cn.sxuedu.controller.backend;


import cn.sxuedu.common.Const;
import cn.sxuedu.common.ResponseCode;
import cn.sxuedu.common.ServerResponse;
import cn.sxuedu.pojo.Product;
import cn.sxuedu.pojo.ProductWithBLOBs;
import cn.sxuedu.pojo.UserInfo;
import cn.sxuedu.service.IProductService;
import cn.sxuedu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/manage/product")
public class ProductManageController {

        /**
         * 新增或者更新产品接口
         * */
        @Autowired
        IUserService userService;
        @Autowired
        IProductService productService;
        @RequestMapping(value = "/save.do")
        public ServerResponse saveOrUpdate(ProductWithBLOBs product, HttpSession session){

            //用户是否登录
            UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
            if (userInfo==null){
                return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            //是否有管理员权限
            ServerResponse serverResponse=userService.checkUserAdmin(userInfo);
            if (serverResponse.isSuccess()){
                //有管理员权限
                return productService.saveOrUpdate(product);
            }else {
                return ServerResponse.creatByError("用户无权操作");
            }

        }


        /**
         * 产品上下架
         * */
        @RequestMapping(value = "/set_sale_status.do")
        public ServerResponse set_sale_status(Integer productId,Integer status, HttpSession session){

            //用户是否登录
            UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
            if (userInfo==null){
                return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            //是否有管理员权限
            ServerResponse serverResponse=userService.checkUserAdmin(userInfo);
            if (serverResponse.isSuccess()){
                //有管理员权限
                return productService.set_sale_status(productId,status);
            }else {
                return ServerResponse.creatByError("用户无权操作");
            }

        }

    /**
     * 产品列表
     * */
    @RequestMapping(value = "/list.do")
    public ServerResponse List(@RequestParam(required = false,defaultValue = "1") Integer pageNo,
                               @RequestParam(required = false,defaultValue = "10") Integer pageSize,
                               HttpSession session){

        //用户是否登录
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //是否有管理员权限
        ServerResponse serverResponse=userService.checkUserAdmin(userInfo);
        if (serverResponse.isSuccess()){
            //有管理员权限
            return productService.findProductByPage(pageNo,pageSize);
        }else {
            return ServerResponse.creatByError("用户无权操作");
        }

    }

    /**
     * 产品详情
     * */
    @RequestMapping(value = "/detail.do")
    public ServerResponse detail(Integer productId,
                               HttpSession session){

        //用户是否登录
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //是否有管理员权限
        ServerResponse serverResponse=userService.checkUserAdmin(userInfo);
        if (serverResponse.isSuccess()){
            //有管理员权限
            return productService.findProductDetail(productId);
        }else {
            return ServerResponse.creatByError("用户无权操作");
        }

    }

    /**
     * 产品搜索
     * */
    @RequestMapping(value = "/search.do")
    public ServerResponse search(@RequestParam(required = false) String productName,
                                 @RequestParam(required = false)Integer productId,
                                 @RequestParam(required = false,defaultValue = "1")Integer pageNo,
                                 @RequestParam(required = false,defaultValue = "10")Integer pageSize,
                                 HttpSession session){

        //用户是否登录
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //是否有管理员权限
        ServerResponse serverResponse=userService.checkUserAdmin(userInfo);
        if (serverResponse.isSuccess()){
            //有管理员权限
            return productService.searchProductsByProductIdOrProductName(productId,productName,pageNo,pageSize);
        }else {
            return ServerResponse.creatByError("用户无权操作");
        }

    }

}
