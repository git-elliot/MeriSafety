package com.developers.droidteam.merisafety;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.MemoryFile;
import android.preference.PreferenceManager;
import android.support.constraint.solver.Cache;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.engine.cache.MemoryCacheAdapter;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.CharBuffer;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragHome.SendLocation {

    private DatabaseReference mDatabase;
    private DatabaseReference userEnd ;
    ImageView iv;
    ProgressBar progressBar;
    Toolbar toolbar=null;
    TextView textbar = null;
    boolean toggle=true;
    private static final String sp_db = "account_db";
    private static final String l_key = "login_key";
    private static final String d_key = "users";
    private static final String n_key = "name";
    private static final String m_key = "mobile";
    private static final String e_key = "email";
    private static final String p_key = "photoUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragHome obj = new FragHome();
        ft.add(R.id.newfraglayout, obj, "homepage");
        ft.commit();

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork =  cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();

        Toast.makeText(this, "Long press these alert buttons to activate them", Toast.LENGTH_LONG).show();


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

        //**************************havigation drawer data**********************
        final TextView tv_name =  hView.findViewById(R.id.nav_drawer_name);

        final TextView tv_user =  hView.findViewById(R.id.nav_drawer_user);

        progressBar=hView.findViewById(R.id.prog_user);

        iv =  hView.findViewById(R.id.imageView);

        final SharedPreferences sp = getSharedPreferences(sp_db, Context.MODE_PRIVATE);
        final String user = sp.getString(l_key, null);
        final String user_name = sp.getString(n_key,null);
        final String user_email = sp.getString(e_key,null);

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
                        FetchBitmap task = new FetchBitmap(NavigationDrawerActivity.this,imageURL,iv,progressBar);
                        task.execute();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
              tv_name.setText(user_name);
              tv_user.setText(user_email);
            FetchBitmap task = new FetchBitmap(NavigationDrawerActivity.this,null,iv,progressBar);
            task.execute();

        }


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

    @SuppressLint("StaticFieldLeak")
    private class FetchBitmap extends AsyncTask<Void, Void, Bitmap> {
        String imageURL;
        ImageView imgView;
        ProgressBar progressBar;
        public FetchBitmap(Activity activity, String imgURL, ImageView imageView,ProgressBar progressBar1) {
            imageURL = imgURL;
            imgView = imageView;
           progressBar= progressBar1;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(View.INVISIBLE);
            imgView.setImageBitmap(result);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {

            File file;
            FileInputStream fileInputStream;
            try{
                file = new File(getCacheDir(),"user_pic");
                fileInputStream = new FileInputStream(file);
                Log.i("file","File found");
                return BitmapFactory.decodeStream(fileInputStream);

             } catch (IOException e) {
                e.printStackTrace();
                Log.i("file","file not found");
                return getBitmapFromURL(imageURL);

            }

        }
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return getResizedBitmap(myBitmap,70,70);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        createCacheOfFile("user_pic",resizedBitmap);
        return resizedBitmap;
    }
    public void createCacheOfFile(String fileName, Bitmap data){
        File file;
        FileOutputStream fileOutputStream;
        try{
             file = new File(getCacheDir(),fileName);
             fileOutputStream = new FileOutputStream(file);
             data.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
             fileOutputStream.flush();
             fileOutputStream.close();
             Log.i("file","file created successfully");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("file","Cannot create file, failed");
        }
    }


        public void fun(View v)
    {
        if(R.id.lowalert==v.getId()){

            Toast.makeText(NavigationDrawerActivity.this, "This alert will only call your guardian", Toast.LENGTH_LONG).show();

        }
        if(R.id.save_me==v.getId())
        {
            Toast.makeText(NavigationDrawerActivity.this, "This alert will call, mail and message your guardian and show the location of other users also.", Toast.LENGTH_LONG).show();
        }
        if(R.id.highalert==v.getId())
        {

            Toast.makeText(NavigationDrawerActivity.this, "This alert will call and message your guardian", Toast.LENGTH_LONG).show();

        }
        if(R.id.advancealert==v.getId())
        {

            Toast.makeText(NavigationDrawerActivity.this, "This alert will call, mail and message your guardian", Toast.LENGTH_LONG).show();

        }
        if(R.id.self_defence==v.getId())
        {

            Toast.makeText(NavigationDrawerActivity.this, "This will help you learn some basics moves.", Toast.LENGTH_LONG).show();

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
