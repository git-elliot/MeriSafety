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


    EditText phno;
    Button sms;
    View v;
    public View onCreateView(LayoutInflater l, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = l.inflate(R.layout.activity_frag_verification, container,false);

        phno = (EditText) v.findViewById(R.id.editSMSText);
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
        phno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if(s.length()>10)
                {
                    phno.setError("Number should be less than 10 digits");
                }
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phno.getText().toString().length() < 10) {
                    Toast.makeText(getActivity(), "Number of digits should be 10.", Toast.LENGTH_SHORT).show();
                } else {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phno.getText().toString(), null, "Help! Call me urgently.", null, null);
                    Toast.makeText(getActivity(), "Message sent successfully.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}