package com.developers.droidteam.merisafety;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Frag_signup extends Fragment {

    Context con;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con = context;
    }

    @Override
    public View onCreateView(LayoutInflater l, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v2 = l.inflate(R.layout.activity_frag_signup, container, false);
        final SharedPreferences sp = getActivity().getSharedPreferences("account_db", Context.MODE_PRIVATE);
        final EditText e1 = (EditText) v2.findViewById(R.id.e1);
        TelephonyManager tmgr = (TelephonyManager) con.getSystemService(con.TELEPHONY_SERVICE);
        String number = tmgr.getLine1Number();
        e1.setText(number);
        final EditText e2 = (EditText) v2.findViewById(R.id.e2);
        final EditText e3 = (EditText) v2.findViewById(R.id.e3);
        Button btn = (Button) v2.findViewById(R.id.btt);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
String s = e2.getText().toString();
                if (e1.getText().toString().trim().length() != 10) {
                    e1.setError("Phone Number is invalid");
                    e1.requestFocus();
                } else if (e2.getText().toString().trim().length() < 6) {
                    e2.setError("Password must be atleast 6 characters");
                    e2.requestFocus();
                }else if(!e3.getText().toString().equals(s))
                {
                    e2.setError("Password didn't match");

                }
                else {
                    DatabaseOperations DB = new DatabaseOperations(getActivity());
                    DB.putInformation(DB,e1.getText().toString(),e2.getText().toString());
                    Toast.makeText(getActivity(),"Registration Success",Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor et = sp.edit();
                    et.putString("login_key",e1.getText().toString().trim());
                    et.remove("logout_key");
                    et.commit();

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.replace(R.id.l2, new Frag_guardian());
                    //  ft.addToBackStack(null);
                    ft.commit();

                }


            }
        });

        return v2;
    }
}