package cn.sxuedu.dao;

import cn.sxuedu.pojo.UserInfo;
import org.apache.ibatis.annotations.Param;

public interface UserInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);
    int updateBySelectedActive(UserInfo record);

    /**
     * 检查用户名
     * */
    int checkUsername(String username);

    /**
     * 检查邮箱
     * */
    int checkEmail(String email);

    /**
     * 根据用户名和密码来查询
     * */
    UserInfo selectLogin(@Param("username") String username,@Param("password") String password);

    /**
     * 根据用户名查询密保问题
     * */
    String selectQuestionByUsername(String username);


    /**
     * 校验用户密保答案
     * */
    int check_forget_answer(@Param("username") String username,@Param("question") String question,@Param("answer") String answer);

    /**
     * 根据用户名修改密码
     * */
    int updatePasswordByUsername(@Param("username") String username,@Param("password") String passwordNew);

    /**
     * 根据userId和passwordOld查询
     * */
    int selectCountByUserIdAndPasswordOld(@Param("userId") int userId,@Param("password") String passwordOld);


    /**
     *校验邮箱
     * */
    int checkEmailByUseridAndEmail(@Param("userId") int userId,@Param("newEmail") String email);

}