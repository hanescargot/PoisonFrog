package com.pyrion.poison_frog;

import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MainPaperAdapter extends FragmentStateAdapter {

    Fragment[] fragments = new Fragment[3];

    public MainPaperAdapter(FragmentActivity fragmentActivity){
        super(fragmentActivity);

        fragments[0] = new FightPageFragment();
        fragments[1] = new MainPageFragment();
        fragments[2] = new TradePageFragment();

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
