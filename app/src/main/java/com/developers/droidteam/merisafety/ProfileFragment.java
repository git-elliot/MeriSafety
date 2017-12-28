package com.developers.droidteam.merisafety;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.developers.droidteam.merisafety.NavigationDrawerActivity.FetchBitmap;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    Context con;
    View v;
    ImageView imgView;
    ProgressBar progressBar;
    String url;
    TextView text_name;
    TextView text_email;
    TextView text_number;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con=context;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        progressBar = v.findViewById(R.id.progress_profile_pic);
        imgView = v.findViewById(R.id.profile_pic);
        text_name = v.findViewById(R.id.p_name);
        text_email = v.findViewById(R.id.p_email);
        text_number = v.findViewById(R.id.p_number);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FetchBitmap task =new FetchBitmap(null, url,imgView,progressBar);


    }
}
