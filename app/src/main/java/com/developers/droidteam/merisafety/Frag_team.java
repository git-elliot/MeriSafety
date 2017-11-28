package com.developers.droidteam.merisafety;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Frag_team extends Fragment {

    View v;
    ProgressBar progressBar[] = new ProgressBar[6];
    Context con;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        con = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v= inflater.inflate(R.layout.fragment_frag_team, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageView abhi = v.findViewById(R.id.abhi);
        ImageView paras = v.findViewById(R.id.paras);
        ImageView priya = v.findViewById(R.id.priya);
        ImageView ridhima = v.findViewById(R.id.ridhima);
        ImageView siddharth = v.findViewById(R.id.siddharth);
        ImageView sunny = v.findViewById(R.id.sunny);

        setImageView(abhi,"abhi_new",R.id.prog_abhi,0);
        setImageView(paras,"par_new",R.id.prog_paras,1);
        setImageView(priya,"pri1_new",R.id.prog_priya,2);
        setImageView(ridhima,"rid_new",R.id.prog_ridhi,3);
        setImageView(siddharth,"sid1_new1",R.id.prog_sid,4);
        setImageView(sunny,"sun1_new",R.id.prog_sun,5);

    }


    public void setImageView(ImageView i, String name, int id, final int k)
    {

        progressBar[k] = v.findViewById(id);
        // Reference to an image file in Cloud Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();

        StorageReference photoRef = storageRef.child("teamimages/"+name+".png");

        GlideApp.with(i.getContext() /* context */)
                .load(photoRef)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar[k].setVisibility(View.INVISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar[k].setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .dontAnimate()
                .into(i);

    }

}


