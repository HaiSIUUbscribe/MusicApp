package com.example.musicapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicapplication.fragments.AlbumsFragment;
import com.example.musicapplication.fragments.ProfileFragment;
import com.example.musicapplication.fragments.SongsFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new SongsFragment();
            case 1:
                return new AlbumsFragment();
            case 2:
                return new ProfileFragment();
            default:
                return new SongsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Songs, Albums, Profile
    }
}

