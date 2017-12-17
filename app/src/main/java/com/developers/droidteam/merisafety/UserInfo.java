package com.developers.droidteam.merisafety;

/**
 * Created by paras on 10/11/17.
 */

public class UserInfo {

    private String userid;
    private String name;
    private String email;
    private long lat;
    private long lng;
    private String mobile;
    private long pincode;
    private String photoUrl;
    private long useloc;

    public void setUseloc(long useloc) {
        this.useloc = useloc;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setPincode(long pincode) {
        this.pincode = pincode;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public void setLng(long lng) {
        this.lng = lng;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public long getPincode() {
        return pincode;
    }

    public String getUserid() {
        return userid;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getName() {
        return name;
    }
}

