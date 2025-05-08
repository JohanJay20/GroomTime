package com.example.groomtime;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.groomtime.fragments.AppointmentsFragment;
import com.example.groomtime.fragments.ArchiveFragment;

public class DashboardPagerAdapter extends FragmentStateAdapter {
    public DashboardPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new AppointmentsFragment() : new ArchiveFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
} 