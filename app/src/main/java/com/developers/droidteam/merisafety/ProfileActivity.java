package com.developers.droidteam.merisafety;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileActivity extends Activity {

    ImageView imgView;
    ProgressBar progressBar;
    TextView text_name;
    TextView text_email;
    TextView text_number;

    private DatabaseReference mDatabase;
    private DatabaseReference userEnd ;
    private static String UID = null;
    private static final String sp_db = "account_db";
    private static final String l_key = "login_key";
    private static final String d_key = "users";
    private static final String n_key = "name";
    private static final String m_key = "mobile";
    private static final String e_key = "email";
    private static final String p_key = "photoUrl";
    private final int GALLERY_ACTIVITY_CODE=200;
    private final int RESULT_CROP = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        progressBar = findViewById(R.id.progress_profile_pic);
        imgView = findViewById(R.id.profile_pic);
        text_name = findViewById(R.id.p_name);
        text_email = findViewById(R.id.p_email);
        text_number = findViewById(R.id.p_number);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork =  cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();


        final SharedPreferences sp = getSharedPreferences(sp_db, Context.MODE_PRIVATE);
        final String user = sp.getString(l_key, null);
        final String user_name = sp.getString(n_key,null);
        final String user_email = sp.getString(e_key,null);
          UID=user;
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
                startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);

            }});

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
                file = new File(getCacheDir(),"user_pic");
                fileInputStream = new FileInputStream(file);
                Log.i("file","File found");
                imgView.setImageBitmap(BitmapFactory.decodeStream(fileInputStream));

            } catch (IOException e) {
                e.printStackTrace();
                Log.i("file","file not found");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String picturePath = data.getStringExtra("picturePath");
                //perform Crop on the Image Selected from Gallery
                performCrop(picturePath);
            }
        }

        if (requestCode == RESULT_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                progressBar.setVisibility(View.VISIBLE);
                Bundle extras = data.getExtras();
                final Bitmap bitmap = extras.getParcelable("data");
                File file = MainActivity.createCacheOfFile(ProfileActivity.this,"user_pic",bitmap);
                Uri o = Uri.fromFile(file);

                FirebaseStorage storage = FirebaseStorage.getInstance();

                // Create a storage reference from our app
                StorageReference storageRef = storage.getReference();


                StorageReference photoRef = storageRef.child("user_photos/"+UID+".jpg");

                photoRef.putFile( o)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                progressBar.setVisibility(View.INVISIBLE);
                                imgView.setImageBitmap(bitmap);
                                deleteCache(ProfileActivity.this);
                                Toast.makeText(ProfileActivity.this, "Upload successfull", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(ProfileActivity.this, "Upload unsucessfull", Toast.LENGTH_SHORT).show();
                            }
                        });

            }

            }
        }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
