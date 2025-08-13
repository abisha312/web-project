package com.smart.alerts.models;

public class CustomerDetailRequest {

    public long UserID;
    public String CustomerDOB;
    public String CustomerAnniversary;
    public String ProfilePicURL;
    public String DeviceFCMToken;

    public CustomerDetailRequest() {
        UserID = 0;
        CustomerDOB = null;
        CustomerAnniversary = null;
        ProfilePicURL = null;
        DeviceFCMToken=null;
    }
}
