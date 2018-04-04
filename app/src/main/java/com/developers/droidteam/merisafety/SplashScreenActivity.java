package com.developers.droidteam.merisafety;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

public class SplashScreenActivity extends AppCompatActivity {

    private final String sp_db = "account_db";
    private final String l_key = "login_key";
    private static final X500Principal DEBUG_DN = new X500Principal("CN=Android Debug,O=Android,C=US");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isDebuggable(this)){
            Fabric.with(this, new Crashlytics());
        }
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
    private boolean isDebuggable(Context ctx)
    {
        boolean debuggable = false;

        try
        {
            PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature signatures[] = pinfo.signatures;

            CertificateFactory cf = CertificateFactory.getInstance("X.509");

            for (Signature signature : signatures) {
                ByteArrayInputStream stream = new ByteArrayInputStream(signature.toByteArray());
                X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
                debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
                if (debuggable)
                    break;
            }
        }
        catch (PackageManager.NameNotFoundException | CertificateException e)
        {
            //debuggable variable will remain false
        }
        return debuggable;
    }
}
