package com.developers.droidteam.merisafety;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener {

    private static final String GOOGLE_API_KEY = "AIzaSyDQ94rd1-xHgj_sSBNbusWx4QQl8A56cik";
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LocationRequest mLocationRequest;
    private boolean mLocationUpdateState;
    private static final int PLACE_PICKER_REQUEST = 3;
    // 2
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private String guarEmail = null;
    private ProgressBar progressBar;
    private DatabaseReference mDatabase;
    private DatabaseReference peopleEnd ;
    private DatabaseReference userEnd ;

    private final String l_key = "login_key";
    private final String cur_key="curloc";
    private final String cur_db = "currentloc";
    private final String sp_db = "account_db";
    private final String d_key = "users";
    private final String lat_key = "lat";
    private final String n_key = "name";
    private final String m_key = "mobile";
    private final String e_key = "email";
    private final String lng_key = "lng";
    private final String pin_key = "pincode";
    private final String uid_key= "uid";
    private final String p_key = "photoUrl";
    private final String use_loc_key = "useloc";
    private boolean nearbySet=false;
    private final String ge_key="gemail";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent i = getIntent();
        String saveme=  i.getStringExtra("saveme");
        if(saveme.equals("yes")&&saveme!=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.RED);
                window.setNavigationBarColor(Color.RED);
            }

        }
        progressBar = findViewById(R.id.updateMap);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        createLocationRequest();

        Button nearby_people = findViewById(R.id.people);
        Button nearby_police = findViewById(R.id.police);
        Button nearby_hospitals = findViewById(R.id.hospitals);
        Button rescued = findViewById(R.id.rescued);

        rescued.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.rescued_color));
                    window.setNavigationBarColor(getResources().getColor(R.color.rescued_color));
                }
            }
        });

        nearby_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             mMap.clear();

                Toast.makeText(MapsActivity.this, "Finding nearby peoples", Toast.LENGTH_SHORT).show();
               if(mLastLocation.getLongitude()!=0){
                   placeMarkerOnMap(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
               }
                placeNearbyPeoples();

            }
        });

        nearby_police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();

                Toast.makeText(MapsActivity.this, "Finding nearby police stations", Toast.LENGTH_SHORT).show();
                if(mLastLocation.getLongitude()!=0){
                    placeMarkerOnMap(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                }

                performSearch("police",mLastLocation.getLatitude(),mLastLocation.getLongitude(),5000,mMap);

            }
        });

        nearby_hospitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();

                Toast.makeText(MapsActivity.this, "Finding nearby Hospitals", Toast.LENGTH_SHORT).show();
                if(mLastLocation.getLongitude()!=0){
                    placeMarkerOnMap(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                }
                performSearch("hospital",mLastLocation.getLatitude(),mLastLocation.getLongitude(),5000,mMap);

            }
        });
    }


    public void performSearch(String type, double latitude, double longitude, int PROXIMITY_RADIUS, GoogleMap googleMap)
    {


        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask(this,type);
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
        googlePlacesReadTask.execute(toPass);


    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();

        if (mLocationUpdateState) {
            startLocationUpdates();
        }


    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 2
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 3
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            // return;
        } else {
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            LocationAvailability locationAvailability =
                    LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
            if (null != locationAvailability && locationAvailability.isLocationAvailable()) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation
                            .getLongitude());
                    //add pin at user's location
                    placeMarkerOnMap(currentLocation);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,13));
                }
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;

        if(mLastLocation!=null)
        {
            mMap.clear();
            progressBar.setVisibility(View.VISIBLE);

            placeMarkerOnMap(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            placeNearbyPeoples();
        }
    }

    public void placeNearbyPeoples()
    {
        SharedPreferences sp = getSharedPreferences(sp_db, Context.MODE_PRIVATE);
        final String user = sp.getString(l_key, null);
        final String currentpin = sp.getString(pin_key,null);


        peopleEnd = mDatabase.child(d_key);

        peopleEnd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot currentsnapshot : dataSnapshot.getChildren())
                {
                    if(!currentsnapshot.getKey().equals(user))
                    {
                      String pincode =currentsnapshot.child(pin_key).getValue().toString();
                        boolean useLoc = (boolean) currentsnapshot.child(use_loc_key).getValue();
                        if(guarEmail.equals(currentsnapshot.child(e_key).getValue().toString())){

                            placePeopleWindow(currentsnapshot.child(n_key).getValue().toString(),currentsnapshot.child(m_key).getValue().toString(),currentsnapshot.child(lat_key).getValue().toString(),currentsnapshot.child(lng_key).getValue().toString(),currentsnapshot.child(p_key).getValue().toString(),currentsnapshot.child(e_key).getValue().toString());
                        }else if(pincode.equals(currentpin)&&useLoc)
                        {
                            placePeopleWindow(currentsnapshot.child(n_key).getValue().toString(),currentsnapshot.child(m_key).getValue().toString(),currentsnapshot.child(lat_key).getValue().toString(),currentsnapshot.child(lng_key).getValue().toString(),currentsnapshot.child(p_key).getValue().toString(),currentsnapshot.child(e_key).getValue().toString());

                        }
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void placePeopleWindow(String name, String mobile,String lat, String lng,String pUrl,String email)
    {
        double mylat = Double.parseDouble(lat);
        double mylng = Double.parseDouble(lng);
        LatLng mylatlng = new LatLng(mylat,mylng);
        placeMarkerOnMapPeople(name,mobile,mylatlng,pUrl,email);
    }

    private class FetchBitmap extends AsyncTask<Void, Void, Bitmap> {
        String imageURL;
        LatLng loc ;
        String myName;
        GoogleMap myMap;
        String gEmail ;
        public FetchBitmap(String imgURL, GoogleMap mMap, LatLng location, String name,String email) {
            imageURL = imgURL;
            loc = location;
            myName = name;
            myMap = mMap;
            gEmail=email;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            ImageView mImageView = new ImageView(getApplicationContext());
            IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
            mIconGenerator.setContentView(mImageView);
            mImageView.setImageBitmap(result);
            Bitmap iconBitmap = mIconGenerator.makeIcon();

            MarkerOptions markerOptions = new MarkerOptions().position(loc).icon(BitmapDescriptorFactory.fromBitmap(iconBitmap));
            String titleStr = getAddress(loc);  // add these two lines
           if(guarEmail!=null&&guarEmail.equals(gEmail))
           {

               markerOptions.title("Guardian: "+myName).snippet(titleStr);
           }
           else
           {
                markerOptions.title(myName).snippet(titleStr);
           }
            // 2
            LatLng l = new LatLng(loc.latitude,loc.longitude);
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l,12));
            myMap.addMarker(markerOptions);
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
            return getResizedBitmap(myBitmap,100,100);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static Bitmap createDrawableFromView(Context context, View view, Bitmap bitmap) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(100,100));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
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


    protected void placeMarkerOnMap(LatLng location) {
        // 1
        final LatLng loc = location;
        final SharedPreferences sp = getSharedPreferences(sp_db, Context.MODE_PRIVATE);
        String userid = sp.getString(l_key,null);

        userEnd = mDatabase.child(d_key).child(userid).child(p_key);

        userEnd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    FetchBitmap task = new FetchBitmap(dataSnapshot.getValue().toString(),mMap,loc,"You",null);
                    task.execute();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userEnd = mDatabase.child((d_key)).child(userid).child(userid).child(e_key);
        userEnd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    guarEmail = dataSnapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    protected void placeMarkerOnMapPeople(String name, String mobile, LatLng location, String pUrl,String email) {
        // 1
        FetchBitmap task = new FetchBitmap(pUrl,mMap,location,name,email);
        task.execute();

    }

    private String getAddress(LatLng latLng) {
        // 1
        Geocoder geocoder = new Geocoder(this);
        String addressText = "";
        List<Address> addresses = null;
        Address address = null;
        try {
            // 2
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            // 3
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    addressText += (i == 0) ? address.getAddressLine(i) : ("\n" + address.getAddressLine(i));
                }
            }
        } catch (IOException e) {
        }
        return addressText;
    }
    protected void startLocationUpdates() {
        //1
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        //2
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                this);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        // 2
        mLocationRequest.setInterval(500000);
        // 3
        mLocationRequest.setFastestInterval(25000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    // 4
                    case LocationSettingsStatusCodes.SUCCESS:
                        mLocationUpdateState = true;
                        startLocationUpdates();
                        break;
                    // 5
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    // 6
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    // 2
    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    // 3
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mLocationUpdateState) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                mLocationUpdateState = true;
                startLocationUpdates();
            }
        }
    }
}