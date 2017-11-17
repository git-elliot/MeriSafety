package com.developers.droidteam.merisafety;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdvanceAlertFragment extends Fragment {

    private DatabaseReference mDatabase;
    private DatabaseReference guarEnd ;


    View v;
    Context con;
    public AdvanceAlertFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_advance_alert, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

     /*   Button b = (Button) v.findViewById(R.id.save_me_travel);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(con,MapsActivity.class));
            }
        }); */

        SharedPreferences sp = con.getSharedPreferences("account_db", Context.MODE_PRIVATE);
        final String user = sp.getString("login_key", null);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        guarEnd = mDatabase.child("users").child(user).child(user);

        guarEnd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot currentsnapshot : dataSnapshot.getChildren())
                {
                    sendAlert(dataSnapshot.child("name").getValue().toString(),dataSnapshot.child("mobile").getValue().toString(),dataSnapshot.child("email").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public  void sendAlert(String name, final String mobile, String email)
    {

        SharedPreferences sp = con.getSharedPreferences("account_db", Context.MODE_PRIVATE);


        final PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, new Intent("in.wptrafficanalyzer.sent"), 0);
        final PendingIntent pin = PendingIntent.getBroadcast(getActivity(), 0, new Intent("in.wptrafficanalyzer.delivered"), 0);
        final SmsManager smss = SmsManager.getDefault();
        sp = con.getSharedPreferences("currentloc", con.MODE_PRIVATE);
        String loc = sp.getString("curloc",null);

        final String sms = name+" Help me!, i'm in emergency. My Location is "+loc+". You received this alert because you are the guardian";


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

        BackgroundMail.newBuilder(con).withUsername("merisafety@gmail.com")
                .withPassword("WRTB@droid")
                .withMailto(email)
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Mail from MeriSafety")
                .withBody(sms)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        //do some magic
//                        SmsManager smsManager = SmsManager.getDefault();
//                        smsManager.sendTextMessage(number, null, "Help! Call me urgently. Please save me", null, null);
//                        Toast.makeText(getActivity(), "Message sent successfully.", Toast.LENGTH_SHORT).show();

                        smss.sendTextMessage(mobile, null, sms, pi, pin);



                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        //do some magic
                        /*
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(number, null, "Help! Call me urgently. Please save me", null, null);
                        Toast.makeText(getActivity(), "Message sent successfully.", Toast.LENGTH_SHORT).show();
*/

                        smss.sendTextMessage(mobile, null, sms, pi, pin);

                    }
                })
                .send();

    }
}
