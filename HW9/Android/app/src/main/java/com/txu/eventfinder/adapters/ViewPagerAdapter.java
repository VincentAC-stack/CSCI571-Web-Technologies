package com.txu.eventfinder.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.txu.eventfinder.fragments.FavoritesFragment;
import com.txu.eventfinder.fragments.NavigationHost;
import com.txu.eventfinder.fragments.SearchFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new NavigationHost();
            case 1:
                return new FavoritesFragment();
            default:
                return new NavigationHost();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
