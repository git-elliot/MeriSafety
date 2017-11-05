package com.developers.droidteam.merisafety;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.telephony.SmsManager;
import android.widget.Toast;

public class Frag_verification extends Fragment {

    Button sms;
    View v;
    public View onCreateView(LayoutInflater l, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = l.inflate(R.layout.activity_frag_verification, container,false);

        sms = (Button) v.findViewById(R.id.resend_code);

        Button b1 = (Button)v.findViewById(R.id.proceed1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.l2, new Frag_guardian());
                ft.addToBackStack(null);
                ft.commit();
            }});

                return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }


}