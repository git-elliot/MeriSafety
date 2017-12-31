package com.developers.droidteam.merisafety;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    Context con;
    View v;
    ImageView imgView;
    ProgressBar progressBar;
    TextView text_name;
    TextView text_email;
    TextView text_number;
    private DatabaseReference mDatabase;
    private DatabaseReference userEnd ;
    private static final String sp_db = "account_db";
    private static final String l_key = "login_key";
    private static final String d_key = "users";
    private static final String n_key = "name";
    private static final String m_key = "mobile";
    private static final String e_key = "email";
    private static final String p_key = "photoUrl";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con=context;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        progressBar = v.findViewById(R.id.progress_profile_pic);
        imgView = v.findViewById(R.id.profile_pic);
        text_name = v.findViewById(R.id.p_name);
        text_email = v.findViewById(R.id.p_email);
        text_number = v.findViewById(R.id.p_number);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork =  cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();


        final SharedPreferences sp = con.getSharedPreferences(sp_db, Context.MODE_PRIVATE);
        final String user = sp.getString(l_key, null);
        final String user_name = sp.getString(n_key,null);
        final String user_email = sp.getString(e_key,null);

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        
        if(isConnected){

            mDatabase = FirebaseDatabase.getInstance().getReference();

            userEnd = mDatabase.child(d_key).child(user).child(n_key);

            userEnd.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        text_name.setText(dataSnapshot.getValue().toString());

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
                        text_email.setText(dataSnapshot.getValue().toString());
                        SharedPreferences.Editor et = sp.edit();
                        et.putString(e_key,dataSnapshot.getValue().toString());
                        et.apply();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            new NavigationDrawerActivity().setImageView(imgView,"user_photos/"+user+".jpg",progressBar);

            userEnd = mDatabase.child(d_key).child(user).child(m_key);

            userEnd.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {

                        text_number.setText(dataSnapshot.getValue().toString());
                        SharedPreferences.Editor et = sp.edit();
                        et.putString(m_key,dataSnapshot.getValue().toString());
                        et.apply();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            text_name.setText(user_name);
            text_email.setText(user_email);
            File file;
            FileInputStream fileInputStream;
            try{
                file = new File(con.getCacheDir(),"user_pic");
                fileInputStream = new FileInputStream(file);
                Log.i("file","File found");
                imgView.setImageBitmap(BitmapFactory.decodeStream(fileInputStream));

            } catch (IOException e) {
                e.printStackTrace();
                Log.i("file","file not found");
            }
        }
    }
}
