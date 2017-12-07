package com.developers.droidteam.merisafety;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SaveMeActivity extends AppCompatActivity {

    private final String key = "key";
    private final int defaultValue = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_me);
      Intent i = getIntent();

       int Resid = i.getIntExtra(key,defaultValue);
        if(Resid==R.id.save_me)
        {
            Toast.makeText(this, "Save Me Alert generated", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.RED);
                window.setNavigationBarColor(Color.RED);
            }
            /*
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.maps_layout,new SaveMeFragment());
            ft.commit();*/
        }
        if(Resid==R.id.lowalert)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.maps_layout,new LowAlertFragment());
            ft.commit();
        }
        if(Resid==R.id.highalert)
        {
            Toast.makeText(this, "High Alert generated", Toast.LENGTH_SHORT).show();

            /*
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.maps_layout,new HighAlertFragment());
            ft.commit();
            */
        }
        if(Resid==R.id.advancealert)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.maps_layout,new AdvanceAlertFragment());
            ft.commit();
        }
        if(Resid==R.id.self_defence)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.maps_layout,new SelfDefenceFragment());
            ft.commit();
        }
    }
}
