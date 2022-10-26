package com.example.elmapp.Activities;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.elmapp.Adapter.OrderAdapter;
import com.example.elmapp.DataBean.FoodBean;
import com.example.elmapp.DataBean.ShopBean;
import com.example.elmapp.R;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {

    private ShopBean shopBean;
    private List<FoodBean> foodBeans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Gson gson = new Gson();
        shopBean = gson.fromJson(getIntent().getStringExtra("shopbean"),ShopBean.class);
        foodBeans = shopBean.getFoodList();
        init();
    }

    private void init(){
        TextView title_bar = findViewById(R.id.tv_title);
        title_bar.setText("订单");
        TextView tv_back = findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);//标题栏初始化

        //订单列表数据适配器初始化
        OrderAdapter orderAdapter = new OrderAdapter(this);
        orderAdapter.setData(foodBeans);
        ListView orderlist = findViewById(R.id.food_list_order);
        orderlist.setAdapter(orderAdapter);

        TextView order_foods_price_tv = findViewById(R.id.order_foods_price_tv);
        order_foods_price_tv.setText("￥"+getFoodsPrice());//小计初始化

        TextView order_dcost_tv = findViewById(R.id.order_dcost_tv);
        order_dcost_tv.setText("￥"+shopBean.getDistributionCost());//配送费初始化

        TextView order_total_price_tv = findViewById(R.id.order_total_price_tv);
        order_total_price_tv.setText("￥"+getFoodsPrice().add(shopBean.getDistributionCost()));//总价初始化

        Button order_pay_btn = findViewById(R.id.order_pay_btn);
        order_pay_btn.setOnClickListener(this);
    }

    public BigDecimal getFoodsPrice(){
        BigDecimal foodsprice = new BigDecimal(0);
        for(FoodBean foodBean:foodBeans){
            foodsprice = foodsprice.add(foodBean.getPrice().multiply(new BigDecimal(foodBean.getCount())));
        }
        return foodsprice;
    }

    @Override
    public void onClick(View v) {

    }
}