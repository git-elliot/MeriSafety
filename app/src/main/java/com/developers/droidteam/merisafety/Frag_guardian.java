package com.developers.droidteam.merisafety;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsManager;
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

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Frag_guardian extends Fragment {

    private static final int RQS_PICK_CONTACT =66 ;
    Button b1,b2;
    EditText et1,et2;
    private static final int REQUEST_CAMERA = 1888;
    private static final int REQUEST_PHONE =1889 ;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference guarEnd;
    Context con;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con= context;
    }

    View v4;
    @Override
    public View onCreateView(LayoutInflater l, @Nullable ViewGroup container, @ Nullable Bundle savedInstanceState){
         v4=  l.inflate(R.layout.activity_frag_guardian,container,false);
        mAuth = FirebaseAuth.getInstance();
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
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    if(currentUser!=null)
                    {
                        BackgroundTask task = new BackgroundTask(getActivity(),currentUser,et1.getText().toString(),et2.getText().toString(),et3.getText().toString());
                        task.execute();

                    }
//                    DatabaseOperations DB = new DatabaseOperations(getActivity());
  //                  DB.putInformationGaur(DB,et1.getText().toString(),et2.getText().toString(),et3.getText().toString());
         }

            }
        });


        return v4;
}

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        String yournumber;
        String yourname;
        String youremail;
        FirebaseUser user;
        public BackgroundTask(Activity activity, FirebaseUser currentUser, String name, String number, String email) {
            dialog = new ProgressDialog(activity);
            youremail= email;
            yourname=name;
            yournumber=number;
            user = currentUser;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait.,,,,");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
                createGuardian(user,yourname,yournumber,youremail);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

    }


    public void createGuardian(FirebaseUser currentUser,String name, String number, String email)
    {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        guarEnd = mDatabase.child("users").child(currentUser.getUid());

        addGuarToFirebase(name,email,number,currentUser.getUid().toString());

    }

    public void addGuarToFirebase(String name, String email, String number, final String uid ){
        final String guarNum = number;
        final String guarEmail = email;
        List<GuardianInfo> guardianInfos = getGuarInfo(name,email,number,uid);

        for(GuardianInfo guardianInfo : guardianInfos)
        {

            guarEnd.child(uid).setValue(guardianInfo).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(con, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //*****************SENDING TEXT TO GUARDIAN THAT YOU ADDED THEM***************
                    final PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, new Intent("in.wptrafficanalyzer.sent"), 0);
                    final PendingIntent pin = PendingIntent.getBroadcast(getActivity(), 0, new Intent("in.wptrafficanalyzer.delivered"), 0);

                    final SmsManager smss = SmsManager.getDefault();

                    final String sms = " I have added you as my guardian on MeriSafety app. Download MeriSafety app on PlayStore to help me when I need you.";
                    SharedPreferences sp = con.getSharedPreferences("account_db",con.MODE_PRIVATE);
                    String name1 = sp.getString("name",null);

                    final String mail = name1+" have added you as their guardian on MeriSafety app. Download MeriSafety app on PlayStore to help them when they need you.";


                    smss.sendTextMessage(guarNum, null, sms, pi, pin);

                    //**********************SENDING MAIL TO GUARDIAN***********************

                    BackgroundMail.newBuilder(con).withUsername("merisafety@gmail.com")
                            .withPassword("WRTB@droid")
                            .withMailto(guarEmail)
                            .withType(BackgroundMail.TYPE_PLAIN)
                            .withSubject("Mail from MeriSafety")
                            .withBody(mail)
                            .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                                @Override
                                public void onSuccess() {

                                    Toast.makeText(getContext(), "Guardian added successfully", Toast.LENGTH_SHORT).show();
                                    SharedPreferences sp = con.getSharedPreferences("account_db", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor et = sp.edit();
                                    et.putString("login_key",uid);
                                    et.commit();

                                    Intent it3=new Intent(getContext(),NavigationDrawerActivity.class);
                                    startActivity(it3);

                                }
                            })
                            .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                                @Override
                                public void onFail() {

                                    Toast.makeText(getContext(), "Unable to send them email.", Toast.LENGTH_SHORT).show();
                                    SharedPreferences sp = con.getSharedPreferences("account_db", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor et = sp.edit();
                                    et.putString("login_key",uid);
                                    et.commit();

                                    Intent it3=new Intent(getContext(),NavigationDrawerActivity.class);
                                    startActivity(it3);
                                }
                            })
                            .send();



                }
            });
        }

    }


    public static List<GuardianInfo> getGuarInfo(String name, String email, String number, String uid){

        List<GuardianInfo> guardianInfos = new ArrayList<>();
        GuardianInfo guardianInfo = new GuardianInfo();
        guardianInfo.setEmail(email);
        guardianInfo.setName(name);
        guardianInfo.setMobile(number);
        guardianInfo.setGuardianid(uid);
        guardianInfos.add(guardianInfo);
        return guardianInfos;
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
