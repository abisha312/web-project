package com.smart.alerts.models;

public class GeoFenceRequest {
    public long userId;
    public double userLat;
    public double userLon;
    public String deviceToken;

    public GeoFenceRequest() {
        this.userId = 0;
        this.userLat = 0;
        this.userLon = 0;
        this.deviceToken = "";
    }
}
