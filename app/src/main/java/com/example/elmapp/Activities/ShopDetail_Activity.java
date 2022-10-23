package com.example.elmapp.Activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import com.bumptech.glide.Glide;
import com.example.elmapp.Adapter.MeanAdapter;
import com.example.elmapp.DataBean.FoodBean;
import com.example.elmapp.DataBean.ShopBean;
import com.example.elmapp.R;
import com.example.elmapp.TcpClient.Client;
import com.example.elmapp.TcpClient.OneStringCallable;
import com.example.elmapp.tools.mFiles;
import com.google.gson.Gson;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShopDetail_Activity extends AppCompatActivity implements View.OnClickListener {

    private static ShopBean shopBean;

    private TextView titlebar_back,titlebar_title,shop_dh_name,shop_dh_time,shop_dh_notice;//头部信息TextView
    private TextView car_foods_count,car_foods_price_tv,car_dc_tv;//购物车TextView组件
    private TextView shopping_carlist_clear_tv;//购物清单组件
    private ImageView shop_dh_img;
    private ImageView car;
    private Button car_settled_btn;
    private MeanAdapter meanAdapter;
    private ListView foodlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        Gson gson = new Gson();
        shopBean = gson.fromJson(getIntent().getStringExtra("shop"),ShopBean.class);
        viewInit();

    }

    public void viewInit(){
        //标题栏初始化
        titlebar_back = findViewById(R.id.tv_back);
        titlebar_title = findViewById(R.id.tv_title);
        titlebar_title.setText("店铺详情");
        titlebar_back.setOnClickListener(this);

        //头部信息初始化
        shop_dh_name = findViewById(R.id.shop_dh_name);shop_dh_name.setText(shopBean.getShopName());
        shop_dh_time = findViewById(R.id.shop_dh_time);shop_dh_time.setText(shopBean.getTime());
        shop_dh_notice = findViewById(R.id.shop_dh_notice);shop_dh_notice.setText(shopBean.getShopNotice());
        shop_dh_img = findViewById(R.id.shop_dh_img);
        Glide.with(this).load(mFiles.seachThisFile(shopBean.getShopPic())).into(shop_dh_img);
        Log.e("---------------",mFiles.seachThisFile(shopBean.getShopPic()));

        //菜品list初始化
        foodlist = findViewById(R.id.foods_list);
        meanAdapter = new MeanAdapter(this);
        meanAdapter.setData(shopBean.getFoodList());
        getFoodsImg();
        foodlist.setAdapter(meanAdapter);

        //购物车初始化
        car_foods_count = findViewById(R.id.car_foods_count);//已选购的菜品总数量
        car_foods_price_tv = findViewById(R.id.car_foods_price_tv);//菜品总价
        car_dc_tv = findViewById(R.id.car_dc_tv);//配送费
        car_settled_btn = findViewById(R.id.car_settled_btn);//结算按钮
        car = findViewById(R.id.car);//购物车图标
        shoppingCarRefresh();//刷新控件

        //购物清单初始化
        shopping_carlist_clear_tv = findViewById(R.id.shopping_carlist_clear_tv);
        Drawable icon = ResourcesCompat.getDrawable(getResources(),R.drawable.clear,null);
        if (icon!=null){
            icon.setBounds(0,0,40,40);
            shopping_carlist_clear_tv.setCompoundDrawables(icon,null,null,null);
        }

    }

    /*
    * 获取所有菜品图片
    * */
    private void getFoodsImg(){
        List<FoodBean> foodBeans = shopBean.getFoodList();
        for(FoodBean foodBean:foodBeans){
            Client.GetFile(foodBean.getFoodpic(),new getFoodImgCb());
        }
    }

    /*
    * 获取菜品图片的回调函数
    * */
    private class getFoodImgCb implements OneStringCallable{

        @Override
        public Object call(ArrayList strings) throws Exception {
            if(strings!=null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        meanAdapter.addFiles(new File((String) strings.get(0)));
                    }
                });

            }
            return null;
        }
    }

    public void shoppingCarRefresh(){
        BigDecimal lump_sum = new BigDecimal(0);//总价
        int count_sum = 0;//总菜品数量
        for(FoodBean foodBean:shopBean.getFoodList()){
            lump_sum = lump_sum.add(foodBean.getPrice().multiply(new BigDecimal(foodBean.getCount())));
            count_sum+=foodBean.getCount();
        }

        //如果总价大于0
        if(lump_sum.compareTo(new BigDecimal(0))==1){
            car_foods_count.setText(""+count_sum);//更新已选购的菜品总数量
            car_foods_count.setVisibility(View.VISIBLE);//将菜品总数量设为可见
            car_foods_price_tv.setText("￥ "+lump_sum.add(shopBean.getDistributionCost()));//更新菜品总价
            car_dc_tv.setText("另需配送费￥"+shopBean.getDistributionCost());//配送费更新
            car_dc_tv.setVisibility(View.VISIBLE);//配送费可见
            car_settled_btn.setText("去结算");
            car_settled_btn.setEnabled(true);//按钮可用
            car.setSelected(true);//购物车图标亮起
        }else {
            car_foods_count.setVisibility(View.INVISIBLE);//菜品总数量不可见
            car_foods_price_tv.setText("未选购商品");//菜品总价更新
            car_dc_tv.setVisibility(View.INVISIBLE);//配送费不可见
            car_settled_btn.setText("￥"+shopBean.getOfferPrice()+"起送");//结算按钮
            car_settled_btn.setEnabled(false);//设置按钮不可用
            car.setSelected(false);//购物车暗淡
        }
    }
    @Override
    public void onClick(View v) {

    }
}
