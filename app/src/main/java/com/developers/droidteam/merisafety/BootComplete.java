package com.developers.droidteam.merisafety;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class BootComplete extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, AlertService.class);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean hot_key = sharedPref.getBoolean(SettingsActivity.KEY_HOT,true);
        if(hot_key){

            context.startService(i);

        }
    }
}
