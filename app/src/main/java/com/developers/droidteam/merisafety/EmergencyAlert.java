package com.developers.droidteam.merisafety;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

import static android.content.Context.KEYGUARD_SERVICE;

public class EmergencyAlert extends BroadcastReceiver {
    static int countPowerOff = 0;
    long lastClicktime = 0;
    public EmergencyAlert() {
        //empty constructor
    }
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.v("onReceive", "Power button is pressed.");

        if (Objects.equals(intent.getAction(), Intent.ACTION_SCREEN_OFF)) {
           if(lastClicktime==0)
           {
               lastClicktime =System.currentTimeMillis() ;
               countPowerOff++;

           }
           else if(System.currentTimeMillis()-lastClicktime<=1000)
           {

               lastClicktime = System.currentTimeMillis();
               countPowerOff++;

           }
           else
           {
               lastClicktime=0;
               countPowerOff=0;
           }

        }
        else if (Objects.equals(intent.getAction(), Intent.ACTION_SCREEN_ON)) {
            if(lastClicktime==0)
            {
                lastClicktime = System.currentTimeMillis();
                countPowerOff++;
            }
            else if(System.currentTimeMillis()-lastClicktime<=1000)
            {
                lastClicktime =System.currentTimeMillis();
                countPowerOff++;
            }
            else {
                countPowerOff=0;
                lastClicktime=0;
            }

            if (countPowerOff >= 6) {

                final Intent i = new Intent();
                i.setClassName("com.developers.droidteam.merisafety","com.developers.droidteam.merisafety.MapsActivity");
                i.putExtra("saveme","yes");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(KEYGUARD_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (keyguardManager != null) {
                        keyguardManager.requestDismissKeyguard((Activity) context, null);
                    }
                }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    context.startActivity(i);
                }

                countPowerOff=0;
                lastClicktime=0;
           }


        }


    }
}