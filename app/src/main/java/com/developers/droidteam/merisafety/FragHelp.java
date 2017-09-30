package com.developers.droidteam.merisafety;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by siddharth on 6/28/2017.
 */

public class FragHelp extends Fragment {

    View v;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.activity_fragment_help,container,false);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WebView webView = (WebView) v.findViewById(R.id.web);
        WebSettings w = webView.getSettings();
        w.setJavaScriptEnabled(true);
        w.setBuiltInZoomControls(true);
        webView.loadUrl("https://www.google.co.in/search?q=nearby+police+station");

    }
}
