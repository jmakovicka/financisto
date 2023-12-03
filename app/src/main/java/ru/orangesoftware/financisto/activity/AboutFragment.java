/*
 * Copyright (c) 2011 Denis Solonenko.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */

package ru.orangesoftware.financisto.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import ru.orangesoftware.financisto.R;

public class AboutFragment extends Fragment {
    TabLayout tabLayout;
    AboutCollectionAdapter aboutCollectionAdapter;
    ViewPager2 viewPager;
    TabLayoutMediator tabLayoutMediator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.about_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tabLayout = view.findViewById(R.id.tabLayout);

        aboutCollectionAdapter = new AboutCollectionAdapter(this);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setAdapter(aboutCollectionAdapter);

        String[] tabNames = {"New", "Privacy", "License", "About"};

        tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabNames[position])
        );
        tabLayoutMediator.attach();
    }
}

