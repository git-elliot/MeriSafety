package com.developers.droidteam.merisafety;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


public class BootComplete extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, AlertService.class);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean hot_key = sharedPref.getBoolean(SettingsActivity.KEY_HOT,false);
        if(hot_key){

            Log.d("hotkey","hot key is on");
            context.startService(i);

        }
        else {
            Log.d("hotkey","hot key is off");
            context.stopService(i);
        }
    }
}
