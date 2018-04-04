package com.developers.droidteam.merisafety;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class FragHome extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private DatabaseReference mDatabase;
    private DatabaseReference userEndlat ;
    private DatabaseReference userEndlng ;
    private DatabaseReference userPincode;
    private DatabaseReference userLocUse;

    private static final int REQUEST_PHONE =1889 ;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LocationRequest mLocationRequest;
    private boolean mLocationUpdateState;
    private static final int REQUEST_CHECK_SETTINGS = 2;

    private final String cur_key="curloc";
    private final String cur_db = "currentloc";
    private final String sp_db = "account_db";
    private final String d_key = "users";
    private final String lat_key = "lat";
    private final String lng_key = "lng";
    private final String pin_key = "pincode";
    private final String uid_key= "uid";
    private final String use_loc_key = "useloc";
    private static final String i_key = "key";



    View v;
    Context con;

    Button savemeAlert;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con=context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.activity_frag_home,container,false);

        savemeAlert = v.findViewById(R.id.save_me);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        mGoogleApiClient = new GoogleApiClient.Builder(con)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

       }

    @Override
    public void onStart() {
        super.onStart();


        final Button btn = v.findViewById(R.id.save_me);
        final Button anim_btn = v.findViewById(R.id.anim_button);
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        anim_btn.startAnimation(animation);

        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                final Animation myAnim = AnimationUtils.loadAnimation(con,R.anim.bounce);
                MyBounceInterpolator bounceInterpolator = new MyBounceInterpolator(0.1,10);
                myAnim.setInterpolator(bounceInterpolator);
                anim_btn.startAnimation(myAnim);
                myAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        anim_btn.animate().alpha(0.0f).setDuration(300);
                        anim_btn.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        anim_btn.animate().alpha(1.0f).setDuration(300);
                        anim_btn.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(300);
                            con.startActivity(new Intent(con,MapsActivity.class).putExtra("saveme","yes"));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                view.clearAnimation();
                return false;
            }
        });



        Intent intent = new Intent(con, AlertService.class);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(con);
        boolean hot_key = sharedPref.getBoolean(SettingsActivity.KEY_HOT,true);
        if(hot_key){
            //Starting the service for the hot key
            con.startService(intent);
        }
        else{
            con.stopService(intent);
        }


    }

    @Override
    public void onPause() {
        super.onPause();
       }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                mLocationUpdateState = true;
            }
        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {


        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(7000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest).setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                // 4
                case LocationSettingsStatusCodes.SUCCESS:
                    mLocationUpdateState = true;
                    break;
                // 5
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);

                    }catch(IntentSender.SendIntentException e) {
                     Log.d("Location updates",e.getMessage());
                    }
                    break;
                // 6
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
            }
        });

        if (ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                LatLng mylatlng = new LatLng(lat, lng);

                if (ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                List<Address> list2 = null;
                Geocoder geocoder1 = new Geocoder(con);

                try {
                    list2 = geocoder1.getFromLocation(lat, lng, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if(list2 != null)
                {
                    Address address1 = list2.get(0);
                    SharedPreferences sp = con.getSharedPreferences(cur_db,MODE_PRIVATE);
                    SharedPreferences.Editor et = sp.edit();
                    et.putString(cur_key,address1.getAddressLine(0));
                    et.apply();

                    SendLocation sendLocation= (SendLocation) con;
                    sendLocation.communicate(address1.getAddressLine(0));

                    SharedPreferences sp1 = con.getSharedPreferences(sp_db, MODE_PRIVATE);
                    SharedPreferences.Editor et1 = sp1.edit();
                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    String uid =  sp1.getString(uid_key,null);
                    if(uid!=null)
                    {
                        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(con);
                        boolean locationPref = sharedPref.getBoolean(SettingsActivity.KEY_PREF_LOCATION,true);
                        boolean shareLocationPref =sharedPref.getBoolean(SettingsActivity.KEY_PREF_SHARE_LOCATION,true);
                        if(locationPref){

                            userEndlat = mDatabase.child(d_key).child(uid).child(lat_key);
                            userEndlat.setValue(lat);
                            userEndlng = mDatabase.child(d_key).child(uid).child(lng_key);
                            userEndlng.setValue(lng);
                            userPincode = mDatabase.child(d_key).child(uid).child(pin_key);
                            userPincode.setValue(address1.getPostalCode());
                            et1.putString(pin_key,address1.getPostalCode());
                            et1.apply();

                            if(!shareLocationPref){
                                userLocUse = mDatabase.child(d_key).child(uid).child(use_loc_key);
                                userLocUse.setValue(false);

                            }
                        }
                    }
                }
            }
        });

    }
    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public interface SendLocation{
        void communicate(String address);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
