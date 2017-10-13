package com.developers.droidteam.merisafety;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;

public class Frag_guardian extends Fragment {

    private static final int RQS_PICK_CONTACT =66 ;
    Button b1,b2;
    EditText et1,et2;
    private static final int REQUEST_CAMERA = 1888;
    private static final int REQUEST_PHONE =1889 ;

    View v4;
    @Override
    public View onCreateView(LayoutInflater l, @Nullable ViewGroup container, @ Nullable Bundle savedInstanceState){
         v4=  l.inflate(R.layout.activity_frag_guardian,container,false);
        Button b1 = (Button)v4.findViewById(R.id.b2);
        final EditText et1 = (EditText)v4. findViewById(R.id.et1);
        final EditText et2 = (EditText)v4. findViewById(R.id.et2);
        final EditText et3 = (EditText) v4.findViewById(R.id.et3);
        final String emailPattern = "[a-zA-Z0-9._-]+@gmail+\\.+[a-z]+";

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = et3.getText().toString().trim();
                if (et1.getText().toString().length() == 0) {     et1.setError("Enter guardian name");
                }

               else if (!(email.matches(emailPattern))) {
                     et3.setError("Invalid Email Address");
                }
                else{
                    DatabaseOperations DB = new DatabaseOperations(getActivity());
                    DB.putInformationGaur(DB,et1.getText().toString(),et2.getText().toString(),et3.getText().toString());

                    Toast.makeText(getContext(), "Guardian added successfully", Toast.LENGTH_SHORT).show();
                    Intent it3=new Intent(getContext(),NavigationDrawerActivity.class);
                    startActivity(it3);
                }

            }
        });


        return v4;
}


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        et1 = (EditText)v4.findViewById(R.id.et1);
        et2  = (EditText)v4.findViewById(R.id.et2);
        b1 = (Button)v4.findViewById(R.id.b1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(i,RQS_PICK_CONTACT);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RQS_PICK_CONTACT){
            if(resultCode==RESULT_OK){
                Uri con = data.getData();
                Cursor cur = getActivity().managedQuery(con,null,null,null,null);
                cur.moveToFirst();
                String num = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String num1 = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                et1.setText(num1);
                et2.setText(num);
                Toast.makeText(getActivity(), num, Toast.LENGTH_SHORT).show();
            }
        }
    }




}
