package com.developers.droidteam.merisafety;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_PERMISSIONS = 10;
    private static final int REQUEST_PHONE =1889 ;
    private static final int REQUEST_MSG =1880 ;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int CORSE_LOCATION_PERMISSION_REQUEST_CODE = 2;
    private DatabaseReference mDatabase;
    private DatabaseReference userEnd ;

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

        setContentView(R.layout.activity_main);

        l = findViewById(R.id.l2);
        //        View Flipper

        simpleViewFlipper = (ViewFlipper) findViewById(R.id.main_view_pager);
        for (int i = 0; i < images.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(images[i]);
            simpleViewFlipper.addView(imageView);
        }
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        simpleViewFlipper.setInAnimation(in);
        simpleViewFlipper.setOutAnimation(out);
        simpleViewFlipper.setFlipInterval(4000);
        simpleViewFlipper.setAutoStart(true);


        mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)+ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)+ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)+ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){

        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)||shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)||shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)||shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    Toast.makeText(this, "you need to check permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            requestPermissions(new String[]{Manifest.permission.SEND_SMS,Manifest.permission.CALL_PHONE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_PERMISSIONS);
        }



        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,this/* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        progressBar = findViewById(R.id.progress_bar);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, 1);
            }
        });


    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        GoogleSignInAccount acct;
        String name;
        String email;
        String pUrl;
        public BackgroundTask(MainActivity activity, GoogleSignInAccount googleSignInAccount, String yourname, String youremail, String photoUrl) {
            dialog = new ProgressDialog(activity);
            acct = googleSignInAccount;
            name = yourname;
            email = youremail;
           pUrl = photoUrl;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait.....");
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
                firebaseAuthWithGoogle(acct,name,email,pUrl);


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
            progressBar.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.INVISIBLE);
        }
        else
        {

            progressBar.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.VISIBLE);
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct, final String name, final String email, final String pUrl) {
        Log.d("OAuth", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Log.d("OAuth", "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                          //  updateUI(user);
                            SharedPreferences sp = getSharedPreferences(sp_db, Context.MODE_PRIVATE);
                            final SharedPreferences.Editor et = sp.edit();
                            et.putString("uid",user.getUid().toString());
                            et.putString(n_key,name);
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
                                        createUser(name, email, user.getUid().toString(),pUrl);

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

                                            MainActivity.this.finish();

                                        }
                                        else
                                        {
                                            l.removeAllViews();;
                                            FragmentManager fm = getSupportFragmentManager();
                                            FragmentTransaction ft = fm.beginTransaction();
                                            Frag_verification obj = new Frag_verification();
                                            ft.addToBackStack("stack2");
                                            ft.replace(R.id.l2,obj,"verify");
                                            ft.commit();

                                            MainActivity.this.finish();

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("OAuth", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed. Check your internet",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(false);
                        }

                        // ...
                    }
                });
    }
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("aaa", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();


//           tv.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            String name = acct.getDisplayName();
            String email = acct.getEmail();
            String photoUrl = acct.getPhotoUrl().toString();


            FirebaseUser currentUser = mAuth.getCurrentUser();


               BackgroundTask task = new BackgroundTask(MainActivity.this,acct,name,email,photoUrl);
               task.execute();


        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "Unable to sign in, Maybe internet problem.", Toast.LENGTH_SHORT).show();
        }

    }


    public void createUser(String name, String email, String uid, String pUrl)
    {

    mDatabase = FirebaseDatabase.getInstance().getReference();

    userEnd = mDatabase.child(d_key);

    addUserToFirebase(name,email,uid,pUrl);



       /* FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();


        StorageReference photoRef = storageRef.child("images/"+email+".jpg");

        photoRef.putFile(purl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(con, "Upload successfull", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        Toast.makeText(con, "Upload unsucessfull", Toast.LENGTH_SHORT).show();
                    }
                });
                */
    }

    public static List<UserInfo> getUserInfo(String name, String email, String uid, String pUrl){

        List<UserInfo> userInfos = new ArrayList<>();
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(email);
        userInfo.setName(name);
        userInfo.setUserid(uid);
        userInfo.setPhotoUrl(pUrl);
        userInfo.setMobile(null);
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

                    l.removeAllViews();;
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    Frag_verification obj = new Frag_verification();
                    ft.addToBackStack("stack2");
                    ft.replace(R.id.l2,obj,"Verify");
                    ft.commit();
                }
            });

        }

    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if(requestCode==REQUEST_PERMISSIONS){
            if((grantResults.length>0)&&(grantResults[0]+grantResults[1]+grantResults[2]+grantResults[3])==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permissions successfully granted.", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(this, "permission was not granted", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
}
