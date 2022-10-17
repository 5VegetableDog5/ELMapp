package com.example.elmapp.Adapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class mPagerAdapter extends PagerAdapter {

    private ArrayList<View> viewlist;

    public mPagerAdapter(ArrayList<View> viewlist)
    {
        super();
        this.viewlist = viewlist;
    }



    @Override
    public int getCount() {
        return viewlist.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewlist.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewlist.get(position));
        return viewlist.get(position);
    }

}
