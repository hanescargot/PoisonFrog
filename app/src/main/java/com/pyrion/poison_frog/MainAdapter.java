package com.pyrion.poison_frog;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pyrion.poison_frog.center.FragmentCenter;
import com.pyrion.poison_frog.egg.FragmentEgg;
import com.pyrion.poison_frog.trade.FragmentTrade;

public class MainAdapter extends FragmentStateAdapter {

    Fragment[] fragments = new Fragment[3];

    public MainAdapter(FragmentActivity fragmentActivity){
        super(fragmentActivity);

        fragments[0] = new FragmentEgg();
        fragments[1] = new FragmentCenter();
        fragments[2] = new FragmentTrade();

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
