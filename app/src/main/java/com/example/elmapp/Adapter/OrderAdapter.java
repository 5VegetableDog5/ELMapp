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
import com.example.elmapp.tools.mFiles;

import java.math.BigDecimal;
import java.util.List;

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private List<FoodBean> foodBeanList;
    public OrderAdapter(Context context){
        this.context = context;
    }

    public void setData(List<FoodBean> foodBeans){
        this.foodBeanList = foodBeans;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        if(foodBeanList ==null) return 0;
        int c = 0;
        for(FoodBean foodBean: foodBeanList){
            if(foodBean.getCount()>0)
                c++;
        }
        return c;
    }

    @Override
    public Object getItem(int position) {
        if(foodBeanList==null) return null;
        int i = 0;
        for(FoodBean foodBean:foodBeanList){
            if(foodBean.getCount()>0) {
                if(i == position) return foodBeanList.get(i);
            }
            i++;
        }
        return null;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.order_item,null);
            vh.oi_food_img = convertView.findViewById(R.id.oi_food_img);
            vh.oi_food_totalprice_tv = convertView.findViewById(R.id.oi_food_totalprice_tv);
            vh.oi_foodcount_tv = convertView.findViewById(R.id.oi_foodcount_tv);
            vh.oi_foodname_tv = convertView.findViewById(R.id.oi_foodname_tv);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }

        final FoodBean foodBean = (FoodBean) getItem(position);
        if(foodBean!=null){
            vh.oi_foodname_tv.setText(foodBean.getFoodName());
            vh.oi_food_totalprice_tv.setText("￥"+foodBean.getPrice().multiply(new BigDecimal(foodBean.getCount())));
            vh.oi_foodcount_tv.setText("x"+foodBean.getCount());
            Log.d("Order","x"+foodBean.getCount()+" "+foodBean.getFoodName());
            Glide.with(context).load(mFiles.seachThisFile(foodBean.getFoodpic())).into(vh.oi_food_img);
        }

        return convertView;
    }

    class ViewHolder{
        public TextView oi_foodname_tv,oi_foodcount_tv,oi_food_totalprice_tv;
        public ImageView oi_food_img;
    }
}
