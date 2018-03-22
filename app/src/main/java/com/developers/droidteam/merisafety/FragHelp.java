package com.developers.droidteam.merisafety;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class FragHelp extends Fragment {

    View v;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.activity_fragment_help,container,false);

        return v;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WebView webView = v.findViewById(R.id.web);
        WebSettings w = webView.getSettings();
        w.setJavaScriptEnabled(true);
        w.setBuiltInZoomControls(true);
        webView.loadUrl("https://www.google.co.in/search?q=nearby+police+station");

    }
}
