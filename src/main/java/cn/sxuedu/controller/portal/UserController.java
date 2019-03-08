package cn.sxuedu.controller.portal;


import cn.sxuedu.common.Const;
import cn.sxuedu.common.ResponseCode;
import cn.sxuedu.common.ServerResponse;
import cn.sxuedu.pojo.UserInfo;
import cn.sxuedu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    IUserService userService;

    @RequestMapping(value = "/login.do")
    public ServerResponse login(String username, String password, HttpSession session){

        ServerResponse serverResponse=userService.login(username,password);
        if (serverResponse.isSuccess()){
            //登录成功
            session.setAttribute(Const.CURRENT_USER,serverResponse.getData());
        }


        return serverResponse;

    }

    //注册
    @RequestMapping(value = "/register.do",method = RequestMethod.POST)
    public ServerResponse register(UserInfo userInfo){

        return userService.register(userInfo);

    }

    //检测用户名或邮箱是否有效
    @RequestMapping(value = "/check_valid.do")
    public ServerResponse checkValid(String str,String type){

        return userService.checkValid(str,type);
    }


    //获取登录状态下的用户信息
    @RequestMapping(value = "/get_user_info.do")

    public ServerResponse getUserInfo(HttpSession session){
        Object o=session.getAttribute(Const.CURRENT_USER);
        if (o==null){
            //用户未登录
            return ServerResponse.creatByError("用户未登录或已过期");
        }
        return ServerResponse.createBySuccess("成功",o);
    }


    //忘记密码——获取密保问题
    @RequestMapping(value = "/forget_get_question.do")
    public ServerResponse forget_get_question(String username){

        return userService.forget_get_question(username);
    }


    //提交问题答案
    @RequestMapping(value = "/forget_check_answer.do")
    public ServerResponse forget_answer(String username,String question,String answer){

        return userService.forget_answer(username,question,answer);
    }


    //忘记密码--重设密码
    @RequestMapping(value = "/forget_reset_password.do")
    public ServerResponse forget_reset_password(String username,String passwordNew,String forgetToken){


        return userService.forget_reset_password(username,passwordNew,forgetToken);
    }


    //登陆状态重置密码
    @RequestMapping(value = "/reset_password.do")
    public ServerResponse rest_password(String passwordOld,String passwordNew,HttpSession session){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            //用户未登录或登录过期,当前端收到status=10，就会跳转到登录页面
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        return userService.rest_password(passwordOld,passwordNew,userInfo);

    }

    //登录状态修改个人信息
    @RequestMapping(value = "/update_information.do")
    public ServerResponse updateUserInfo( UserInfo user,HttpSession session){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            //用户未登录或登录过期,当前端收到status=10，就会跳转到登录页面
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        user.setId(userInfo.getId());
        user.setUsername(userInfo.getUsername());
        user.setRole(Const.USERROLE.CUSTOMERUSER);

        return userService.updateUserInfo(user);
    }

    //获取当前登录用户的详细信息
    @RequestMapping(value = "/get_information.do")
    public ServerResponse get_information(HttpSession session){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            //用户未登录或登录过期,当前端收到status=10，就会跳转到登录页面
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createBySuccess("success",userInfo);
    }


    //退出登录
    @RequestMapping(value = "/logout.do")
    public ServerResponse logout(HttpSession session){

        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess("退出成功");

    }



}
