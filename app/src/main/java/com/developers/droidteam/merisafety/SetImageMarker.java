package com.developers.droidteam.merisafety;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.ui.IconGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SetImageMarker extends AsyncTask{


    private String UID;
    private GoogleMap mMap;
    private LatLng location;
    String name;
    String email;
    private Bitmap downloadfile;
    SetImageMarker(String UID, GoogleMap mMap, LatLng location, String name, String email){
        this.UID=UID;
        this.mMap=mMap;
        this.location=location;
        this.name=name;
        this.email=email;

    }
    public interface OnDownloadListener{
        public void onDownload(Bitmap bitmap);
    }

    private OnDownloadListener onDownloadListener;

    public void setOnDownloadListener(OnDownloadListener listener){
        onDownloadListener= listener;
    }

    public Bitmap getDownloadfile(){
        return downloadfile;
    }
    @Override
    protected Object doInBackground(Object[] objects) {

        Log.d("glide","entered glide");


        // Reference to an image file in Cloud Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        StorageReference photoRef = storageRef.child("user_photos/"+UID+".jpg");

        File localFile;
        final FileInputStream[] fileInputStream = {null};
        try {
            localFile = File.createTempFile(UID, "jpg");

            final File finalLocalFile = localFile;
            photoRef.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Successfully downloaded data to local file
                        // ...

                        Log.d("glide","successfully downloaded : "+UID);
                        try {
                            fileInputStream[0] = new FileInputStream(finalLocalFile);
                            Bitmap dBitmap =  BitmapFactory.decodeStream(fileInputStream[0]);
                            if(onDownloadListener!=null){
                                onDownloadListener.onDownload(dBitmap);
                            }

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }).addOnFailureListener(exception -> {
                // Handle failed download
                // ...
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
