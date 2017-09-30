package com.developers.droidteam.merisafety;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;


/**
 * A simple {@link Fragment} subclass.
 */
public class MedicalFragment extends Fragment {

    Context con;
    public MedicalFragment() {
        // Required empty public constructor
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
        return inflater.inflate(R.layout.fragment_medical, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DatabaseOperations DOP = new DatabaseOperations(getActivity());
        Cursor CR= DOP.getInformationGaur(DOP);
        CR.moveToNext();
        final String number =CR.getString(1).trim();
        final String mail =CR.getString(2).trim();
        final PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, new Intent("in.wptrafficanalyzer.sent"), 0);
        final PendingIntent pin = PendingIntent.getBroadcast(getActivity(), 0, new Intent("in.wptrafficanalyzer.delivered"), 0);
        final SmsManager smss = SmsManager.getDefault();
        final String sms = "Emergency! Save me urgently. I need medical help";

        new Thread(){

            @Override
            public void run() {
                super.run();

                try {
                    Thread.sleep(100);

                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +number));
                    startActivity(intent);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
/*
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

                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(number, null, "Help! Call me urgently. Please save me", null, null);
                        Toast.makeText(getActivity(), "Message sent successfully.", Toast.LENGTH_SHORT).show();
                    }
                })
                .send();

*/

    }
}
