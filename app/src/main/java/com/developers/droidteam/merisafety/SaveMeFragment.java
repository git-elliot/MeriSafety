package com.developers.droidteam.merisafety;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SaveMeFragment extends Fragment {



    private DatabaseReference mDatabase;
    private DatabaseReference guarEnd ;
    private DatabaseReference apiKey;

    private final static String sp_db = "account_db";
    private final static String l_key = "login_key";
    private final static String d_key = "users";
    private final static String n_key = "name";
    private final static String m_key = "mobile";
    private final static String e_key = "email";
    private final static String cur_key="curloc";
    private final static String cur_db = "currentloc";
    private final static String gn_key="gname";
    private final static String gm_key="gmobile";
    private final static String ge_key="gemail";
    private boolean executeOnce = false;

    SharedPreferences sp;

    Context con;
    public SaveMeFragment() {
        // Required empty public constructor
    }
    View savemeinflater;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con= context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         savemeinflater = inflater.inflate(R.layout.fragment_save_me, container, false);
        // Inflate the layout for this fragment
        return savemeinflater;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork =  cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();

        sp = con.getSharedPreferences(sp_db, Context.MODE_PRIVATE);
        final String user = sp.getString(l_key, null);
        final String gname = sp.getString(gn_key,null);
        final String gmobile = sp.getString(gm_key,null);
        final String gemail = sp.getString(ge_key,null);



        mDatabase = FirebaseDatabase.getInstance().getReference();

        guarEnd = mDatabase.child(d_key).child(user).child(user);

        guarEnd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(isConnected)
                {
                    sendAlert(dataSnapshot.child(n_key).getValue().toString(),dataSnapshot.child(m_key).getValue().toString(),dataSnapshot.child(e_key).getValue().toString());

                }
                else
                {
                    sendAlert(gname,gmobile,gemail);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendAlert(String name, final String mobile, final String email)
    {




        final PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, new Intent("in.wptrafficanalyzer.sent"), 0);
        final PendingIntent pin = PendingIntent.getBroadcast(getActivity(), 0, new Intent("in.wptrafficanalyzer.delivered"), 0);
        final SmsManager smss = SmsManager.getDefault();
        sp = con.getSharedPreferences(cur_db, Context.MODE_PRIVATE);
        String loc = sp.getString(cur_key,null);

        final String sms = name+" Help me!, i'm in emergency. My Location is "+loc+". You received this alert because you are the guardian";


        new Thread(){

            @Override
            public void run() {
                super.run();

                try {
                    Thread.sleep(2000);

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +mobile));
                    startActivity(intent);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();


        smss.sendTextMessage(mobile, null, sms, pi, pin);

        apiKey = mDatabase.child("mailapikey");
        apiKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String api_key=dataSnapshot.getValue().toString();
                if(!executeOnce){

                    SendMail sendMail = new SendMail(email,"Save Me Alert",sms,api_key);
                    sendMail.execute();
                    executeOnce=true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        new Thread(){

            @Override
            public void run() {
                super.run();

                try {
                    Thread.sleep(100);

                    startActivity(new Intent(con,MapsActivity.class).putExtra("saveme","yes"));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
