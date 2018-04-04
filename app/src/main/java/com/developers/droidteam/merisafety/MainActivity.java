package com.developers.droidteam.merisafety;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_PERMISSIONS = 10;
    private static final String TAG = "Firebaselogin";
    private static final String WEBCLIENT_ID = "165861330286-ja2b9c1riu1d3uaj99kbuesnijd64ufg.apps.googleusercontent.com";
    private static final String ANDROIDCLIENT_ID = "165861330286-bet681ilog8ok9bps51tl8qddeks9ipk.apps.googleusercontent.com";
    private DatabaseReference mDatabase;
    private DatabaseReference userEnd ;
    private static final int RC_SIGN_IN = 123;
    private final String sp_db = "account_db";
    private final String l_key = "login_key";
    private final String d_key = "users";
    private final String n_key = "name";
    private final String m_key = "mobile";
    private final String e_key = "email";

    FragmentManager fm = getSupportFragmentManager();
    LinearLayout l =null;
    private ViewFlipper simpleViewFlipper;
    int[] images = {R.drawable.round_safety_launcher,R.drawable.m1, R.drawable.m2, R.drawable.m3};
//v
    GoogleApiClient mGoogleApiClient;
    Context con;
    Bundle bs= new Bundle();
    private FirebaseAuth mAuth;

    ProgressBar progressBar;
    SignInButton signInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        con = this;
        setContentView(R.layout.activity_main);
       //hiding action bar
        getSupportActionBar().hide();
        l = findViewById(R.id.l2);
        //        View Flipper

        simpleViewFlipper = (ViewFlipper) findViewById(R.id.main_view_pager);
        for (int image : images) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(image);
            simpleViewFlipper.addView(imageView);
        }
        final Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        simpleViewFlipper.setInAnimation(in);
        simpleViewFlipper.setOutAnimation(out);
        simpleViewFlipper.setFlipInterval(6000);
        simpleViewFlipper.setAutoStart(true);

        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)+ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)+ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)+ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)+ActivityCompat.checkSelfPermission(this,Manifest.permission.RECEIVE_BOOT_COMPLETED)+ActivityCompat.checkSelfPermission(this,Manifest.permission.DISABLE_KEYGUARD)== PackageManager.PERMISSION_GRANTED){

        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)||shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)||shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)||shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)||shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_BOOT_COMPLETED)||shouldShowRequestPermissionRationale(Manifest.permission.DISABLE_KEYGUARD)){
                    Toast.makeText(this, "you need to check permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            requestPermissions(new String[]{Manifest.permission.SEND_SMS,Manifest.permission.CALL_PHONE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.RECEIVE_BOOT_COMPLETED,Manifest.permission.DISABLE_KEYGUARD},REQUEST_PERMISSIONS);
        }


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(WEBCLIENT_ID)
                .requestEmail()
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        progressBar = findViewById(R.id.progress_bar);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isGooglePlayServicesAvailable(MainActivity.this)) {
                    finish();
                }
                updateUI(true);
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, 1);
            }
        });

    }

    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
                googleApiAvailability.makeGooglePlayServicesAvailable(activity);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("aaa", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Snackbar.make(findViewById(R.id.l2),"Signed in with  " + acct.getEmail(), Snackbar.LENGTH_SHORT).show();
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            BackgroundTask task2 = new BackgroundTask(MainActivity.this,acct);
            task2.execute();

//           tv.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));

        } else {
            updateUI(false);
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "unable to sign in to app : "+result.getStatus().toString(), Toast.LENGTH_LONG).show();
        }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        Crashlytics.log("crashed when authenticating with firebase");

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            checkandlogin(user,user.getDisplayName(),user.getEmail(),user.getPhotoUrl().toString());
                            updateUI(true);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.l2), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(false);
                        }

                        // ...
                    }
                });
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        GoogleSignInAccount user;
        public BackgroundTask(MainActivity activity, GoogleSignInAccount user) {
            this.user = user;
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Checking your details with server...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                updateUI(true);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);

               firebaseAuthWithGoogle(user);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

    public void updateUI(Boolean value)
    {
        if(value)
        {
                signInButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

        }
        else
        {
               signInButton.setVisibility(View.VISIBLE);
               progressBar.setVisibility(View.INVISIBLE);

        }

    }
    public void checkandlogin(final FirebaseUser user, final String name, final String email, final String pUrl ){
        SharedPreferences sp = getSharedPreferences(sp_db, Context.MODE_PRIVATE);
        final SharedPreferences.Editor et = sp.edit();
        et.putString("uid",user.getUid());
        et.putString(n_key,name);
        et.putString(e_key,email);
        et.apply();


        mDatabase = FirebaseDatabase.getInstance().getReference();

        userEnd = mDatabase.child(d_key).child(user.getUid());

        userEnd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    Log.d("OAuth","User does not exists");
                    Toast.makeText(MainActivity.this, "Sigining in you to MeriSafety.. Please wait..", Toast.LENGTH_SHORT).show();
                    createUser(name, email, user.getUid(),pUrl);

                }
                else
                {

                    if(dataSnapshot.child(user.getUid()).exists())
                    {

                        et.putString(l_key,user.getUid());
                        et.apply();
                        Toast.makeText(MainActivity.this, "Welcome back to MeriSafey, you are signed in as "+dataSnapshot.child("email").getValue().toString(), Toast.LENGTH_LONG).show();
                        Intent it3=new Intent(MainActivity.this,NavigationDrawerActivity.class);
                        startActivity(it3);

                    }
                    else
                    {
                        l.removeAllViews();;
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        Frag_verification obj = new Frag_verification();
                        ft.replace(R.id.l2,obj,"verify");
                        ft.commit();


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
              Log.d(TAG,databaseError.getMessage());
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1) {
            final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }


    }

    public void createUser(String name, String email, String uid, String pUrl)
    {


        mDatabase = FirebaseDatabase.getInstance().getReference();
        userEnd = mDatabase.child(d_key);
        addUserToFirebase(name,email,uid,pUrl);

        DownloadAndUpload task = new DownloadAndUpload(con,uid,pUrl);
        task.execute();

    }

    public static class DownloadAndUpload extends AsyncTask {

        String UID;
        String URL;
        @SuppressLint("StaticFieldLeak")
        Context con;
        DownloadAndUpload(Context context, String uid, String url){
            UID=uid;
            URL=url;
            con=context;

        }
        @Override
        protected Object doInBackground(Object[] objects) {

            return Uri.fromFile(getBitmapFromURL(con,URL,200,200));
        }

        @Override
        protected void onPostExecute(Object o) {

            FirebaseStorage storage = FirebaseStorage.getInstance();

            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();


            StorageReference photoRef = storageRef.child("user_photos/"+UID+".jpg");

            photoRef.putFile((Uri) o)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(con, "Upload successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            Toast.makeText(con, "Upload unsucessfully", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    public static File getBitmapFromURL(Context context, String src, int h, int w) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return getResizedBitmap(context,myBitmap,h,w);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getResizedBitmap(Context context, Bitmap bm, int newHeight, int newWidth) {
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

      return createCacheOfFile(context,"user_pic",resizedBitmap);

    }
    public static File createCacheOfFile(Context context,String fileName, Bitmap data){


        File file = null;
        FileOutputStream fileOutputStream;
        try{
            file = new File(context.getCacheDir(),fileName);
            fileOutputStream = new FileOutputStream(file);
            data.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Log.i("file","file created successfully");
          
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("file","Cannot create file, failed");
        }
        return file;
    }

    public static List<UserInfo> getUserInfo(String name, String email, String uid, String pUrl){

        List<UserInfo> userInfos = new ArrayList<>();
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(email);
        userInfo.setName(name);
        userInfo.setUserid(uid);
        userInfo.setPhotoUrl(pUrl);
        userInfo.setMobile(null);
        userInfo.setUseloc(true);
        userInfos.add(userInfo);
    return userInfos;
    }

    private void addUserToFirebase(String name, String email, String uid, String pUrl)
    {
        List<UserInfo> userInfoList = getUserInfo(name,email,uid,pUrl);
        for(UserInfo userInfo1 : userInfoList)
        {
            userEnd.child(uid).setValue(userInfo1).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    l.removeAllViews();
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Frag_verification obj = new Frag_verification();
                    ft.replace(R.id.l2,obj,"Verify");
                    ft.commit();
                }
            });

        }

    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if(requestCode==REQUEST_PERMISSIONS){
            if((grantResults.length>0)&&(grantResults[0]+grantResults[1]+grantResults[2]+grantResults[3]+grantResults[4]+grantResults[5])==PackageManager.PERMISSION_GRANTED){
                Log.d("Permissions-merisafety","Permissions successfully granted.");

            }else {
                Log.d("Permissions-merisafety","Permission request has been denied.");
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
}
