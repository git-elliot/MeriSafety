package com.developers.droidteam.merisafety;

import android.annotation.SuppressLint;
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
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private final String d_key = "users";
    private final String l_key = "login_key";
    private final String sp_db = "account_db";
    private final String sp_n="name";
    private final String gn_key="gname";
    private final String gm_key="gmobile";
    private final String ge_key="gemail";

    private DatabaseReference apiKey;
    private boolean executeOnce = false;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con= context;
    }

    View v4;
    @Override
    public View onCreateView(@NonNull LayoutInflater l, @Nullable ViewGroup container, @ Nullable Bundle savedInstanceState){
         v4=  l.inflate(R.layout.activity_frag_guardian,container,false);
        mAuth = FirebaseAuth.getInstance();
        Button b1 = (Button)v4.findViewById(R.id.b2);
        final EditText et1 = v4. findViewById(R.id.et1);
        final EditText et2 = v4. findViewById(R.id.et2);
        final EditText et3 =  v4.findViewById(R.id.et3);
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
         }

            }
        });


        return v4;
}

    @SuppressLint("StaticFieldLeak")
    private class BackgroundTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        String yournumber;
        String yourname;
        String youremail;
        FirebaseUser user;
        BackgroundTask(Activity activity, FirebaseUser currentUser, String name, String number, String email) {
            dialog = new ProgressDialog(activity);
            youremail= email;
            yourname=name;
            yournumber=number;
            user = currentUser;
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Saving your guardian details on server.");
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

        guarEnd = mDatabase.child(d_key).child(currentUser.getUid());

        addGuarToFirebase(name,email,number,currentUser.getUid());

    }

    public void addGuarToFirebase(final String name, final String email, final String number, final String uid ){
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

                    final String sms = name+" have added you as my guardian on MeriSafety app. Download MeriSafety app on PlayStore to help me when I need you.";
                    SharedPreferences sp = con.getSharedPreferences(sp_db, Context.MODE_PRIVATE);
                    String name1 = sp.getString(sp_n,null);

                    final String mail = name1+" have added you as their guardian on MeriSafety app. Download MeriSafety app on PlayStore to help them when they need you.";


                    smss.sendTextMessage(number, null, sms, pi, pin);
                    apiKey = mDatabase.child("mailapikey");
                    apiKey.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String api_key=dataSnapshot.getValue().toString();

                            if(!executeOnce){
                                SendMail sendMail = new SendMail(email,"Added you as a guardian",mail,api_key);
                                sendMail.execute();

                                executeOnce=true;
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Toast.makeText(getContext(), "Guardian added successfully", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor et = sp.edit();
                    et.putString(l_key,uid);
                    et.putString(gn_key,name);
                    et.putString(gm_key,number);
                    et.putString(ge_key,email);

                    et.apply();

                    Intent it3=new Intent(getContext(),NavigationDrawerActivity.class);
                    startActivity(it3);

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
