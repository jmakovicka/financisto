package ru.orangesoftware.financisto.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AboutCollectionAdapter extends FragmentStateAdapter {
    public static final String[] tabNames = {"New", "Privacy", "License", "About"};
    public static final String[] tabUrls = {
            "file:///android_asset/whatsnew.htm",
            "file:///android_asset/privacy.htm",
            "file:///android_asset/gpl-2.0-standalone.htm",
            "file:///android_asset/about.htm"
    };

    public AboutCollectionAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new AboutPageFragment();
        Bundle args = new Bundle();
        args.putString(AboutPageFragment.PAGE_URL, tabUrls[position]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() { return tabNames.length; }
}
