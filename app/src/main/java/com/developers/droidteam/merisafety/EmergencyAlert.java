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
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork =  cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();

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

            if(countPowerOff==1)
            {
                if(isConnected)
                {
                    Toast.makeText(context, "Data is on, Save Me alert will be generated.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context, "Data is off, High alert will be generated.", Toast.LENGTH_SHORT).show();
                }
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

            if(countPowerOff==1)
            {
                if(isConnected)
                {
                    Toast.makeText(context, "Data is on, Save Me alert will be generated.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(context, "Data is off, High alert will be generated.", Toast.LENGTH_SHORT).show();
                }
            }
            if (countPowerOff >= 4) {

                final Intent i = new Intent();
                if(isConnected)
                {
                    i.putExtra("key", R.id.save_me);
                }else
                {
                    i.putExtra("key", R.id.highalert);
                }

                i.setClassName("com.developers.droidteam.merisafety","com.developers.droidteam.merisafety.SaveMeActivity");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(KEYGUARD_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (keyguardManager != null) {
                        keyguardManager.requestDismissKeyguard((Activity) context, new KeyguardManager.KeyguardDismissCallback() {
                            @Override
                            public void onDismissSucceeded() {
                                super.onDismissSucceeded();
                            }
                        });
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