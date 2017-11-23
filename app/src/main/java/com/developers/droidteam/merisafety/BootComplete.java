package com.developers.droidteam.merisafety;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by paras on 23/11/17.
 */

public class BootComplete extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, AlertService.class);
        context.startService(i);

    }
}
