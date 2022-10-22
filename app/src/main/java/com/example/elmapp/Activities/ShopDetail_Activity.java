package com.example.elmapp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
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
import java.util.ArrayList;
import java.util.List;

public class ShopDetail_Activity extends AppCompatActivity implements View.OnClickListener {

    private ShopBean shopBean;

    private TextView titlebar_back,titlebar_title,shop_dh_name,shop_dh_time,shop_dh_notice;
    private ImageView shop_dh_img;
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

    }

    private void getFoodsImg(){
        List<FoodBean> foodBeans = shopBean.getFoodList();
        for(FoodBean foodBean:foodBeans){
            Client.GetFile(foodBean.getFoodpic(),new getFoodImgCb());
        }
    }

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
    @Override
    public void onClick(View v) {

    }
}
