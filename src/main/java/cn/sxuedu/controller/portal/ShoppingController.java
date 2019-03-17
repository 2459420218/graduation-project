package cn.sxuedu.controller.portal;

import cn.sxuedu.common.Const;
import cn.sxuedu.common.ResponseCode;
import cn.sxuedu.common.ServerResponse;
import cn.sxuedu.pojo.Shopping;
import cn.sxuedu.pojo.UserInfo;
import cn.sxuedu.service.IShoppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping(value = "/shopping")
public class ShoppingController {

    @Autowired
    IShoppingService shoppingService;
    /**
     * 添加地址
     * */
    @RequestMapping(value = "/add.do")
    public ServerResponse add(HttpSession session, Shopping shopping){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

        }

        return shoppingService.add(userInfo.getId(),shopping);
    }

    /**
     * 删除地址
     * */
    @RequestMapping(value = "/delete.do")
    public ServerResponse delete(HttpSession session, Integer shoppingId){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

        }

        return shoppingService.delete(userInfo.getId(),shoppingId);
    }

    /**
     * 更新地址
     * */
    @RequestMapping(value = "/update.do")
    public ServerResponse update(HttpSession session, Shopping shopping){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

        }

        return shoppingService.update(userInfo.getId(),shopping);
    }

    /**
     * 查看地址
     * */
    @RequestMapping(value = "/select.do")
    public ServerResponse select(HttpSession session, Integer shoppingId){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

        }

        return shoppingService.select(userInfo.getId(),shoppingId);
    }

    /**
     * 分页查看地址列表
     * */
    @RequestMapping(value = "/list.do")
    public ServerResponse list(HttpSession session,
                               @RequestParam(required = false,defaultValue = "1") Integer pageNo,
                               @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        UserInfo userInfo=(UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo==null){
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());

        }

        return shoppingService.list(userInfo.getId(),pageNo,pageSize);
    }

}