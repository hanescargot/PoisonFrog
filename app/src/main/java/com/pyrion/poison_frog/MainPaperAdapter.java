package com.pyrion.poison_frog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainPaperAdapter extends FragmentStateAdapter {

    Fragment[] fragments = new Fragment[2];

    public MainPaperAdapter(FragmentActivity fragmentActivity){
        super(fragmentActivity);

        fragments[0] = new MainCenterFragment();
        fragments[1] = new tradeCenterFragment();

    }
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }
}
