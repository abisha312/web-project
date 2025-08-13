package com.smart.alerts.models;
import android.graphics.Bitmap;

public class CommonVariable {
    public static String ApibaseUrl = "http://192.168.1.7/SmartAlertsAPI/api/";
    public static long RegisteredUserID = -1;
    public static long SigninUserID = -1;
    public static long SigninUserSpecificID=-1;
    public static String SigninUserName = null;
    public static String DeviceFCMToken = null; // Added for storing FCM token globally
    public static String ShopProfilePicUrl = null;
    public static String CustomerProfilePicUrl = null;
    public static String Token = null;
}
