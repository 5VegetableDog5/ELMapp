package com.example.elmapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.elmapp.Activities.ShopDetail_Activity;
import com.example.elmapp.DataBean.FoodBean;
import com.example.elmapp.R;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCarListAdapter extends BaseAdapter {
    private Context context;
    private List<FoodBean> foodBeanList;
    public ShoppingCarListAdapter(Context context){
        this.context = context;
    }


    public void setData(List<FoodBean> foodBeanList){
        this.foodBeanList = foodBeanList;
        notifyDataSetChanged();
    }
    public void addData(FoodBean foodBean){
        if(foodBeanList==null) foodBeanList = new ArrayList<>();
        foodBeanList.add(foodBean);
        notifyDataSetChanged();
    }
    public void fresh(){
        if(foodBeanList!=null)
            notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        if(foodBeanList==null) return 0;
        int c = 0;
        for(FoodBean foodBean:foodBeanList){
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
                if(i == position) break;
                i++;
            }
        }
        return foodBeanList.get(i);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.shopping_car_list_item,null);
            vh.scli_foodname_tv = convertView.findViewById(R.id.scli_foodname_tv);
            vh.scli_foodprice_tv = convertView.findViewById(R.id.scli_foodprice_tv);
            vh.scli_foodcount_tv = convertView.findViewById(R.id.scli_foodcount_tv);
            vh.scli_add_btn = convertView.findViewById(R.id.scli_add_btn);
            vh.scli_reduce_btn = convertView.findViewById(R.id.scli_reduce_btn);
            convertView.setTag(vh);
        }else {
            vh = (ViewHolder) convertView.getTag();
        }

        final FoodBean foodBean = (FoodBean) getItem(position);
        if(foodBean!=null){
            vh.scli_foodname_tv.setText(foodBean.getFoodName());
            vh.scli_foodprice_tv.setText("￥"+foodBean.getPrice());
            vh.scli_foodcount_tv.setText(""+foodBean.getCount());

            vh.scli_add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    foodBean.setCount(foodBean.getCount()+1);
                    ShopDetail_Activity shopDetail_activity = (ShopDetail_Activity) context;
                    notifyDataSetChanged();
                    shopDetail_activity.shoppingCarRefresh();
                }
            });

            vh.scli_reduce_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(foodBean.getCount()>0){
                        foodBean.setCount(foodBean.getCount()-1);
                        ShopDetail_Activity shopDetail_activity = (ShopDetail_Activity) context;
                        notifyDataSetChanged();
                        shopDetail_activity.shoppingCarRefresh();
                    }

                }
            });
        }

        return convertView;


    }

    class ViewHolder{
        public TextView scli_foodname_tv,scli_foodprice_tv,scli_foodcount_tv;
        public ImageView scli_reduce_btn,scli_add_btn;
    }
}
