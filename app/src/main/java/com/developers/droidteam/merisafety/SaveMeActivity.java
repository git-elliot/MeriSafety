package com.developers.droidteam.merisafety;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SaveMeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_me);
      Intent i = getIntent();

       int Resid = i.getIntExtra("key",0);
        if(Resid==R.id.save_me)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.maps_layout,new SaveMeFragment());
            ft.commit();
        }
        if(Resid==R.id.medical)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.maps_layout,new MedicalFragment());
            ft.commit();
        }
        if(Resid==R.id.security)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.maps_layout,new SecurityFragment());
            ft.commit();
        }
        if(Resid==R.id.travel)
        {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.maps_layout,new TravelFragment());
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
