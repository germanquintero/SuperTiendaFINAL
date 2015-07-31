package com.example.ga.supertienda;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Jose on 30/06/2015.
 */
public class MyPageAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments;

    public MyPageAdapter(FragmentManager fragmentManager, ArrayList<Fragment> listaFragmets){
        super (fragmentManager);
        this.fragments = listaFragmets;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
