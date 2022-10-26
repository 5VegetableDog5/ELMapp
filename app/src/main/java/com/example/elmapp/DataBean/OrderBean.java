package com.example.elmapp.DataBean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class OrderBean {
    private String shopID;
    private List<FoodBean> foodBeans;
    private String customerAddress;
    private String startime;//yyyy-MM-dd HH:mm:ss
    private String endtime;//yyyy-MM-dd HH:mm:ss
    private BigDecimal distributionCost;
    public OrderBean(String shopID, List<FoodBean> foodBeans, String customerAddress, String startime, String endtime, BigDecimal distributionCost){
        this.shopID = shopID;
        this.foodBeans = foodBeans;
        this.customerAddress = customerAddress;
        this.startime = startime;
        this.endtime = endtime;
        this.distributionCost = distributionCost;
    }
    public OrderBean(){

    }

    public void setStartime(String time) {
        this.startime = time;
    }
    public String getStartime() {
        return startime;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setFoodBeans(List<FoodBean> foodBeans) {
        this.foodBeans = foodBeans;
    }

    public List<FoodBean> getFoodBeans() {
        return foodBeans;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getShopID() {
        return shopID;
    }

    public BigDecimal getDistributionCost() {
        return distributionCost;
    }

    public void setDistributionCost(BigDecimal distributionCost) {
        this.distributionCost = distributionCost;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }
}
