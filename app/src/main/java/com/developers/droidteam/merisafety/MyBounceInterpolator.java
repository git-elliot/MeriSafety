package com.developers.droidteam.merisafety;


import android.view.animation.Interpolator;

public class MyBounceInterpolator implements android.view.animation.Interpolator{
    private double mFrequency =10 ;
    private double mAmplitude = 1;
    MyBounceInterpolator(double amplitude, double frequency){
        mFrequency = frequency;
        mAmplitude =amplitude;
    }
    @Override
    public float getInterpolation(float time) {
        return (float) (-1 * Math.pow(Math.E, -time/ mAmplitude) *
                Math.cos(mFrequency * time) + 1);
    }
}
