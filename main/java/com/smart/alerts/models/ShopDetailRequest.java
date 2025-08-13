package com.smart.alerts.models;

import java.time.LocalTime;

public class ShopDetailRequest {

    public long UserID;
    public String ShopType;
    public String OpeningTime;
    public String ClosingTime;
    public String WebsiteURL;
    public String ShopManagerName;
    public String ShopContact;
    public String ShopProfilePicURL;
    public String Latitude;
    public String Longitude;

    public ShopDetailRequest() {
        UserID = 0;
        ShopType = "";
        OpeningTime = null;
        ClosingTime = null;
        WebsiteURL = null;
        ShopManagerName = "";
        ShopContact = "";
        ShopProfilePicURL = null;
        Latitude = null;
        Longitude= null;
    }
}
