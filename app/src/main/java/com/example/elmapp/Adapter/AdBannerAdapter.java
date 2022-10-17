package com.example.elmapp.Adapter;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.elmapp.fragment.AdBannerFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdBannerAdapter extends FragmentPagerAdapter {

    private List<File> files;

    public AdBannerAdapter(FragmentManager fragmentManager){
        super(fragmentManager);
        files = new ArrayList<>();
    }

    public void setData(List<File> files){
        this.files = files;
        notifyDataSetChanged();//更新界面数据
    }
    public void addData(File file){
        files.add(file);
        notifyDataSetChanged();
        Log.d("AdBannerAdapter","add a file");
    }
    public void reflesh(){
        Log.d("AdBannerAdapter",String.format("file len:%d",files.size()));
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        if(files.size()>0){
            args.putSerializable("ad",files.get(position));
        }
        //返回一个Fragment
        return AdBannerFragment.newInstance(args);
    }

    @Override
    public int getCount() {
        return files.size();
    }

    public int getSize(){
        return files == null ? 0 : files.size()-1;
    }

    @Override
    public int getItemPosition(Object object){
        //防止刷新结果显示列表的时候出现缓存数据，重载这个函数，使之默认返回POSITION_NONE
        return POSITION_NONE;
    }
}
