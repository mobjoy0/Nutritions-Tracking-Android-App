package com.wess.makmouk.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.wess.makmouk.fragments.*;

import java.util.List;

public class PagerAdapter  extends FragmentStateAdapter {

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new TrackerFragment();
            case 2:
                return new ProfileFragment();
            default:
                return new HomeFragment(); // Default case
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }


}
