package com.developers.droidteam.merisafety;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragHelpLine extends Fragment {


    public FragHelpLine() {
        // Required empty public constructor
    }
String[] src = {"Police call : 100","Women HelpLine numbers: 1091 ","Fire: 101","Traffic police: 103","Hospital on wheels: 104","Road Accident: 1073","Disaster Management: 108","Anti poison: 1066","Train Accident: 1072","Child Abuse: 1098","All in one Emergeny: 112","Railway enquiry: 139","Drug Deaddiction: 1800-11-0031"};
ListView list;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vv =  inflater.inflate(R.layout.fragment_frag_help_line, container, false);
    list = (ListView)vv.findViewById(R.id.listss);
        return vv;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<String> ad = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,src);
        list.setAdapter(ad);
    }
}
