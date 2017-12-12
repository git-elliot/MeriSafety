package com.developers.droidteam.merisafety;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by paras on 21/11/17.
 */

public class EmergencyAlert extends BroadcastReceiver {
    static int countPowerOff = 0;
    long lastClicktime = 0;
    public EmergencyAlert() {
        //empty constructor


    }
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("onReceive", "Power button is pressed.");
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork =  cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
           if(lastClicktime==0)
           {
               lastClicktime =System.currentTimeMillis() ;
               countPowerOff++;

           }
           else if(System.currentTimeMillis()-lastClicktime<=1200)
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
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            if(lastClicktime==0)
            {
                lastClicktime = System.currentTimeMillis();
                countPowerOff++;
            }
            else if(System.currentTimeMillis()-lastClicktime<=1200)
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
            if (countPowerOff >= 3) {

                Intent i = new Intent();
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
                KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
                lock.disableKeyguard();
                context.startActivity(i);
                countPowerOff=0;
                lastClicktime=0;
           }


        }


    }
}