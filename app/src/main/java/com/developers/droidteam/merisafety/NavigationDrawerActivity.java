package com.developers.droidteam.merisafety;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragHome.SendLocation {

    private DatabaseReference mDatabase;
    private DatabaseReference userEnd ;
    ImageView iv;
    ProgressBar progressBar;
    Toolbar toolbar=null;
    TextView textbar = null;
    boolean toggle=true;
    private static final String appURL = "https://play.google.com/store/apps/details?id=com.developers.droidteam.merisafety";

    private static final String sp_db = "account_db";
    private static final String l_key = "login_key";
    private static final String d_key = "users";
    private static final String n_key = "name";
    private static final String m_key = "mobile";
    private static final String e_key = "email";
    private static final String p_key = "photoUrl";
    private static final String TAG = "NavigationDrawerActivit";
    BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragHome obj = new FragHome();
        ft.add(R.id.newfraglayout, obj, "homepage");
        ft.commit();

        br = new CheckConnectivity();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(br,intentFilter);


        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork =  cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textbar = findViewById(R.id.textbar);
        textbar.setSelected(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        Log.d(TAG,"Token : "+FirebaseInstanceId.getInstance().getToken());
        //**************************navigation drawer data**********************
        final TextView tv_name =  hView.findViewById(R.id.nav_drawer_name);

        final TextView tv_user =  hView.findViewById(R.id.nav_drawer_user);

        progressBar=hView.findViewById(R.id.prog_user);
        progressBar.setVisibility(View.VISIBLE);

        iv =  hView.findViewById(R.id.imageView);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(NavigationDrawerActivity.this,ProfileActivity.class));
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        final SharedPreferences sp = getSharedPreferences(sp_db, Context.MODE_PRIVATE);
        final String user = sp.getString(l_key, null);
        final String user_name = sp.getString(n_key,null);
        final String user_email = sp.getString(e_key,null);
        final String user_purl = sp.getString(p_key,null);


        if(isConnected){

            mDatabase = FirebaseDatabase.getInstance().getReference();

            userEnd = mDatabase.child(d_key).child(user).child(n_key);

            userEnd.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        tv_name.setText(dataSnapshot.getValue().toString());

                        SharedPreferences.Editor et = sp.edit();
                        et.putString(n_key,dataSnapshot.getValue().toString());
                        et.apply();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            userEnd = mDatabase.child(d_key).child(user).child(e_key);

            userEnd.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        tv_user.setText(dataSnapshot.getValue().toString());
                        SharedPreferences.Editor et = sp.edit();
                        et.putString(e_key,dataSnapshot.getValue().toString());
                        et.apply();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            userEnd = mDatabase.child(d_key).child(user).child(p_key);

            userEnd.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {

                        String imageURL = dataSnapshot.getValue().toString();
                        SharedPreferences.Editor et = sp.edit();
                        et.putString(p_key,imageURL);
                        et.apply();

                       setImageView(iv,"user_photos/"+user+".jpg",progressBar);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
              tv_name.setText(user_name);
              tv_user.setText(user_email);
            File file;
            FileInputStream fileInputStream;
            try{
                file = new File(getCacheDir(),"user_pic");
                fileInputStream = new FileInputStream(file);
                Log.i("file","File found");
                iv.setImageBitmap(BitmapFactory.decodeStream(fileInputStream));



            } catch (IOException e) {
                e.printStackTrace();
                Log.i("file","file not found");
            }

        }

        FirebaseMessaging.getInstance().subscribeToTopic("alert");

    }

    void turnOffGps(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
        //turnOffGps();
    }

    public void setImageView(ImageView i, String dir, final ProgressBar progressBar)
    {

        // Reference to an image file in Cloud Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        StorageReference photoRef = storageRef.child(dir);

        GlideApp.with(i.getContext() /* context */)
                .load(photoRef)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d("glide","Photo load failed");
                        progressBar.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        Log.d("glide","successfully downloaded");
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .dontAnimate()
                .into(i);

    }


    @Override
    public void communicate(String address) {

        if(toggle)
        {
            textbar.setText(address);
            toggle = false;
        }else
        {
            textbar.setText("");
            toggle = true;
        }
    }
    public void fun(View v)
    {
        Context con = getApplicationContext();

        if(R.id.save_me==v.getId())
        {
            Button savemeAlert = findViewById(R.id.save_me);
            final Animation myAnim = AnimationUtils.loadAnimation(con,R.anim.bounce);
            MyBounceInterpolator bounceInterpolator = new MyBounceInterpolator(0.2,10);
              myAnim.setInterpolator(bounceInterpolator);
            savemeAlert.startAnimation(myAnim);

            Snackbar.make(findViewById(R.id.newfraglayout),"Long press to activate it.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            showRateandReview();
        } else {
            super.onBackPressed();
        }
    }
    public void showRateandReview(){
        SharedPreferences sp = getSharedPreferences("ratedb",MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
        alertdialog.setTitle("Rate and Review");
        alertdialog.setMessage("Like this app, rate and review our app on the google play store.");
        alertdialog.setPositiveButton("Rate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appURL)));
        }});
        alertdialog.setNegativeButton("No", null);
        alertdialog.setNeutralButton("Never ask me again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                et.putBoolean("neverask",true);
                et.apply();
            }
        });
        if(!sp.getBoolean("neverask",false)){
            alertdialog.show();
        }

    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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

        }else if(R.id.nav_self_defence==id) {

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            SelfDefenceFragment obj = new SelfDefenceFragment();
            ft.addToBackStack("stack2");
            ft.replace(R.id.newfraglayout,obj,"guardian");
            ft.commit();

        }
        else if (id == R.id.aboutapp) {
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
        }
        else if( id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Download Droid Watch app from play store "+appURL;
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Droid Watch app by Paras khandelwal");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        }
        else if (id == R.id.nav_police) {

            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.addToBackStack("stack5");
            FragHelp obj = new FragHelp();
            ft.replace(R.id.newfraglayout,obj,"help");
            ft.commit();

        }else if(id == R.id.nav_nearby)
        {
         startActivity(new Intent(NavigationDrawerActivity.this,MapsActivity.class).putExtra("saveme","no"));
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
        }
        else if (id == R.id.nav_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        }
        /*else if (id == R.id.nav_logout) {
            final SharedPreferences.Editor e = sp.edit();

            AlertDialog.Builder ad= new AlertDialog.Builder(this);
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    e.remove(l_key);
                    e.putString("logout_key","logout");
                    e.commit();
                    startActivity(new Intent(NavigationDrawerActivity.this, MainActivity.class));

                }
            });

            ad.setNegativeButton("No",null);
            ad.setMessage("Are you sure to logout ?");
            ad.show();
        }
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
