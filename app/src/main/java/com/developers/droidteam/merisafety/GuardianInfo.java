package com.developers.droidteam.merisafety;

/**
 * Created by paras on 10/11/17.
 */

public class GuardianInfo {

    private String guardianid;
    private String name;
    private String email;
    private String mobile;

    public void setGuardianid(String guardianid) {
        this.guardianid = guardianid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGuardianid() {
        return guardianid;
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
