package com.developers.droidteam.merisafety;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class Frag_verification extends Fragment {
    private DatabaseReference mDatabase;
    private DatabaseReference userEnd ;
    private FirebaseAuth mAuth;
    private final String d_key = "users";
    private final String m_key = "mobile";
    Context con;
    Button b1;
    EditText editText;
    View v;
    private ProgressDialog dialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con= context;
    }

    public View onCreateView(@NonNull LayoutInflater l, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = l.inflate(R.layout.activity_frag_verification, container,false);

        editText = v.findViewById(R.id.enternum);
         b1 = (Button)v.findViewById(R.id.proceed1);
        mAuth = FirebaseAuth.getInstance();


        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        b1.setOnClickListener(v -> {

            if (editText.getText().toString().trim().length() < 10) {
                editText.setError("Phone Number is invalid");
                editText.requestFocus();
            }
            else {
                Toast.makeText(con, "Please Wait Automatically Detecting OTP.", Toast.LENGTH_LONG).show();

                dialog = new ProgressDialog(con);
                dialog.setMessage("Verifying your mobile number please wait");
                dialog.show();

                verifyUser(editText.getText().toString());

            }

        });


    }


   public void verifyUser(final String num)
   {
       PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

           @Override
           public void onVerificationCompleted(PhoneAuthCredential credential) {
               // This callback will be invoked in two situations:
               // 1 - Instant verification. In some cases the phone number can be instantly
               //     verified without needing to send or enter a verification code.
               // 2 - Auto-retrieval. On some devices Google Play services can automatically
               //     detect the incoming verification SMS and perform verificaiton without
               //     user action.
               Log.d("OAuth", "onVerificationCompleted:" + credential);

               Toast.makeText(con, "Verification successfull", Toast.LENGTH_SHORT).show();
               FirebaseUser currentUser = mAuth.getCurrentUser();

               FragmentManager fm = getFragmentManager();
               FragmentTransaction ft = fm.beginTransaction();
               Frag_guardian obj = new Frag_guardian();

               mDatabase = FirebaseDatabase.getInstance().getReference();

               assert currentUser != null;
               userEnd = mDatabase.child(d_key).child(currentUser.getUid()).child(m_key);
               userEnd.setValue(num).addOnCompleteListener(task -> {
                   Toast.makeText(con, "Mobile number added successfully", Toast.LENGTH_SHORT).show();

                   if (dialog.isShowing()) {
                       dialog.dismiss();
                   }

                   ft.replace(R.id.l2,obj,"guardian");
                   ft.commit();

               }).addOnFailureListener(e -> Toast.makeText(con, "Unable to add mobile number, try again", Toast.LENGTH_SHORT).show());


           }

           @Override
           public void onVerificationFailed(FirebaseException e) {
               // This callback is invoked in an invalid request for verification is made,
               // for instance if the the phone number format is not valid.
               Log.w("OAuth", "onVerificationFailed", e);
               if (dialog.isShowing()) {
                   dialog.dismiss();
               }

               if (e instanceof FirebaseAuthInvalidCredentialsException) {
                   // Invalid request
                   // ...

                   Log.d("firebase verification","Invalid Request due to credentials : "+e.getMessage() );
               } else if (e instanceof FirebaseTooManyRequestsException) {
                   // The SMS quota for the project has been exceeded
                   // ...
                   Log.d("firebase verification","Contact admin,The SMS quota for the project has been exceeded.");
               }

               // Show a message and update the UI
               // ...
           }

       };

       PhoneAuthProvider.getInstance().verifyPhoneNumber(
               num,
               30,
               TimeUnit.SECONDS,
               (Activity) con,
               mCallbacks);

   }

}