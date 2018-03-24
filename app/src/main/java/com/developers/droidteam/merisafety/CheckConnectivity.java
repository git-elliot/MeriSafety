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

public class CheckConnectivity extends BroadcastReceiver {

    private boolean offline = false;
    @Override
    public void onReceive(Context context, Intent intent) {

        Activity activity = (Activity) context;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork =  cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
                window.setNavigationBarColor(activity.getResources().getColor(R.color.black));
            }
           if(offline){
                Snackbar.make(activity.findViewById(R.id.newfraglayout),"You are back to Online",Snackbar.LENGTH_SHORT).show();
                offline=false;
           }
        }
        else{
            offline=true;
            Snackbar.make(activity.findViewById(R.id.newfraglayout),"You are offline",Snackbar.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = activity.getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(activity.getResources().getColor(R.color.offline_color));
                    window.setNavigationBarColor(activity.getResources().getColor(R.color.offline_color));
                }
        }

    }
}
