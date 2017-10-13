package com.developers.droidteam.merisafety;


import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;


/**
 * A simple {@link Fragment} subclass.
 */
public class SaveMeFragment extends Fragment {



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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         savemeinflater = inflater.inflate(R.layout.fragment_save_me, container, false);
        // Inflate the layout for this fragment
        return savemeinflater;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

     /*   Intent gmail = new Intent(android.content.Intent.ACTION_SEND);
        gmail.putExtra(Intent.EXTRA_EMAIL, new String[] { "Sunnyparihar35@gmail.com" });
        gmail.setData(Uri.parse("Sunnyparihar35@gmail.com"));
        gmail.putExtra(Intent.EXTRA_SUBJECT, "I'm in Emergency, Please Call me");
        gmail.putExtra(Intent.EXTRA_TEXT, "Im at noida please call me, i need your help");
        gmail.setType("application/octet-stream");
        startActivity(gmail);
*/
     Button b = (Button) savemeinflater.findViewById(R.id.save_me_map);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(con,MapsActivity.class));
            }
        });

        DatabaseOperations DOP = new DatabaseOperations(getActivity());
        Cursor CR= DOP.getInformationGaur(DOP);
        CR.moveToNext();
        final String number =CR.getString(1).trim();
        final String mail =CR.getString(2).trim();
        final PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, new Intent("in.wptrafficanalyzer.sent"), 0);
        final PendingIntent pin = PendingIntent.getBroadcast(getActivity(), 0, new Intent("in.wptrafficanalyzer.delivered"), 0);
        final SmsManager smss = SmsManager.getDefault();
        SharedPreferences sp = con.getSharedPreferences("currentloc",con.MODE_PRIVATE);
        String loc = sp.getString("curloc",null);

        final String sms = number+" Help me!, i'm in emergency. My Location is "+loc+". You received this alert because you are the guardian of "+number;


        new Thread(){

            @Override
            public void run() {
                super.run();

                try {
                    Thread.sleep(100);

                    smss.sendTextMessage(number, null, sms, pi, pin);


                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +number));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();


//***************************************************************************************************
        BackgroundMail.newBuilder(con).withUsername("merisafety@gmail.com")
                .withPassword("WRTB@droid")
                .withMailto(mail)
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
                    }
                })
                .send();






    }
}
