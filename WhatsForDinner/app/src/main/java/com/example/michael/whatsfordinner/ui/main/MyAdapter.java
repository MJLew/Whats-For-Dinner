package com.example.michael.whatsfordinner.ui.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyAdapter extends FragmentPagerAdapter {
    private Context myContext;
    int totalTabs;

    public MyAdapter(Context context, FragmentManager fm, int totalTabs){
        super(fm);
        this.myContext = context;
        this.totalTabs = totalTabs;
    }


    public Fragment getItem(int position){
        switch (position){
            case 0:
                AddFragment addFragment = new AddFragment();
                return addFragment;
            case 1:
                DeleteFragment deleteFragment = new DeleteFragment();
                return deleteFragment;
            case 2:
                EditFragment editFragment = new EditFragment();
                return editFragment;
            case 3:
                FilterFragment filterFragment = new FilterFragment();
                return filterFragment;
            default:
                return null;
        }
    }

    public int getCount(){
        return totalTabs;
    }
}
