package cn.sxuedu.controller.backend;


import cn.sxuedu.common.Const;
import cn.sxuedu.common.ResponseCode;
import cn.sxuedu.common.ServerResponse;
import cn.sxuedu.pojo.UserInfo;
import cn.sxuedu.service.ICategoryService;
import cn.sxuedu.service.IUserService;
import cn.sxuedu.service.impl.UserServiceImpl;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/manage/category")
public class CategoryManageController {

        @Autowired
        private IUserService userService;
        @Autowired
        private ICategoryService categoryService;
        /**
         * 添加节点
         * */
        @RequestMapping(value = "/add_category.do")
        public ServerResponse add_category(@RequestParam(required = false,defaultValue = "0") int parentId,
                                           String categoryName,
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
                return categoryService.addCategory(parentId,categoryName);
            }else {
                return ServerResponse.creatByError("用户无权操作");
            }

        }


        /**
         * 获取子节点
         * */
        @RequestMapping(value = "/get_category.do")

        public ServerResponse get_category(int categoryId,HttpSession session){
            UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
            if (userInfo==null){
                return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
            }
            //是否有管理员权限
            ServerResponse serverResponse=userService.checkUserAdmin(userInfo);
            if (serverResponse.isSuccess()){
                //有管理员权限
                return categoryService.getCategory(categoryId);
            }else {
                return ServerResponse.creatByError("用户无权操作");
            }

        }


    /**
     * 修改子节点
     * */
    @RequestMapping(value = "/set_category_name.do")

    public ServerResponse set_category_name(Integer categoryId,String categoryName,HttpSession session){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //是否有管理员权限
        ServerResponse serverResponse=userService.checkUserAdmin(userInfo);
        if (serverResponse.isSuccess()){
            //有管理员权限
            return categoryService.set_category_name(categoryId,categoryName);
        }else {
            return ServerResponse.creatByError("用户无权操作");
        }

    }


    /**
     * 递归查询后代节点
     * */
    @RequestMapping(value = "/get_deep_category.do")

    public ServerResponse get_deep_category(Integer categoryId,HttpSession session){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //是否有管理员权限
        ServerResponse serverResponse=userService.checkUserAdmin(userInfo);
        if (serverResponse.isSuccess()){
            //有管理员权限
            return categoryService.get_deep_category(categoryId);
        }else {
            return ServerResponse.creatByError("用户无权操作");
        }

    }

}
