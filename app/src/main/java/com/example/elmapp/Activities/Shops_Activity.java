package com.example.elmapp.Activities;

import android.os.Bundle;
import android.os.Looper;
import android.preference.EditTextPreference;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.example.elmapp.Adapter.AdBannerAdapter;
import com.example.elmapp.DataBean.BannerBean;
import com.example.elmapp.R;
import com.example.elmapp.TcpClient.Client;
import com.example.elmapp.TcpClient.OneStringCallable;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

public class Shops_Activity extends AppCompatActivity {

    ArrayList<String> bannerJSONS;
    ArrayList<BannerBean> bannerBeans;
    static int bannerInt = 0;
    private AdBannerAdapter ada;//数据适配器

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        textView = new TextView(getApplicationContext());

        ViewPager viewPager = findViewById(R.id.adVPager);
        Client.GetallBanners(new getbannersCb());//向服务器获取banners资源
        viewPager.setLongClickable(false);
        ada = new AdBannerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(ada);

        //TextView test;
        /*ImageView imageView = findViewById(R.id.bannerImage);
        File file = new File("data/data/com.example.elmapp/files/Old/banner1.png");
        if(file.exists())
            Glide.with(this).load(file).into(imageView);
        else Log.e("Image","file not exists");*/

    }

    private class getbannersCb implements OneStringCallable {

        @Override
        public Object call(ArrayList BannerJSONS) throws Exception {
            if(BannerJSONS!=null)
            {
                bannerJSONS = BannerJSONS;
                pagerAdapterInit();
                Log.d("Banner","BannerCb success");
            }
            else {
                Log.e("Banner","BannerCb find BannerJSONS is null");
            }

            return null;
        }
    }

    private void pagerAdapterInit(){
        Gson gson = new Gson();
        ArrayList<BannerBean> bannerBeans = new ArrayList<>();
        this.bannerBeans = bannerBeans;
        //将JSON解析为BannerBean
        for (String s: bannerJSONS) {
            bannerBeans.add(gson.fromJson(s, BannerBean.class));
        }
        for (BannerBean b: bannerBeans) {
            Client.GetFile(b.getImgurl(),new receiveFileCb());
        }
        ada.reflesh();
    }

    private class receiveFileCb implements OneStringCallable{

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

}