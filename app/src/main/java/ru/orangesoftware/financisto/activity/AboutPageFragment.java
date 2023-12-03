package ru.orangesoftware.financisto.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.orangesoftware.financisto.R;

public class AboutPageFragment extends Fragment {
    public static final String PAGE_ID = "page_id";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.about_collection_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        switch(args.getInt(PAGE_ID))
        {
            case 1:
                ((WebView) view.findViewById(R.id.aboutWebView)).loadUrl("file:///android_asset/whatsnew.htm");
                break;
            case 2:
                ((WebView) view.findViewById(R.id.aboutWebView)).loadUrl("file:///android_asset/privacy.htm");
                break;
            case 3:
                ((WebView) view.findViewById(R.id.aboutWebView)).loadUrl("file:///android_asset/gpl-2.0-standalone.htm");
                break;
            case 4:
                ((WebView) view.findViewById(R.id.aboutWebView)).loadUrl("file:///android_asset/about.htm");
                break;
            default:
                break;
        }
    }
}
