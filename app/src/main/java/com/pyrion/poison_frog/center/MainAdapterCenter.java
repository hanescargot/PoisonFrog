package com.pyrion.poison_frog.center;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pyrion.poison_frog.fight.MainFragmentFight;
import com.pyrion.poison_frog.trade.MainFragmentTrade;

public class MainAdapterCenter extends FragmentStateAdapter {

    Fragment[] fragments = new Fragment[3];

    public MainAdapterCenter(FragmentActivity fragmentActivity){
        super(fragmentActivity);

        fragments[0] = new MainFragmentFight();
        fragments[1] = new MainFragmentCenter();
        fragments[2] = new MainFragmentTrade();

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
