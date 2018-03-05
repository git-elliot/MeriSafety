package com.developers.droidteam.merisafety;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by paras on 28/1/18.
 */

public class CheckConnectivity extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork =  cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Activity activity = (Activity) context;
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
                window.setNavigationBarColor(activity.getResources().getColor(R.color.black));
            }

        }
        else{
            Toast.makeText(context, "You are offline.", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Activity activity = (Activity) context;
                    Window window = activity.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(activity.getResources().getColor(R.color.offline_color));
                    window.setNavigationBarColor(activity.getResources().getColor(R.color.offline_color));
                }
        }

    }
}
