package cn.sxuedu.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<I> implements Serializable {

    //状态码
    private int status;
    //数据
    private I data;
    //提示信息
    private String msg;

    private ServerResponse(int status){
        this.status=status;
    }

    private ServerResponse(int status,String msg){
        this.status=status;
        this.msg=msg;
    }

    private ServerResponse(int status,I data){
        this.status=status;
        this.data=data;
    }

    private ServerResponse(int status,I data,String msg){
        this.status=status;
        this.data=data;
        this.msg=msg;
    }

    //判断接口返回数据是否成功
    @JsonIgnore    //不在json序列化对象中
    public boolean isSuccess(){
        return this.status==ResponseCode.SUCCESS.getCode();
    }

    public static <I> ServerResponse<I> createBySuccess(){
        return new ServerResponse<I>(ResponseCode.SUCCESS.getCode());
    }

    public static <I> ServerResponse<I> createBySuccess(String msg){
        return new ServerResponse<I>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <I> ServerResponse<I> createBySuccess(I data){
        return new ServerResponse<I>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <I> ServerResponse<I> createBySuccess(String msg,I data){
        return new ServerResponse<I>(ResponseCode.SUCCESS.getCode(),data,msg);
    }


    //失败

    public static <I> ServerResponse<I> creatByError(){
        return new ServerResponse<I>(ResponseCode.ERROR.getCode());
    }

    public static <I> ServerResponse<I> creatByError(String msg){
        return new ServerResponse<I>(ResponseCode.ERROR.getCode(),msg);
    }

    public static <I> ServerResponse<I> creatByError(int status,String msg){
        return new ServerResponse<I>(status,msg);
    }

    public int getStatus() {
        return status;
    }

    public I getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }
}
