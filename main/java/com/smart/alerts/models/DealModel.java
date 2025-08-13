package com.smart.alerts.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DealModel {

    public long UserID;
    public String DealName;
    public String DealStartDate;
    public String DealEndDate;
    public Double DealPrice;
    public Double DealPercent;
    public String DealImage1;
    public String DealImage2;
    public String DealImage3;

    public DealModel() {
        UserID = 0;
        DealName = "";
        DealStartDate = null;
        DealEndDate = null;
        DealPrice = null;
        DealPercent = null;
        DealImage1 = null;
        DealImage2 = null;
        DealImage3 = null;
    }

    public boolean isActive() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date endDate = sdf.parse(DealEndDate);
            return endDate != null && endDate.after(new Date());
        } catch (ParseException e) {
            return false;
        }
    }

    public Date getEndDateAsDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            return sdf.parse(DealEndDate);
        } catch (ParseException e) {
            return new Date(0); // fallback to old date
        }
    }
}
