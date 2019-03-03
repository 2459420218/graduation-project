package cn.sxuedu.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class ProductWithBLOBs extends Product {
    private String subImages;

    private String detail;

    public ProductWithBLOBs(Integer id, Integer categoryId, String name, String subtitle, String mainImage, BigDecimal price, Integer stock, Integer status, Date createTime, Date updateTime, String subImages, String detail) {
        super(id, categoryId, name, subtitle, mainImage, price, stock, status, createTime, updateTime);
        this.subImages = subImages;
        this.detail = detail;
    }

    public ProductWithBLOBs() {
        super();
    }

    public String getSubImages() {
        return subImages;
    }

    public void setSubImages(String subImages) {
        this.subImages = subImages == null ? null : subImages.trim();
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
    }
}