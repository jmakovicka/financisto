package ru.orangesoftware.financisto.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AboutCollectionAdapter extends FragmentStateAdapter {
    public AboutCollectionAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new AboutPageFragment();
        Bundle args = new Bundle();
        args.putInt(AboutPageFragment.PAGE_ID, position + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
