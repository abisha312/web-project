package com.smart.alerts.models;

public class UserModel {

    public long UserId;
    public String FirstName;
    public String LastName;
    public String Email;
    public long UserTypeId;
    public String ShopName;
    public String Address1;
    public String Address2;
    public String City;
    public String Region;
    public String PostalCode;
    public String Country;
    public String Phone;
    public String ProfilePicUrl;

    public UserModel() {
        UserId = 0;
        FirstName = "";
        LastName = "";
        Email = "";
        UserTypeId = 0;
        ShopName = "";
        Address1 = "";
        Address2 = "";
        City = "";
        Region = "";
        PostalCode = "";
        Country = "";
        Phone = "";
        ProfilePicUrl = "";
    }
}
