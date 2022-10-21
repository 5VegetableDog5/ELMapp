package com.example.elmapp.DataBean;

import java.math.BigDecimal;
import java.util.List;

public class ShopBean {
    private int id;                         //店铺id
    private String shopName;                //店铺名称
    private int saleNum;                    //月销售量
    private BigDecimal offerPrice;          //起送价格
    private BigDecimal distributionCost;    //配送费
    private String feature;                 //店铺特色
    private String time;                    //配送时间
    private String shopPic;                 //店铺图片
    private String shopNotice;              //店铺公告
    private List<FoodBean> foodList;        //菜单

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(int saleNum) {
        this.saleNum = saleNum;
    }

    public void setDistributionCost(BigDecimal distributionCost) {
        this.distributionCost = distributionCost;
    }

    public BigDecimal getDistributionCost() {
        return distributionCost;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getFeature() {
        return feature;
    }

    public void setFoodList(List<FoodBean> foodList) {
        this.foodList = foodList;
    }

    public List<FoodBean> getFoodList(){
        return foodList;
    }

    public void setOfferPrice(BigDecimal offerPrice) {
        this.offerPrice = offerPrice;
    }

    public BigDecimal getOfferPrice() {
        return offerPrice;
    }

    public void setShopNotice(String shopNotice) {
        this.shopNotice = shopNotice;
    }

    public String getShopNotice() {
        return shopNotice;
    }


    public String getShopPic() {
        return shopPic;
    }

    public void setShopPic(String shopPic) {
        this.shopPic = shopPic;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
