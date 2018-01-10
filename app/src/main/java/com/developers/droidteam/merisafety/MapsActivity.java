package com.developers.droidteam.merisafety;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.polok.routedrawer.RouteDrawer;
import com.github.polok.routedrawer.RouteRest;
import com.github.polok.routedrawer.model.Routes;
import com.github.polok.routedrawer.model.TravelMode;
import com.github.polok.routedrawer.parser.RouteJsonParser;
import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.ui.IconGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener {

    private static final String GOOGLE_API_KEY = "AIzaSyDQ94rd1-xHgj_sSBNbusWx4QQl8A56cik";
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LocationRequest mLocationRequest;
    private boolean mLocationUpdateState;
    // 2
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private String guarEmail = null;
    private ProgressBar progressBar;
    private DatabaseReference mDatabase;
    private double minDistance=200000;
    private int mode =0;

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
                   mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getLastLatLng(),13));
               }
                placeNearbyPeoples();
                mode=0;
            }
        });

        nearby_police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();

                Toast.makeText(MapsActivity.this, "Finding nearby police stations", Toast.LENGTH_SHORT).show();

                if(mLastLocation.getLongitude()!=0){
                    placeMarkerOnMap(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getLastLatLng(),13));

                }
                performSearch("police",mLastLocation.getLatitude(),mLastLocation.getLongitude(),5000,mMap);
                mode=1;

            }
        });

        nearby_hospitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();

                Toast.makeText(MapsActivity.this, "Finding nearby Hospitals", Toast.LENGTH_SHORT).show();
                if(mLastLocation.getLongitude()!=0){
                    placeMarkerOnMap(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(getLastLatLng(),13));

                }

                performSearch("hospital",mLastLocation.getLatitude(),mLastLocation.getLongitude(),5000,mMap);
               mode=2;
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
        progressBar.setVisibility(View.INVISIBLE);
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

            if(mode==0){

                placeMarkerOnMap(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                placeNearbyPeoples();
            }
            else if(mode==1){
                placeMarkerOnMap(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                performSearch("police",mLastLocation.getLatitude(),mLastLocation.getLongitude(),5000,mMap);
            }
            else{
                placeMarkerOnMap(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
                performSearch("hospital",mLastLocation.getLatitude(),mLastLocation.getLongitude(),5000,mMap);
            }
        }
    }

    public void placeNearbyPeoples()
    {
        SharedPreferences sp = getSharedPreferences(sp_db, Context.MODE_PRIVATE);
        final String user = sp.getString(l_key, null);
        final String currentpin = sp.getString(pin_key,null);
        final double[] mlat = new double[1];
        final double[] mlng = new double[1];
        final boolean[] mUseloc = {false};
        DatabaseReference peopleEnd = mDatabase.child(d_key);

        peopleEnd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot currentsnapshot : dataSnapshot.getChildren())
                {
                    if(!currentsnapshot.getKey().equals(user))
                    {

                      String pincode =currentsnapshot.child(pin_key).getValue().toString();
                      boolean useLoc = (boolean) currentsnapshot.child(use_loc_key).getValue();
                      String slat=currentsnapshot.child(lat_key).getValue().toString();
                      String slng=currentsnapshot.child(lng_key).getValue().toString();
                      double lat =Double.parseDouble(currentsnapshot.child(lat_key).getValue().toString());
                      double lng =Double.parseDouble(currentsnapshot.child(lng_key).getValue().toString());
                      String name = currentsnapshot.child(n_key).getValue().toString();
                      String mobile = currentsnapshot.child(m_key).getValue().toString();
                      String email = currentsnapshot.child(e_key).getValue().toString();
                      String uid = currentsnapshot.getKey();
                        //****************check nearest helping hand********************
                        float[] results= new float[1];
                        Location.distanceBetween(mLastLocation.getLatitude(),mLastLocation.getLongitude(),lat,lng,results);
                        Log.d("Distance", "Result is "+results[0]);
                        if(results[0]<minDistance){
                            minDistance=results[0];
                            Log.d("Distance","min : "+minDistance);
                            mUseloc[0] =(boolean) currentsnapshot.child(use_loc_key).getValue();
                            mlat[0] =Double.parseDouble(currentsnapshot.child(lat_key).getValue().toString());
                            mlng[0] =Double.parseDouble(currentsnapshot.child(lng_key).getValue().toString());

                        }
                        if(guarEmail.equals(currentsnapshot.child(e_key).getValue().toString())){

                            placePeopleWindow(name,mobile,slat,slng,uid,email);
                        }else if(pincode.equals(currentpin)&&useLoc)
                        {

                            placePeopleWindow(name,mobile,slat,slng,uid,email);

                        }
                    }
                }
                if(mUseloc[0]){

                    LatLng latlng = new LatLng(mlat[0], mlng[0]);
                    int color = getResources().getColor(R.color.colorPrimary);
                    DrawRoute drawRoute = new DrawRoute(mMap,color,getLastLatLng(),latlng);
                    drawRoute.execute();

                }
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                  Log.d("database",databaseError.getMessage());
            }
        });

    }
    public void placePeopleWindow(String name, String mobile,String lat, String lng,String uid,String email)
    {
        double mylat = Double.parseDouble(lat);
        double mylng = Double.parseDouble(lng);
        LatLng mylatlng = new LatLng(mylat,mylng);
        placeMarkerOnMapPeople(name,mobile,mylatlng,uid,email);
    }
    public class DrawRoute extends AsyncTask{

        GoogleMap map;
        LatLng start,end;
        int color;

        public DrawRoute(GoogleMap googleMap, int color, LatLng first, LatLng last){
            map=googleMap;
            start=first;
            end=last;
            this.color=color;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            final RouteDrawer routeDrawer = new RouteDrawer.RouteDrawerBuilder(map)
                    .withColor(color)
                    .withWidth(10)
                    .withAlpha(0.5f)
                    .withMarkerIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .build();
            RouteRest routeRest = new RouteRest();
            routeRest.getJsonDirections(start,end, TravelMode.WALKING)
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<String, Routes>() {
                        @Override
                        public Routes call(String s) {
                            return new RouteJsonParser<Routes>().parse(s, Routes.class);
                        }
                    })
                    .subscribe(new Action1<Routes>() {
                        @Override
                        public void call(Routes r) {
                            routeDrawer.drawPath(r);
                        }
                    });

            return null;
        }
    }

    public class SetImageMarker extends AsyncTask{

        String UID;
        GoogleMap mMap;
        LatLng location;
        String name;
        String email;
     SetImageMarker(String UID, GoogleMap mMap, LatLng location, String name, String email){
            this.UID=UID;
         this.mMap=mMap;
         this.location=location;
         this.name=name;
         this.email=email;

    }

        @Override
        protected Object doInBackground(Object[] objects) {

            Log.d("glide","entered glide");

            final ImageView mImageView = new ImageView(getApplicationContext());
            final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());

            // Reference to an image file in Cloud Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();

            StorageReference storageRef = storage.getReference();

            StorageReference photoRef = storageRef.child("user_photos/"+UID+".jpg");

            File localFile = null;
            final FileInputStream[] fileInputStream = {null};
            try {
                localFile = File.createTempFile(UID, "jpg");

                final File finalLocalFile = localFile;
                photoRef.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Successfully downloaded data to local file
                                // ...

                                Log.d("glide","successfully downloaded : "+UID);
                                mIconGenerator.setContentView(mImageView);
                                try {
                                    fileInputStream[0] = new FileInputStream(finalLocalFile);

                                    mImageView.setImageBitmap(BitmapFactory.decodeStream(fileInputStream[0]));

                                    Bitmap iconBitmap = mIconGenerator.makeIcon();

                                    MarkerOptions markerOptions = new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromBitmap(getResizedBitmap(iconBitmap,120,120)));
                                    String titleStr = getAddress(location);  // add these two lines
                                    if(guarEmail!=null&&guarEmail.equals(email))
                                    {

                                        int color = getResources().getColor(R.color.route_color);
                                        markerOptions.title("Guardian: "+name).snippet(titleStr);
                                        DrawRoute drawRoute = new DrawRoute(mMap,color,getLastLatLng(),location);
                                        drawRoute.execute();
                                    }
                                    else
                                    {
                                        markerOptions.title(name).snippet(titleStr);
                                    }
                                    // 2
                                    LatLng l = new LatLng(location.latitude,location.longitude);
                                    mMap.addMarker(markerOptions);

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle failed download
                        // ...
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
    public LatLng getLastLatLng()
    {
        return new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
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

        //RETURN THE NEW BITMAP

        return Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
    }


    protected void placeMarkerOnMap(LatLng location) {
        // 1
        final SharedPreferences sp = getSharedPreferences(sp_db, Context.MODE_PRIVATE);
        String userid = sp.getString(l_key,null);


        SetImageMarker task = new SetImageMarker(userid,mMap,location,"You",null);
        task.execute();

        DatabaseReference userEnd = mDatabase.child((d_key)).child(userid).child(userid).child(e_key);
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


    protected void placeMarkerOnMapPeople(String name, String mobile, LatLng location, String uid,String email) {
        // 1
        SetImageMarker task = new SetImageMarker(uid,mMap,location,name,email);
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
                            e.printStackTrace();
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