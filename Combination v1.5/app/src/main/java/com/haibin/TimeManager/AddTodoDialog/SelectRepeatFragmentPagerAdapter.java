package com.haibin.TimeManager.AddTodoDialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectRepeatFragmentPagerAdapter extends FragmentStateAdapter {
    List<Fragment> fragmentList;//用于存放实例化的fragment列表，同时在构造方法中初始化


    public SelectRepeatFragmentPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<Fragment> fragmentList) {
        super(fragmentManager, lifecycle);
        this.fragmentList=fragmentList;
    }

    public List<Fragment> GetFragments(){
        //获取fragmentlist
        return fragmentList;
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
