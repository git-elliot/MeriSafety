package com.developers.droidteam.merisafety;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_PERMISSIONS = 10;
    private static final int REQUEST_PHONE =1889 ;
    private static final int REQUEST_MSG =1880 ;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int CORSE_LOCATION_PERMISSION_REQUEST_CODE = 2;

    FragmentManager fm = getSupportFragmentManager();

    GoogleApiClient mGoogleApiClient;
    Context con;
    Bundle bs= new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Inflate the layout for this fragment
        Button bbb =(Button)findViewById(R.id.b1);
        Button bb1 =(Button)findViewById(R.id.b2);
        bbb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.l2, new Frag_login());
                ft.addToBackStack("A");
                ft.commit();
            }
        });
        bb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.l2, new Frag_signup());
                ft.addToBackStack("A");
                ft.commit();
            }
        });

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
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,this/* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, 1);
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
            Toast.makeText(this,acct.getDisplayName()+"\n"+acct.getEmail()+"\n"+acct.getId(), Toast.LENGTH_SHORT).show();
            bs.putString("name",acct.getDisplayName());
            bs.putString("email",acct.getEmail());
            bs.putString("purl",acct.getPhotoUrl().toString());

//           tv.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            String name = acct.getDisplayName();
            String email = acct.getEmail();
            Uri purl = acct.getPhotoUrl();

            SharedPreferences sp = getSharedPreferences("account_db", Context.MODE_PRIVATE);
            SharedPreferences.Editor et = sp.edit();
            et.putString("email",email);
            et.commit();

            createUser(name,email,purl);



        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(this, "OUT", Toast.LENGTH_SHORT).show();
        }

    }
    public void createUser(String name, String email, Uri purl)
    {
        FirebaseStorage storage = FirebaseStorage.getInstance();

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
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if(requestCode==REQUEST_PERMISSIONS){
            if((grantResults.length>0)&&(grantResults[0]+grantResults[1]+grantResults[2]+grantResults[3])==PackageManager.PERMISSION_GRANTED){

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
