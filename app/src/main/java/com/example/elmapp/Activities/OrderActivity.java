package com.example.elmapp.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.elmapp.Adapter.OrderAdapter;
import com.example.elmapp.DataBean.FoodBean;
import com.example.elmapp.DataBean.OrderBean;
import com.example.elmapp.DataBean.ShopBean;
import com.example.elmapp.R;
import com.example.elmapp.TcpClient.Client;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderActivity extends AppCompatActivity implements View.OnClickListener {

    private ShopBean shopBean;
    private List<FoodBean> foodBeans;

    EditText address_edit;
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
        order_pay_btn.setOnClickListener(this);//支付按钮初始化

        address_edit = findViewById(R.id.address_edit);//地址填写框初始化
    }

    public BigDecimal getFoodsPrice(){
        BigDecimal foodsprice = new BigDecimal(0);
        for(FoodBean foodBean:foodBeans){
            foodsprice = foodsprice.add(foodBean.getPrice().multiply(new BigDecimal(foodBean.getCount())));
        }
        return foodsprice;
    }

    public void payBtnCb(){
        /*new AlertDialog.Builder(Login_Activity.this).setTitle("错误")
                .setMessage("请输入正确的密码").setPositiveButton("确定",null)
                .create().show();
        Log.e("Login","密码输入错误");*/
        AlertDialog.Builder builder =  new AlertDialog.Builder(OrderActivity.this).setTitle("确认支付")
                .setMessage("是否支付该订单");
        builder.setNegativeButton("我再想想", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(address_edit.getText().toString().isEmpty()){//如果地址为空
                    Log.e("Order","address is null");
                } else {//地址不为空
                    Gson gson = new Gson();
                    OrderBean orderBean = new OrderBean();
                    List<FoodBean> foodBeans1 = new ArrayList<>();
                    for(FoodBean foodBean:shopBean.getFoodList()){
                        if(foodBean.getCount()>0){
                            foodBeans1.add(foodBean);
                            foodBean.setSaleNum(foodBean.getSaleNum()+foodBean.getCount());//本地数据更新
                            Log.e("Mean",foodBean.getFoodName()+" "+foodBean.getSaleNum());
                            shopBean.setSaleNum(shopBean.getSaleNum()+foodBean.getCount());//本地数据更新
                        }
                    }
                    orderBean.setShopID(String.valueOf(shopBean.getId()));
                    orderBean.setFoodBeans(foodBeans1);//订单购买的菜品列表
                    orderBean.setEndtime(null);
                    orderBean.setStartime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));//订单开始时间
                    orderBean.setCustomerAddress(address_edit.getText().toString());
                    orderBean.setDistributionCost(shopBean.getDistributionCost());
                    Log.d("Order",gson.toJson(orderBean));
                    Client.submitOrder(gson.toJson(orderBean));
                    dialog.dismiss();
                }

            }
        });

        builder.create().show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.order_pay_btn:
                payBtnCb();
                break;
        }
    }
}