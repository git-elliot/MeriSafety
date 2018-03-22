package com.developers.droidteam.merisafety;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class SmsNotifications extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getAction();
        assert name != null;
        if(name.equals("in.wptrafficanalyzer.sent")){
            switch(getResultCode()){
                case Activity.RESULT_OK:
                    Toast.makeText(context, "sms is sent successfully", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(context, "Error in sending sms", Toast.LENGTH_SHORT).show();
            }
        }
        if(name.equals("in.wptrafficanalyzer.delivered")){
            switch(getResultCode()){
                case Activity.RESULT_OK:
                    Toast.makeText(context, "sms is sent successfully", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(context, "Error in delivery in sms", Toast.LENGTH_SHORT).show();
            }
        }

    }
}