package com.developers.droidteam.merisafety;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

/**
 * Created by paras on 24/3/18.
 */

public class SendAlert extends AsyncTask {

    private DatabaseReference mDatabase;
    private DatabaseReference guarEnd;
    private DatabaseReference apiKey;

    private final static String sp_db = "account_db";
    private final static String l_key = "login_key";
    private final static String d_key = "users";
    private final static String n_key = "name";
    private final static String m_key = "mobile";
    private final static String e_key = "email";
    private final static String cur_key = "curloc";
    private final static String cur_db = "currentloc";
    private final static String gn_key = "gname";
    private final static String gm_key = "gmobile";
    private final static String ge_key = "gemail";
    private boolean executeOnce = false;

    final PendingIntent pi;
    final PendingIntent pin;
    final SmsManager smss;
    String loc;
    String sms;
    SharedPreferences sp;
    Context con;
    TextView textView;

    SendAlert(Context context,TextView text_info) {
        textView=text_info;
        con = context;
        pin = PendingIntent.getBroadcast(con, 0, new Intent("in.wptrafficanalyzer.delivered"), 0);
        pi = PendingIntent.getBroadcast(con, 0, new Intent("in.wptrafficanalyzer.sent"), 0);
        smss = SmsManager.getDefault();
        sp = con.getSharedPreferences(cur_db, Context.MODE_PRIVATE);
        loc = sp.getString(cur_key, null);

    }

    @Override
    protected Object doInBackground(Object[] objects) {


        ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        sp = con.getSharedPreferences(sp_db, Context.MODE_PRIVATE);
        final String user = sp.getString(l_key, null);
        final String gname = sp.getString(gn_key, null);
        final String gmobile = sp.getString(gm_key, null);
        final String gemail = sp.getString(ge_key, null);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        guarEnd = mDatabase.child(d_key).child(user).child(user);

        guarEnd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (isConnected) {


                    sendAlert(textView,dataSnapshot.child(n_key).getValue().toString(), dataSnapshot.child(m_key).getValue().toString(), dataSnapshot.child(e_key).getValue().toString());

                } else {
                    sendAlert(textView,gname, gmobile, gemail);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return null;
    }

    private void sendAlert(TextView tv, String name, final String mobile, final String email) {


        sms = name + " Help me!, i'm in emergency. My Location is " + loc + ". You received this alert because you are the guardian";


        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobile));
        if (ActivityCompat.checkSelfPermission(con, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        con.startActivity(intent);
        textView.setText(" Call done ->");

        smss.sendTextMessage(mobile, null, sms, pi, pin);

        tv.append(" Sms sent ->");

        apiKey = mDatabase.child("mailapikey");
        apiKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String api_key=dataSnapshot.getValue().toString();
                if(!executeOnce){

                    SendMail sendMail = new SendMail(email,"Save Me Alert",sms,api_key);
                    sendMail.execute();
                    executeOnce=true;
                    tv.append(" Email sent");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Firebase",databaseError.getMessage());
            }
        });


    }

}
