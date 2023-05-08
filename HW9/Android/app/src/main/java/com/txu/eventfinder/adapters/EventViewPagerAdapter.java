package com.txu.eventfinder.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.txu.eventfinder.fragments.EventArtistFragment;
import com.txu.eventfinder.fragments.EventDetailFragment;
import com.txu.eventfinder.fragments.EventVenueFragment;

public class EventViewPagerAdapter extends FragmentStateAdapter {
    public EventViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new EventDetailFragment();
            case 1:
                return new EventArtistFragment();
            case 2:
                return new EventVenueFragment();
            default:
                return new EventDetailFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
