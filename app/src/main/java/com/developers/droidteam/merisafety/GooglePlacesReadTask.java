package com.developers.droidteam.merisafety;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
    String googlePlacesData = null;
    GoogleMap googleMap;
    Context con;
    String sType;
    public  GooglePlacesReadTask(Context context,String type){
        con=context;
        sType=type;
    }
    @Override
    protected String doInBackground(Object... inputObj) {
        try {
            googleMap = (GoogleMap) inputObj[0];
            String googlePlacesUrl = (String) inputObj[1];
            Http http = new Http();
            googlePlacesData = http.read(googlePlacesUrl);
        } catch (Exception e) {
            Log.d("nearby", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("nearby","Displaying data of places");
        PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask(con,sType);
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = result;
        placesDisplayTask.execute(toPass);
    }
}