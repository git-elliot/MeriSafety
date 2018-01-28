package com.developers.droidteam.merisafety;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by paras on 12/1/18.
 */

public class myFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseMessaging";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Toast.makeText(this, "remoteMessage : "+remoteMessage.toString(), Toast.LENGTH_SHORT).show();
        Log.d(TAG,"message : "+remoteMessage.toString());
    }
}
