package com.developers.droidteam.merisafety;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class LowAlertFragment extends Fragment {


    private DatabaseReference mDatabase;
    private DatabaseReference guarEnd ;

    Context con;

    public LowAlertFragment()
    {
        //required empty constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
         con = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_low_alert, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences sp = con.getSharedPreferences("account_db", Context.MODE_PRIVATE);
        final String user = sp.getString("login_key", null);

        final String[] name = new String[1];
        final String[] email = new String[1];
        final String[] mobile = new String[1];


        mDatabase = FirebaseDatabase.getInstance().getReference();

        guarEnd = mDatabase.child("users").child(user).child(user);

        guarEnd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot currentsnapshot : dataSnapshot.getChildren())
                {
                    sendAlert(dataSnapshot.child("mobile").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public  void sendAlert(final String mobile)
    {

        new Thread(){

            @Override
            public void run() {
                super.run();

                try {
                    Thread.sleep(100);

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +mobile));
                    startActivity(intent);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
}
