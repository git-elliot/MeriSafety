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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
        final String[] name = new String[1];
        final String[] email = new String[1];
        final String[] number = new String[1];

        progressBar = findViewById(R.id.progress_profile_pic);
        imgView = findViewById(R.id.profile_pic);
        text_name = findViewById(R.id.p_name);
        text_email = findViewById(R.id.p_email);
        text_number = findViewById(R.id.p_number);

        final LinearLayout displayLayout = findViewById(R.id.profile_display_layout);
        final LinearLayout editLayout= findViewById(R.id.profile_edit_layout);

        final EditText editText_name = findViewById(R.id.edit_name);

        final EditText editText_email = findViewById(R.id.edit_email);

        final EditText editText_number = findViewById(R.id.edit_number);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork =  cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();


        final SharedPreferences sp = getSharedPreferences(sp_db, Context.MODE_PRIVATE);
        final String user = sp.getString(l_key, null);
        final String user_name = sp.getString(n_key,null);
        final String user_email = sp.getString(e_key,null);
          UID=user;
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, "Upload square picture for best fit.", Toast.LENGTH_SHORT).show();
                  Intent intent = new Intent();
                  intent.setType("image/*");
                  intent.setAction(Intent.ACTION_GET_CONTENT);
                  startActivityForResult(Intent.createChooser(intent,"Choose picture"),0);
            }});


        Button edit_details = findViewById(R.id.edit_details);
        edit_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isConnected){

                    displayLayout.animate().alpha(0.0f).setDuration(500);
                    displayLayout.setVisibility(View.GONE);
                    editLayout.animate().alpha(1.0f).setDuration(500);
                    editLayout.setVisibility(View.VISIBLE);
                    editText_name.setText(name[0]);
                    editText_email.setText(email[0]);
                    editText_number.setText(number[0]);

                }else{
                    Snackbar.make(view,"Internet Required",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }
            }
        });
        Button back_to_display = findViewById(R.id.back_to_display);
        back_to_display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editLayout.animate().alpha(0.0f).setDuration(500);
                editLayout.setVisibility(View.GONE);
                displayLayout.animate().alpha(1.0f).setDuration(500);
                displayLayout.setVisibility(View.VISIBLE);

            }
        });

        Button submit_details = findViewById(R.id.submit_details);
        submit_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final String name = editText_name.getText().toString();
               final String email = editText_email.getText().toString();
               final String number = editText_number.getText().toString();
               if(isConnected){

                   mDatabase = FirebaseDatabase.getInstance().getReference();
                   userEnd = mDatabase.child(d_key).child(user).child(m_key);
                   userEnd.setValue(number).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           Toast.makeText(ProfileActivity.this, "Details changed successfully", Toast.LENGTH_SHORT).show();
                           userEnd = mDatabase.child(d_key).child(user).child(n_key);
                           userEnd.setValue(name);
                           userEnd = mDatabase.child(d_key).child(user).child(e_key);
                           userEnd.setValue(email);

                           editLayout.animate().alpha(0.0f).setDuration(500);
                           editLayout.setVisibility(View.GONE);
                           displayLayout.animate().alpha(1.0f).setDuration(500);
                           displayLayout.setVisibility(View.VISIBLE);
                           text_name.setText(name);
                           text_email.setText(email);
                           text_number.setText(number);

                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(ProfileActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                       }
                   });


               }else {
                   Snackbar.make(view,"Internet Required",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
               }
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
                        name[0] =dataSnapshot.getValue().toString();
                        text_name.setText(name[0]);

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
                        email[0] =dataSnapshot.getValue().toString();
                        text_email.setText(email[0]);
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
                        number[0] =dataSnapshot.getValue().toString();
                        text_number.setText(number[0]);
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
       if(requestCode==0){
            if(data!=null){
                progressBar.setVisibility(View.VISIBLE);
                try {
                    Bitmap b = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),data.getData());
                    uploadBitmap(b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        }
        public void uploadBitmap(Bitmap bitmap){

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
                            Toast.makeText(ProfileActivity.this, "Changed successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(ProfileActivity.this, "Unable to change, try again", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }
}
