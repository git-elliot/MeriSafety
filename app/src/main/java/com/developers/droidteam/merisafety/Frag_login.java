package com.developers.droidteam.merisafety;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseException;

import java.io.File;
import java.io.FileOutputStream;

public class Frag_login extends Fragment {

    View v1;
    String username,userpass;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater l, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
         v1=  l.inflate(R.layout.activity_frag_login,container,false);

  return v1;
}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final EditText e1 = (EditText)v1.findViewById(R.id.e1);
        final EditText e2 = (EditText)v1.findViewById(R.id.e2);
        Button b1 = (Button)v1.findViewById(R.id.b1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Please wait.......",Toast.LENGTH_SHORT).show();

/*
                if(e1.getText().toString().trim().length()!=10){
                    e1.setError("Phone Number is invalid");
                    e1.requestFocus();}


                else  if (e2.getText().toString().trim().length()<6){
                    e2.setError("Password must be atleast 6 characters");
                    e2.requestFocus();
                }
                else
                {

                    Intent itt = new Intent(getContext(), NavigationDrawerActivity.class);
                    startActivity(itt);
                }
  */
                SharedPreferences sp = getActivity().getSharedPreferences("account_db", Context.MODE_PRIVATE);

                try{


                    username = e1.getText().toString();
                    userpass = e2.getText().toString();
                    DatabaseOperations DOP = new DatabaseOperations(getActivity());
                    Cursor CR= DOP.getInformation(DOP);
                    CR.moveToFirst();
                    boolean loginstatus= false;
                    String NAME = "";
                    do
                    {
                        if(username.equals(CR.getString(0)) && userpass.equals(CR.getString(1)))
                        {
                            loginstatus = true;
                            NAME = CR.getString(0);
                        }
                    }while(CR.moveToNext());

                    if(loginstatus)
                    {

                        SharedPreferences.Editor et = sp.edit();
                        et.putString("login_key",username);
                        et.remove("logout_key");
                        et.commit();

                        Toast.makeText(getActivity(),"Login Successful",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(),NavigationDrawerActivity.class);
                        startActivity(intent);

                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Login Failed",Toast.LENGTH_LONG).show();
                    }

                }catch (android.database.CursorIndexOutOfBoundsException e)
                {
                    Toast.makeText(getContext(), "You need to signup first", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }

    }
});

        }
}
