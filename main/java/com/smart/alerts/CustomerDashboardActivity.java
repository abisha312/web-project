package com.smart.alerts;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.*;
import com.smart.alerts.models.APIResult;
import com.smart.alerts.models.CommonVariable;
import com.smart.alerts.models.GeoFenceRequest;
import com.smart.alerts.models.LoginRequest;
import com.smart.alerts.models.SigninResponse;
import com.smart.alerts.services.APIService;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomerDashboardActivity extends AppCompatActivity {

    TextView Uname;
    ImageView imageViewProfile;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Uname = findViewById(R.id.Uname);
        imageViewProfile = findViewById(R.id.customerProfileImage);

        if (CommonVariable.SigninUserName != null) {
            Uname.setText("Hello " + CommonVariable.SigninUserName + "!");
        }

        if (CommonVariable.CustomerProfilePicUrl != null && !CommonVariable.CustomerProfilePicUrl.isEmpty()) {
            try {
                Picasso.get()
                        .load(CommonVariable.CustomerProfilePicUrl)
                        .placeholder(R.drawable.default_customer_pic)
                        .error(R.drawable.default_customer_pic)
                        .into(imageViewProfile);
            } catch (Exception e) {
                Log.e("SmartAlerts", "❌ Image load error: " + e.getMessage());
                imageViewProfile.setImageResource(R.drawable.default_customer_pic);
            }
        } else {
            imageViewProfile.setImageResource(R.drawable.default_customer_pic);
        }

        // 🔁 Step 1: Location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 🔒 Step 2: Ask location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }


    // 📍 Step 3: Start periodic location tracking
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(30000) // 30 sec
                .setFastestInterval(15000)
                .setSmallestDisplacement(100) // 100 meters
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                if (result != null) {
                    Location location = result.getLastLocation();
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();
                        Log.d("GeoFence", "📍 Location changed: " + lat + ", " + lon);
                        sendGeoFenceRequest(CommonVariable.SigninUserSpecificID, lat, lon, CommonVariable.Token);
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }


    // 📡 Step 4: Send location to backend API
    private void sendGeoFenceRequest(long userId, double lat, double lon, String token) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonVariable.ApibaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);

        GeoFenceRequest geoFenceRequest = new GeoFenceRequest();
        geoFenceRequest.userId = userId;
        geoFenceRequest.userLat = lat;
        geoFenceRequest.userLon = lon;
        geoFenceRequest.deviceToken = token;

        Call<APIResult<String>> call = apiService.CheckProximity(geoFenceRequest);
        call.enqueue(new Callback<APIResult<String>>() {
            @Override
            public void onResponse(Call<APIResult<String>> call, Response<APIResult<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("GeoFence", "✅ API Success: " + response.body().ResponseMessage);
                    if (response.body().ResponseData != null && !response.body().ResponseData.isEmpty()) {
                        String msg = response.body().ResponseData.get(0);
                        Log.d("GeoFence", "✅ Nearby Shop Message: " + msg);
                        showLocalNotification("Smart Alerts", msg); // 🔔 Show popup
                    } else {
                        Log.d("GeoFence", "⚠️ No nearby shops detected.");
                    }
                } else {
                    Log.e("GeoFence", "❌ API error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<APIResult<String>> call, Throwable t) {
                Log.e("GeoFence", "🚫 Proximity API failure: " + t.getLocalizedMessage());
            }
        });
    }

    // 📌 Local notification method
    private void showLocalNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "default_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Smart Alerts", NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_notification) // make sure you have this icon
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            notificationManager.notify(2001, builder.build());
        } else {
            Log.e("Notification", "🔒 POST_NOTIFICATIONS permission not granted");
        }
    }
    // 🎯 Step 5: Handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void SignOut(View obj) {
        Intent actMain = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(actMain);
        finish();
    }
}
