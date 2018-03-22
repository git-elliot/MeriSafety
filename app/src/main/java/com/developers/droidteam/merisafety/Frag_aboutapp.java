package com.developers.droidteam.merisafety;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_aboutapp extends Fragment {


    View v;
    Button likeUs;
    public Frag_aboutapp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_frag_aboutapp, container, false);
        likeUs = v.findViewById(R.id.like_us_fb);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
      likeUs.setOnClickListener(view -> {
          Intent intent =  new Intent(Intent.ACTION_VIEW,
                  Uri.parse("https://www.facebook.com/merisafety"));

          startActivity(intent);

      });
    }
}