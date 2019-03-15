package cn.sxuedu.utils;

import java.math.BigDecimal;

/**
 * 价格运算工具
 * */
public class BigDecimalUtil {

    /**
     * 加法
     * */
    public static BigDecimal add(Double v1,Double v2){
        BigDecimal bigDecimal1=new BigDecimal(v1.toString());
        BigDecimal bigDecimal2=new BigDecimal(v2.toString());
        return bigDecimal1.add(bigDecimal2);

    }

    /**
     * 减法
     * */
    public static BigDecimal sub(Double v1,Double v2){
        BigDecimal bigDecimal1=new BigDecimal(v1.toString());
        BigDecimal bigDecimal2=new BigDecimal(v2.toString());
        return bigDecimal1.subtract(bigDecimal2);

    }

    /**
     * 乘法
     * */
    public static BigDecimal mul(Double v1,Double v2){
        BigDecimal bigDecimal1=new BigDecimal(v1.toString());
        BigDecimal bigDecimal2=new BigDecimal(v2.toString());
        return bigDecimal1.multiply(bigDecimal2);

    }

    /**
     * 除法
     * */
    public static BigDecimal divi(Double v1,Double v2){
        BigDecimal bigDecimal1=new BigDecimal(v1.toString());
        BigDecimal bigDecimal2=new BigDecimal(v2.toString());
        //小数点后两位，四舍五入
        return bigDecimal1.divide(bigDecimal2,2,BigDecimal.ROUND_HALF_UP);

    }

}

