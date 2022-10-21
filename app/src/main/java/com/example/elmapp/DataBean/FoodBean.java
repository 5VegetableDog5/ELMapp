package com.example.elmapp.DataBean;

import java.math.BigDecimal;

public class FoodBean {
    private int foodId;
    private String foodName;
    private String popularity; //人气
    private int saleNum;
    private BigDecimal price;
    private int count;     //添加到购物车的数量
    private String foodpic;//菜品图片

    public void setFoodId(int foodId){
        this.foodId = foodId;
    }
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public void setSaleNum(int saleNum) {
        this.saleNum = saleNum;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setFoodpic(String foodpic) {
        this.foodpic = foodpic;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getFoodId() {
        return foodId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }

    public int getSaleNum() {
        return saleNum;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodpic() {
        return foodpic;
    }

    public String getPopularity() {
        return popularity;
    }

}
