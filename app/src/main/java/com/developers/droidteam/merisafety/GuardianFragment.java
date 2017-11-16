package com.developers.droidteam.merisafety;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GuardianFragment extends Fragment {

    private DatabaseReference mDatabase;
    private DatabaseReference guarEnd ;


    View v;
    Context con;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con= context;
    }

    public GuardianFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_guardian, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences sp = con.getSharedPreferences("account_db", Context.MODE_PRIVATE);
        final String user = sp.getString("login_key", null);

        final ImageView img = v.findViewById(R.id.guar_photo);
        final TextView tname = (TextView) v.findViewById(R.id.guar_name);
        final TextView tphone = (TextView) v.findViewById(R.id.guar_phone);
        final TextView temail = (TextView) v.findViewById(R.id.guar_email);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        guarEnd = mDatabase.child("users").child(user).child(user);

        guarEnd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot currentsnapshot : dataSnapshot.getChildren())
                {
                   if(currentsnapshot.getKey().equals("name"))
                   {
                       tname.setText(dataSnapshot.child("name").getValue().toString());
                   }
                   else if(currentsnapshot.getKey().equals("mobile"))
                   {
                       temail.setText(dataSnapshot.child("mobile").getValue().toString());

                   }
                   else if(currentsnapshot.getKey().equals("email"))
                   {
                       tphone.setText(dataSnapshot.child("email").getValue().toString());
                       setGuardianPhoto(dataSnapshot.child("email").getValue().toString(),dataSnapshot.child("mobile").getValue().toString(),user,img);
                       Log.d("photoUrl",dataSnapshot.child("email").getValue().toString()+dataSnapshot.child("mobile").getValue().toString()+user);
                   }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button change = (Button) v.findViewById(R.id.add_new_guar);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder ad= new AlertDialog.Builder(con);
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        guarEnd = mDatabase.child("users").child(user).child(user);
                        guarEnd.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(con, "Guardian Deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        });

                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        Frag_guardian obj = new Frag_guardian();
                        ft.addToBackStack("stack2");
                        ft.replace(R.id.newfraglayout,obj,"guardian");
                        ft.commit();
                    }
                });

                ad.setNegativeButton("No",null);
                ad.setMessage("This will edit your current guardian, Are you sure ?");
                ad.show();

            }
        });


    }

    public void setGuardianPhoto(String email1, String mobile1, String firebaseUser, ImageView imageView)
    {
        final String user = firebaseUser;
        final String email = email1;
        final String mobile = mobile1;
        final ImageView img = imageView;
        guarEnd = mDatabase.child("users");

        guarEnd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot currentsnapshot : dataSnapshot.getChildren())
                {
                    if(!currentsnapshot.getKey().equals(user))
                    {
                        try {

                            String onEmail =dataSnapshot.child("email").getValue().toString();
                            String onMobile =dataSnapshot.child("mobile").getValue().toString();

                            if(onEmail.equals(email))
                            {
                                FetchBitmap task = new FetchBitmap((Activity) con,dataSnapshot.child("photoUrl").getValue().toString(),img);
                                task.execute();

                            }
                        }catch(NullPointerException e)
                        {
                            Toast.makeText(con, "Unable to update picture.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private class FetchBitmap extends AsyncTask<Void, Void, Bitmap> {
        String imageURL;
        ImageView imgView;

        public FetchBitmap(Activity activity, String imgURL, ImageView imageView) {
            imageURL = imgURL;
            imgView = imageView;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Bitmap result) {
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
            return getResizedBitmap(myBitmap,220,220);
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

}
