package com.developers.droidteam.merisafety;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class SplashScreenActivity extends AppCompatActivity {

    private final String sp_db = "account_db";
    private final String l_key = "login_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_splash_screen);


        new Thread(() -> {
            try {
                Thread.sleep(3000);

                SharedPreferences sp = getSharedPreferences(sp_db, Context.MODE_PRIVATE);
                String key=sp.getString(l_key,null);
                if(key!=null)
                {

                    startActivity(new Intent(SplashScreenActivity.this,NavigationDrawerActivity.class));
                }
                else
                {

                    startActivity(new Intent(SplashScreenActivity.this,MainActivity.class));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



            finish();
        }).run();

    }
}
