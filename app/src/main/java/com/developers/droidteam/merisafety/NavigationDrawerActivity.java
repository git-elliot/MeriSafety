package com.developers.droidteam.merisafety;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_MSG =1880 ;
    private static final int REQUEST_CHECK_SETTINGS = 2;

    LocationRequest locationRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);


        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragHome obj = new FragHome();
        ft.add(R.id.newfraglayout, obj, "homepage");
        ft.commit();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        //**************************havigation drawer data**********************

        SharedPreferences sp = getSharedPreferences("account_db", Context.MODE_PRIVATE);
        final String user = sp.getString("login_key", null);

        TextView tv_name = (TextView) hView.findViewById(R.id.nav_drawer_name);
        tv_name.setText("Welcome");

        TextView tv_user = (TextView) hView.findViewById(R.id.nav_drawer_user);
        tv_user.setText(user);

        final ImageView iv = (ImageView) findViewById(R.id.imageView);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();


       String email = sp.getString("email",null);
        if(email!=null)
        {
            StorageReference photoRef = storageRef.child("images/"+email+".jpg");

            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            final File finalLocalFile = localFile;
            photoRef.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Successfully downloaded data to local file
                            // ...
                            File imgFile = new  File(String.valueOf(finalLocalFile));

                            if(imgFile.exists()){

                                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                                iv.setImageBitmap(myBitmap);

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle failed download
                    // ...
                }
            });
        }
    }


    public void fun(View v)
    {
        if(R.id.medical==v.getId()){

            Intent i = new Intent(this,SaveMeActivity.class);
            i.putExtra("key",v.getId());
            startActivity(i);

        }
        if(R.id.save_me==v.getId())
        {
            Intent i = new Intent(this,SaveMeActivity.class);
            i.putExtra("key",v.getId());
            startActivity(i);
        }
        if(R.id.security==v.getId())
        {
            Intent i = new Intent(this,SaveMeActivity.class);
            i.putExtra("key",v.getId());
            startActivity(i);
        }
        if(R.id.travel==v.getId())
        {
            Intent i = new Intent(this,SaveMeActivity.class);
            i.putExtra("key",v.getId());
            startActivity(i);
        }
        if(R.id.self_defence==v.getId())
        {
            Intent i = new Intent(this,SaveMeActivity.class);
            i.putExtra("key",v.getId());
            startActivity(i);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        final SharedPreferences sp = getSharedPreferences("account_db", Context.MODE_PRIVATE);


        if (id == R.id.nav_home) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            FragHome obj = new FragHome();
            ft.addToBackStack("stack1");
            ft.replace(R.id.newfraglayout,obj,"homebutton");
            ft.commit();
        } else if (id == R.id.nav_gaudian) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            GuardianFragment obj = new GuardianFragment();
            ft.addToBackStack("stack2");
            ft.replace(R.id.newfraglayout,obj,"guardian");
            ft.commit();

        } else if (id == R.id.aboutapp) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Frag_aboutapp obj = new Frag_aboutapp();
            ft.addToBackStack("stack3");
            ft.replace(R.id.newfraglayout,obj,"guardian");
            ft.commit();


        } else if (id == R.id.team) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Frag_team obj = new Frag_team();
            ft.addToBackStack("stack4");
            ft.replace(R.id.newfraglayout,obj,"guardian");
            ft.commit();


        } else if (id == R.id.nav_police) {

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.addToBackStack("stack5");
            FragHelp obj = new FragHelp();
            ft.replace(R.id.newfraglayout,obj,"help");
            ft.commit();


        }else if(id == R.id.nav_nearby)
        {
         startActivity(new Intent(NavigationDrawerActivity.this,MapsActivity.class));
        }
        else if (id == R.id.nav_feedback) {

            Intent email = new Intent(Intent.ACTION_SEND);
            String[] s ={"merisafety@gmail.com","khandelwalparas8@gmail.com"};
            email.putExtra(Intent.EXTRA_EMAIL,s);

            email.putExtra(Intent.EXTRA_SUBJECT,"Feedback for MeriSafety App");
            email.putExtra(Intent.EXTRA_TEXT,"Write Feedback here");
            email.setType("message/rfc822");
            startActivity(email);

        } else if (id == R.id.nav_contact) {
            Toast.makeText(this, "Click on each item to generate call", Toast.LENGTH_LONG).show();
              FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.newfraglayout,new FragHelpLine());
            ft.addToBackStack("stack6");
            ft.commit();
        }/*
        else if (id == R.id.nav_settings) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            FragSettings obj = new FragSettings();
            ft.addToBackStack("stack7");
            ft.replace(R.id.newfraglayout,obj,"setting");
            ft.commit();
        }*/else if (id == R.id.nav_logout) {
            final SharedPreferences.Editor e = sp.edit();

            AlertDialog.Builder ad= new AlertDialog.Builder(this);
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    e.remove("login_key");
                    e.putString("logout_key","logout");
                    e.commit();
                    startActivity(new Intent(NavigationDrawerActivity.this, MainActivity.class));

                }
            });

            ad.setNegativeButton("No",null);
            ad.setMessage("Are you sure to logout ?");
            ad.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater m = getMenuInflater();
        m.inflate(R.menu.bottom_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId()==R.id.like) {
        Intent intent =  new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.facebook.com/merisafety"));

        startActivity(intent);

    }
    else if(item.getItemId()==R.id.settings){

    }
    return super.onOptionsItemSelected(item);
    }

}
