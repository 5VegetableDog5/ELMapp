package com.example.elmapp.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.example.elmapp.Adapter.AdBannerAdapter;
import com.example.elmapp.Adapter.ShopAdapter;
import com.example.elmapp.DataBean.BannerBean;
import com.example.elmapp.DataBean.FoodBean;
import com.example.elmapp.DataBean.ShopBean;
import com.example.elmapp.R;
import com.example.elmapp.TcpClient.Client;
import com.example.elmapp.TcpClient.OneStringCallable;
import com.example.elmapp.views.ShopListView;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

public class Shops_Activity extends AppCompatActivity {

    ArrayList<String> bannerJSONS;
    ArrayList<BannerBean> bannerBeans;
    ArrayList<ShopBean> shopBeans;

    private ShopListView shopListView;
    static int bannerInt = 0;
    private AdBannerAdapter ada;//数据适配器

    private ShopAdapter shopAdapter;//店铺列表数据适配器
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        textView = new TextView(getApplicationContext());

        //获取shops
        shopListView = findViewById(R.id.slv_list);
        Client.getallShops( new getShopsCb());
        shopAdapter = new ShopAdapter(this);
        shopListView.setAdapter(shopAdapter);


        //获取banners
        ViewPager viewPager = findViewById(R.id.adVPager);
        Client.getallBanners(new getbannersCb());//向服务器获取banners资源
        viewPager.setLongClickable(false);
        ada = new AdBannerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(ada);

    }

    private class getbannersCb implements OneStringCallable {

        @Override
        public Object call(ArrayList BannerJSONS) throws Exception {
            if(BannerJSONS!=null)
            {
                bannerJSONS = BannerJSONS;
                pagerAdapterInit();
                Log.d("Banner","getBannerCb success");
            }
            else {
                Log.e("Banner","getBannerCb find BannerJSONS is null");
            }

            return null;
        }
    }

    private class getShopsCb implements OneStringCallable{

        @Override
        public Object call(ArrayList shopsJSONS) throws Exception {
            if(shopsJSONS!=null){
                shopsAdapterInit(shopsJSONS);
                Log.d("Shop","getShopsCb success");
            }else Log.e("Shop","getShopsCb find shopsJSONS is null");
            return null;
        }
    }

    //广告栏数据适配器初始化
    private void pagerAdapterInit(){
        Gson gson = new Gson();
        ArrayList<BannerBean> bannerBeans = new ArrayList<>();
        this.bannerBeans = bannerBeans;
        //将JSON解析为BannerBean
        for (String s: bannerJSONS) {
            bannerBeans.add(gson.fromJson(s, BannerBean.class));
        }
        for (BannerBean b: bannerBeans) {
            Client.GetFile(b.getImgurl(),new bannderReceiveFileCb());
        }
        ada.reflesh();
    }

    private void shopsAdapterInit(ArrayList<String> shopJSONS){
        Gson gson = new Gson();
        this.shopBeans = new ArrayList<>();
        //将JSON字符串解析为ShopJSONS
        for(String s:shopJSONS){
            shopBeans.add(gson.fromJson(s,ShopBean.class));
        }
        for(ShopBean s:shopBeans){
            Client.GetFile(s.getShopPic(),new shopsReceiveFileCb());
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                shopAdapter.setData(shopBeans);
            }
        });

    }

    private class bannderReceiveFileCb implements OneStringCallable{

        @Override
        public Object call(ArrayList strings) throws Exception {
            if(strings!=null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ada.addData(new File((String) strings.get(0)));
                    }
                });

                Log.d("File",(String) strings.get(0)+"----------------------------");
                Log.d("File","Shops_Activity File Cb success");
            }else Log.e("File","Shops_Activity File Cb error:strings is null "+String.valueOf(bannerInt));
            return null;
        }
    }

    private class shopsReceiveFileCb implements OneStringCallable{

        @Override
        public Object call(ArrayList strings) throws Exception {
            if(strings!=null){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shopAdapter.addImgs(new File((String) strings.get(0)));
                    }
                });
            }
            return null;
        }
    }

}