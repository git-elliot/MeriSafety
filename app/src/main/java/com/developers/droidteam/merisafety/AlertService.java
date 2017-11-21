package com.developers.droidteam.merisafety;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by paras on 21/11/17.
 */

public class AlertService extends Service {
    public AlertService()
    {
        //empty constructor
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        EmergencyAlert eReceiver = new EmergencyAlert ();
        registerReceiver(eReceiver, filter);

        return super.onStartCommand(intent, flags, startId);

    }

    public class LocalBinder extends Binder{
        AlertService getService(){
            return AlertService.this;
        }
    }
}
