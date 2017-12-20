package com.developers.droidteam.merisafety;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;

public class PlacesDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

    JSONObject googlePlacesJson;
    GoogleMap googleMap;
    Context con;
    String ssType;
    public PlacesDisplayTask(Context context,String type){
        con=context;
        ssType=type;
    }
    @Override
    protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

        List<HashMap<String, String>> googlePlacesList = null;
        Places placeJsonParser = new Places();

        try {
            googleMap = (GoogleMap) inputObj[0];
            googlePlacesJson = new JSONObject((String) inputObj[1]);
            googlePlacesList = placeJsonParser.parse(googlePlacesJson);
        } catch (Exception e) {
            Log.d("nearby", e.toString());
        }
        return googlePlacesList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
        if(list!=null)
        {
            ImageView mImageView = new ImageView(con);
            IconGenerator mIconGenerator = new IconGenerator(con);
            mIconGenerator.setContentView(mImageView);
            mImageView.setBackgroundColor(con.getResources().getColor(R.color.colorPrimary));
            if(ssType.equals("hospital"))
            {
                mImageView.setImageResource(R.drawable.ic_local_hospital_black_24dp);
            }
            else if(ssType.equals("police")){
                mImageView.setImageResource(R.drawable.ic_security_black_24dp);
            }
            MapsActivity mapsActivity = new MapsActivity();
            Bitmap iconBitmap = mapsActivity.getResizedBitmap(mIconGenerator.makeIcon(),100,100);

            for (int i = 0; i < list.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = list.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng).icon(BitmapDescriptorFactory.fromBitmap(iconBitmap));
                markerOptions.title(placeName + " : " + vicinity);
                googleMap.addMarker(markerOptions);
            }
            Log.d("nearby","Marker set for search");
        }
    }
}

