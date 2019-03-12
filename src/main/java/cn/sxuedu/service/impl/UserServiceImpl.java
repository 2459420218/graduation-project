package cn.sxuedu.service.impl;

import cn.sxuedu.common.Const;
import cn.sxuedu.common.ServerResponse;
import cn.sxuedu.dao.UserInfoMapper;
import cn.sxuedu.pojo.UserInfo;
import cn.sxuedu.service.IUserService;
import cn.sxuedu.utils.GuavaCacheUtil;
import cn.sxuedu.utils.MD5Utils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public ServerResponse login(String username, String password) {

        //step1：校验用户名
        int result=userInfoMapper.checkUsername(username);

        //step2：登录操作
        if(result>0){

            //todo MD5(password)
            password=MD5Utils.getMD5Code(password);
            //存在用户
            UserInfo userInfo=userInfoMapper.selectLogin(username,password);
            if (userInfo==null){
                //密码错误
                return ServerResponse.creatByError("密码错误");
            }else {
                userInfo.setPassword("");
                return ServerResponse.createBySuccess("success",userInfo);
            }
        }else{
            //用户名不存在
            return ServerResponse.creatByError("用户名不存在");
        }

    }


    @Override
    public ServerResponse register(UserInfo userInfo) {

        //校验用户名
        /*int result=userInfoMapper.checkUsername(userInfo.getUsername());
        if (result>0){
            //用户名存在
            return ServerResponse.creatByError("用户名已存在");
        }*/
        ServerResponse serverResponse=checkValid(userInfo.getUsername(),Const.USERNAME);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }
        //校验邮箱
        /*int email_result=userInfoMapper.checkEmail(userInfo.getEmail());
        if (email_result>0){
            //邮箱存在
            return ServerResponse.creatByError("邮箱存在");
        }*/
        ServerResponse serverResponse1=checkValid(userInfo.getEmail(),Const.EMAIL);
        if (!serverResponse1.isSuccess()){
            return serverResponse;
        }

        //密码MD5加密
        //加密算法：hash算法（不对称加密）-->md5，RAS

        userInfo.setPassword(MD5Utils.getMD5Code(userInfo.getPassword()));

        userInfo.setRole(Const.USERROLE.CUSTOMERUSER);//0是管理员，1为普通用户
        //将用户插入数据库
        int insert_result=userInfoMapper.insert(userInfo);
        if (insert_result>0){
            //注册成功
            return ServerResponse.createBySuccess("注册成功");
        }
        return ServerResponse.creatByError("注册失败");
    }


    @Override
    public ServerResponse checkValid(String str, String type) {
        if (type==null||type.equals("")){
            return ServerResponse.creatByError("参数必须传递");
        }
        if (str==null||str.equals("")){
            return ServerResponse.creatByError("参数必须传递");
        }
        if (type.equals(Const.USERNAME)){
            //校验用户名
            int username_result=userInfoMapper.checkUsername(str);
            if (username_result>0){
                return ServerResponse.creatByError("用户名已经存在");
            }
        }
        if (type.equals(Const.EMAIL)){
            //校验邮箱
            int email_result=userInfoMapper.checkEmail(str);
            if (email_result>0){
                return ServerResponse.creatByError("邮箱已经存在");
            }
        }


        return ServerResponse.createBySuccess("校验成功");
    }

    @Override
    public ServerResponse forget_get_question(String username) {
        //校验username
        if (username==null||username.equals("")){
            return ServerResponse.creatByError("用户名不能为空");
        }
        //校验用户名是否存在
        int username_result=userInfoMapper.checkUsername(username);
        if (username_result>0){
            //用户名存在
            //根据用户名查询密保问题
            String question=userInfoMapper.selectQuestionByUsername(username);
            if (question==null||question.equals("")){
                return ServerResponse.creatByError("未找到密保问题");
            }
                return ServerResponse.createBySuccess("成功",question);
        }



        return null;
    }

    @Override
    public ServerResponse forget_answer(String username, String question, String answer) {

        if (username==null||username.equals("")){
            return ServerResponse.creatByError("用户名必须传");
        }
        if (question==null||question.equals("")){
            return ServerResponse.creatByError("密保问题必须传");
        }
        if (answer==null||answer.equals("")){
            return ServerResponse.creatByError("答案必须传");
        }


        int result=userInfoMapper.check_forget_answer(username,question,answer);
        if (result>0){
            //答案正确
            String token= UUID.randomUUID().toString();
            //保存在服务端一份-->guava cache
            GuavaCacheUtil.put(username,token);
            String value=GuavaCacheUtil.get(username);
            return ServerResponse.createBySuccess("成功",token);

        }

        return null;
    }

    @Override
    public ServerResponse forget_reset_password(String username, String passwordNew, String forgetToken) {
        if (username==null||username.equals("")){
            return ServerResponse.creatByError("用户名必须传");
        }
        if (forgetToken==null||forgetToken.equals("")){
            return ServerResponse.creatByError("token必须传");
        }
        if (passwordNew==null||passwordNew.equals("")){
            return ServerResponse.creatByError("新密码必须传");
        }
        //校验用户名是否存在
        ServerResponse serverResponse=checkValid(username,Const.USERNAME);
        if (serverResponse.isSuccess()){
            //不存在
            return ServerResponse.creatByError("用户不存在");
        }

        //获取用户存在服务器的token
        String token=GuavaCacheUtil.get(username);
        if (token==null){
            return ServerResponse.creatByError("token不存在或者已过期");
        }
        //判断用户传递的token与服务器端保存的用户的token是否一致
        if (forgetToken.equals(token)){
            //修改密码
            passwordNew=MD5Utils.getMD5Code(passwordNew);
            int result=userInfoMapper.updatePasswordByUsername(username,passwordNew);
            if (result>0) {
                return ServerResponse.createBySuccess("修改成功");
            } else {
                return ServerResponse.creatByError("修改失败");
            }
        }else {
            return ServerResponse.creatByError("token错误，请重新获取");
        }


    }

    @Override
    public ServerResponse rest_password(String passwordOld, String passwordNew, UserInfo userInfo) {
        //step1.非空校验
        if (passwordOld==null||passwordOld.equals("")){
            return ServerResponse.creatByError("旧密码不能为空");
        }
        if (passwordNew==null||passwordNew.equals("")){
            return ServerResponse.creatByError("新密码不能为空");
        }

        //step2.根据userId和passwordOld校验，防止横向越权
        int result=userInfoMapper.selectCountByUserIdAndPasswordOld(userInfo.getId(),MD5Utils.getMD5Code(passwordOld));
            if (result>0){
                //说明用户和旧密码正确
                userInfo.setPassword(MD5Utils.getMD5Code(passwordNew));
                //step3.修改密码
                int update_result=userInfoMapper.updateBySelectedActive(userInfo);
                if (update_result>0){
                    //更新成功
                    return ServerResponse.createBySuccess("密码更新成功");
                }else {
                    return ServerResponse.creatByError("密码更新失败");
                }
            }else {
                return ServerResponse.creatByError("旧密码错误");
            }


    }

    @Override
    public ServerResponse updateUserInfo(UserInfo user) {
        //校验邮箱是否存在
        int result= userInfoMapper.checkEmailByUseridAndEmail(user.getId(),user.getEmail());
        if (result>0){
            //邮箱已存在
            return ServerResponse.creatByError("邮箱存在");
        }
        //更新用户信息
        int update_result=userInfoMapper.updateBySelectedActive(user);
        if (update_result>0){
            return ServerResponse.createBySuccess("更新成功");
        }else{
            return ServerResponse.creatByError("更新失败");
        }


    }


    //查询用户列表
    @Override
    public ServerResponse selectUserByPageNo(int pageNo, int pageSize) {
        //此处会先执行查询select count(1) from table
        PageHelper.startPage(pageNo,pageSize);
        List<UserInfo> userInfoList=userInfoMapper.selectAll();

        //分页模型
        PageInfo pageInfo=new PageInfo(userInfoList);
        return ServerResponse.createBySuccess("成功",pageInfo);
    }

    //判断 是否为管理员
    @Override
    public ServerResponse checkUserAdmin(UserInfo userInfo) {
        if (userInfo.getRole().intValue()==Const.USERROLE.ADMINUSER){

            return ServerResponse.createBySuccess();
        }
        return ServerResponse.creatByError();
    }


}
