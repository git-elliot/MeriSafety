package com.developers.droidteam.merisafety;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_MSG =1880 ;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private DatabaseReference mDatabase;
    private DatabaseReference userEnd ;
    ImageView iv;
    ProgressBar progressBar;
    LocationRequest locationRequest = null;

    private static final String i_key = "key";
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

        Toast.makeText(this, "Long press these alert buttons to know about them", Toast.LENGTH_LONG).show();

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
        final TextView tv_name =  hView.findViewById(R.id.nav_drawer_name);

        final TextView tv_user =  hView.findViewById(R.id.nav_drawer_user);

        progressBar=hView.findViewById(R.id.prog_user);

        iv =  hView.findViewById(R.id.imageView);

        final SharedPreferences sp = getSharedPreferences(sp_db, Context.MODE_PRIVATE);
        final String user = sp.getString(l_key, null);

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

    }

    @Override
    protected void onStart() {
        super.onStart();
        Button lowAlert = findViewById(R.id.lowalert);

        lowAlert.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(NavigationDrawerActivity.this, "This alert will only call your guardian", Toast.LENGTH_LONG).show();
                return false;
            }
        });
        Button highAlert = findViewById(R.id.highalert);
        highAlert.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(NavigationDrawerActivity.this, "This alert will call and message your guardian", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        Button adAlert = findViewById(R.id.advancealert);

        adAlert.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(NavigationDrawerActivity.this, "This alert will call, mail and message your guardian", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        Button selfDef = findViewById(R.id.self_defence);

        selfDef.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(NavigationDrawerActivity.this, "This will help you learn some basics moves.", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        Button savemeAlert = findViewById(R.id.save_me);

        savemeAlert.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(NavigationDrawerActivity.this, "This alert will call, mail and message your guardian and show the location of other users also.", Toast.LENGTH_LONG).show();
                return false;
            }
        });


        Intent i = new Intent(NavigationDrawerActivity.this, AlertService.class);

        startService(i);



    }

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
            return getBitmapFromURL(imageURL);
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

        return resizedBitmap;
    }


        public void fun(View v)
    {
        if(R.id.lowalert==v.getId()){

            Intent i = new Intent(this,SaveMeActivity.class);
            i.putExtra(i_key,v.getId());
            startActivity(i);

        }
        if(R.id.save_me==v.getId())
        {
            Intent i = new Intent(this,SaveMeActivity.class);
            i.putExtra("key",v.getId());
            startActivity(i);
        }
        if(R.id.highalert==v.getId())
        {
            Intent i = new Intent(this,SaveMeActivity.class);
            i.putExtra("key",v.getId());
            startActivity(i);
        }
        if(R.id.advancealert==v.getId())
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
        }/*
        else if (id == R.id.nav_settings) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            FragSettings obj = new FragSettings();
            ft.addToBackStack("stack7");
            ft.replace(R.id.newfraglayout,obj,"setting");
            ft.commit();
        }else if (id == R.id.nav_logout) {
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
