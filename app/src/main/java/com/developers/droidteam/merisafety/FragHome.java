package com.developers.droidteam.merisafety;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by siddharth on 6/28/2017.
 */

public class FragHome extends Fragment {

    private static final int REQUEST_PHONE =1889 ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_frag_home,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(ActivityCompat.checkSelfPermission(getActivity(),Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){

        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)){
                    Toast.makeText(getActivity(), "you need to check permission", Toast.LENGTH_SHORT).show();
                }
            }
        }
        requestPermissions(new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if(requestCode==REQUEST_PHONE){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
            }else {
                Toast.makeText(getActivity(), "permission was not granted", Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

}
