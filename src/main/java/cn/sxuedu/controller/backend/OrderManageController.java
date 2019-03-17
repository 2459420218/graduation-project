package cn.sxuedu.controller.backend;

import cn.sxuedu.common.Const;
import cn.sxuedu.common.ResponseCode;
import cn.sxuedu.common.ServerResponse;
import cn.sxuedu.pojo.UserInfo;
import cn.sxuedu.service.IOrderService;
import cn.sxuedu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    IUserService userService;
    @Autowired
    IOrderService orderService;

    /**
     * 获取商订单列表
     */
    @RequestMapping(value = "/list.do")
    public ServerResponse list(HttpSession session,
                               @RequestParam(required = false, defaultValue = "1") Integer pageNo,
                               @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        //是否有管理员权限
        ServerResponse serverResponse = userService.checkUserAdmin(userInfo);
        if (!serverResponse.isSuccess()) {
            return ServerResponse.creatByError("用户无权操作");
        }
        return orderService.list(null, pageNo, pageSize);
    }

    /**
     * 订单明细
     */
    @RequestMapping(value = "/search.do")
    public ServerResponse search(HttpSession session, Long orderNo) {
        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        //是否有管理员权限
        ServerResponse serverResponse = userService.checkUserAdmin(userInfo);
        if (!serverResponse.isSuccess()) {
            return ServerResponse.creatByError("用户无权操作");
        }
        return orderService.search(orderNo);

    }



    /**
     * 订单发货
     */
    @RequestMapping(value = "/send.do")
    public ServerResponse send(HttpSession session, Long orderNo) {
        UserInfo userInfo = (UserInfo) session.getAttribute(Const.CURRENT_USER);
        if (userInfo == null) {
            return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        //是否有管理员权限
        ServerResponse serverResponse = userService.checkUserAdmin(userInfo);
        if (!serverResponse.isSuccess()) {
            return ServerResponse.creatByError("用户无权操作");
        }
        return orderService.send(orderNo);

    }
}
