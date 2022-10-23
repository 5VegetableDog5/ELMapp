package com.example.elmapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.elmapp.Activities.ShopDetail_Activity;
import com.example.elmapp.DataBean.FoodBean;
import com.example.elmapp.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MeanAdapter extends BaseAdapter {

    private Context context;
    private List<FoodBean> foodBeans;
    private List<File> files;
    public MeanAdapter(Context context){
        this.context = context;
    }

    public void setData(List<FoodBean> foodBeans){
        this.foodBeans = foodBeans;
        notifyDataSetChanged();
    }
    public void addFiles(File file){
        if(files==null) files = new ArrayList<>();
        files.add(file);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return foodBeans == null?0:foodBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return foodBeans == null? null:foodBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if(convertView == null){
            vh = new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(R.layout.food_item,null);
            vh.food_name_tv = convertView.findViewById(R.id.food_name_tv);
            vh.food_popularity_tv = convertView.findViewById(R.id.food_popularity_tv);
            vh.food_salenum_tv = convertView.findViewById(R.id.food_salenum_tv);
            vh.food_price_tv = convertView.findViewById(R.id.food_price_tv);
            vh.food_img = convertView.findViewById(R.id.food_img);
            vh.food_count_tv = convertView.findViewById(R.id.food_count_tv);
            vh.add_btn = convertView.findViewById(R.id.add_btn);
            vh.reduce_btn = convertView.findViewById(R.id.reduce_btn);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        final FoodBean bean = (FoodBean) getItem(position);
        if(bean != null){
            vh.food_name_tv.setText(bean.getFoodName());
            vh.food_popularity_tv.setText(bean.getPopularity());
            vh.food_salenum_tv.setText("月售"+bean.getSaleNum()+" 好评率100%");
            vh.food_price_tv.setText("￥"+bean.getPrice());
            vh.food_count_tv.setText(""+bean.getCount());//setText()不能传入int类型的数据，所以要加一个""让它转化为String

            if(files!=null && files.size()>position) {
                Glide.with(context).load(files.get(position)).into(vh.food_img);
                Log.d("Food","load imgs success");
            }

            //添加菜品至购物车按钮事件
            vh.add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bean.setCount(bean.getCount()+1);
                    notifyDataSetChanged();
                    ShopDetail_Activity shopDetail_activity = (ShopDetail_Activity) context;
                    shopDetail_activity.shoppingCarRefresh();
                }
            });

            //减少购买数量按钮事件
            vh.reduce_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bean.getCount()>0){
                        bean.setCount(bean.getCount()-1);
                        notifyDataSetChanged();
                        ShopDetail_Activity shopDetail_activity = (ShopDetail_Activity) context;
                        shopDetail_activity.shoppingCarRefresh();
                    }
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        public TextView food_name_tv, food_popularity_tv, food_salenum_tv, food_price_tv, food_count_tv;
        public ImageView food_img,reduce_btn,add_btn;
    }
}
