package com.smart.alerts;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.smart.alerts.models.APIResult;
import com.smart.alerts.models.CommonVariable;
import com.smart.alerts.models.LoginRequest;
import com.smart.alerts.models.SigninResponse;
import com.smart.alerts.models.UserModel;
import com.smart.alerts.services.APIService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText etUserName;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            FirebaseApp.initializeApp(this);
            fetchFCMToken();
        }
        catch(Exception e){
            Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUserName = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "default_channel", "Smart Alerts", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1002);
            }
        }

    }

    public void OnSigninClick(View view) {
        Signin();
    }

    private void Signin() {
        fetchFCMToken();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonVariable.ApibaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.Email = etUserName.getText().toString();
        loginRequest.Password = etPassword.getText().toString();

        Call<APIResult<SigninResponse>> login = apiService.AuthenticateUser(loginRequest);
        login.enqueue(new Callback<APIResult<SigninResponse>>() {
            @Override
            public void onResponse(Call<APIResult<SigninResponse>> call, Response<APIResult<SigninResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().ResponseData.isEmpty()) {
                    SigninResponse signinData = response.body().ResponseData.get(0);
                    long userId = signinData.UserID;
                    long userTypeID = signinData.UserTypeID;
                    long userSpecificId=signinData.UserSpecificID;
                    String accessToken=signinData.AccessToken;
                    String userName=signinData.UserName;
                    String token=signinData.FCMToken;

                    CommonVariable.SigninUserID = userId;
                    CommonVariable.SigninUserName=userName;
                    CommonVariable.SigninUserSpecificID=userSpecificId;
                    CommonVariable.DeviceFCMToken=accessToken;
                    CommonVariable.Token=token;

                    fetchProfilePic(userId, userTypeID, () -> {
                        if (userTypeID == 2) {
                            startActivity(new Intent(MainActivity.this, BusinessDashboardActivity.class));
                        } else if (userTypeID == 3) {
                            startActivity(new Intent(MainActivity.this, CustomerDashboardActivity.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Unknown user type", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    });

                } else {
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<APIResult<SigninResponse>> call, Throwable t) {
                Log.e("Signin", "API Error: " + t.getLocalizedMessage());
                Toast.makeText(MainActivity.this, "Login Error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchFCMToken() {
        Log.d("SmartAlerts", "Requesting FCM token...");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    Log.d("SmartAlerts", "📡 Token task completed. Success? " + task.isSuccessful());
                    if (task.isSuccessful()) {
                        CommonVariable.DeviceFCMToken = task.getResult();
                        Log.d("SmartAlerts", "✅ FCM Token: " + CommonVariable.DeviceFCMToken);
                        Toast.makeText(MainActivity.this, "FCM Token:"+ CommonVariable.DeviceFCMToken, Toast.LENGTH_LONG).show();
                    } else {
                        Log.w("SmartAlerts", "❌ FCM token fetch failed", task.getException());
                        Toast.makeText(MainActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void fetchProfilePic(long userId, long userTypeId, Runnable onComplete) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonVariable.ApibaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);

        Call<APIResult<UserModel>> call = apiService.GetProfile(userId);
        call.enqueue(new Callback<APIResult<UserModel>>() {
            @Override
            public void onResponse(Call<APIResult<UserModel>> call, Response<APIResult<UserModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().ResponseData.isEmpty()) {
                    String picUrl = response.body().ResponseData.get(0).ProfilePicUrl;
                    if (userTypeId == 2) {
                        CommonVariable.ShopProfilePicUrl = picUrl;
                    } else if (userTypeId == 3) {
                        CommonVariable.CustomerProfilePicUrl = picUrl;
                    }
                }
                onComplete.run();
            }

            @Override
            public void onFailure(Call<APIResult<UserModel>> call, Throwable t) {
                Log.e("ProfilePicFetch", "Failed: " + t.getLocalizedMessage());
                onComplete.run();
            }
        });
    }

    public void OnSignupClick(View view) {
        startActivity(new Intent(MainActivity.this, SignupActivity.class));
    }

    public void OnForgotPwdClick(View view) {
        startActivity(new Intent(MainActivity.this, ForgotPasswordActivity.class));
    }
}
