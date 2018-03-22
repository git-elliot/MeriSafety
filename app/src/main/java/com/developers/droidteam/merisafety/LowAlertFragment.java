package com.developers.droidteam.merisafety;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    private final String sp_db = "account_db";
    private final String l_key = "login_key";
    private final String d_key = "users";
    private final String m_key = "mobile";
    private final String gn_key="gname";
    private final String gm_key="gmobile";
    private final String ge_key="gemail";


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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_low_alert, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork =  cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();

        SharedPreferences sp = con.getSharedPreferences(sp_db, Context.MODE_PRIVATE);
        final String user = sp.getString(l_key, null);
        final String gname = sp.getString(gn_key,null);
        final String gmobile = sp.getString(gm_key,null);

        if(isConnected){
            mDatabase = FirebaseDatabase.getInstance().getReference();

            guarEnd = mDatabase.child(d_key).child(user).child(user);

            guarEnd.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                        sendAlert(dataSnapshot.child(m_key).getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else{
            sendAlert(gmobile);

        }


    }

    public  void sendAlert(final String mobile)
    {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(con);
        boolean msg = sharedPref.getBoolean(SettingsActivity.KEY_SEND_MSG,true);
        boolean call = sharedPref.getBoolean(SettingsActivity.KEY_DIAL_CALL,true);
        boolean em = sharedPref.getBoolean(SettingsActivity.KEY_SEND_EMAIL,true);

        if(call){

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

        }else {
            Toast.makeText(con, "Call must be turned on in the settings", Toast.LENGTH_LONG).show();
        }
    }
}
