package com.developers.droidteam.merisafety;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Map;

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


        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
           if(lastClicktime==0)
           {
               lastClicktime =System.currentTimeMillis() ;
               countPowerOff++;

           }
           else if(System.currentTimeMillis()-lastClicktime<=1500)
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
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            if(lastClicktime==0)
            {
                lastClicktime = System.currentTimeMillis();
                countPowerOff++;
            }
            else if(System.currentTimeMillis()-lastClicktime<=1500)
            {
                lastClicktime =System.currentTimeMillis();
                countPowerOff++;
            }
            else {
                countPowerOff=0;
                lastClicktime=0;
            }


            if (countPowerOff >= 3) {
                Intent i = new Intent();
                i.putExtra("key", R.id.save_me);
                i.setClassName("com.developers.droidteam.merisafety","com.developers.droidteam.merisafety.SaveMeActivity");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         context.startActivity(i);

                countPowerOff=0;
                lastClicktime=0;
           }


        }


    }
}