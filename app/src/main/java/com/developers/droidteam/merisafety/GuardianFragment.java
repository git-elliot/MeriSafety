package com.developers.droidteam.merisafety;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GuardianFragment extends Fragment {


    View v;
    Context con;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con= context;
    }

    public GuardianFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_guardian, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final DatabaseOperations DOP = new DatabaseOperations(con);
        Cursor CR= DOP.getInformationGaur(DOP);
        CR.moveToNext();
        String name = CR.getString(0).trim();
        String number =CR.getString(1).trim();
        String mail =CR.getString(2).trim();

        Button change = (Button) v.findViewById(R.id.add_new_guar);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder ad= new AlertDialog.Builder(con);
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        DOP.removeGuardian(DOP);

                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        Frag_guardian obj = new Frag_guardian();
                        ft.addToBackStack("stack2");
                        ft.replace(R.id.newfraglayout,obj,"guardian");
                        ft.commit();
                    }
                });

                ad.setNegativeButton("No",null);
                ad.setMessage("This will edit your current guardian, Are you sure ?");
                ad.show();

            }
        });
        TextView tname = (TextView) v.findViewById(R.id.guar_name);
        TextView tphone = (TextView) v.findViewById(R.id.guar_phone);
        TextView temail = (TextView) v.findViewById(R.id.guar_email);

        tname.setText(name);
        tphone.setText(number);
        temail.setText(mail);


    }
}
