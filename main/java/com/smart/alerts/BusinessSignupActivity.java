package com.smart.alerts;
import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.gson.Gson;
import com.smart.alerts.models.APIResult;
import com.smart.alerts.models.CommonVariable;
import com.smart.alerts.models.SigninResponse;
import com.smart.alerts.models.SignupRequest;
import com.smart.alerts.models.SignupResponse;
import com.smart.alerts.models.ShopDetailRequest;
import com.smart.alerts.models.ShopDetailResponse;
import com.smart.alerts.services.APIService;
//import com.smart.alerts.utils.LocationHelper;
import android.content.pm.PackageManager;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;
import android.location.Location;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BusinessSignupActivity extends AppCompatActivity{
    Spinner spinnerShopType;
    EditText etFirstName,etLastName,etEmail,etPassword,etRetypePassword,etAddress1,etAddress2,etShopName,etContactNo,etCity,etPostalCode;
    EditText etWebsiteURL, etShopManagerName, etShopContact;
    EditText etOpeningTime, etClosingTime;
    ImageView img;
    private Button btnSelect;
    private Uri ShopProfilePicUri;
    private final ActivityResultLauncher<Intent> imagePicker1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    ShopProfilePicUri = result.getData().getData();
                    img.setImageURI(ShopProfilePicUri);
                }
            });
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_business_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRetypePassword = findViewById(R.id.etRetypePassword);
        etAddress1=findViewById(R.id.etAddress1);
        etAddress2=findViewById(R.id.etAddress2);
        etShopName=findViewById(R.id.etShopName);
        etContactNo=findViewById(R.id.etContactNo);
        etCity=findViewById(R.id.etCity);
        etPostalCode=findViewById(R.id.etPostalCode);

        etWebsiteURL = findViewById(R.id.etWebsiteURL);
        etShopManagerName = findViewById(R.id.etShopManagerName);
        etShopContact = findViewById(R.id.etShopContact);

        img = findViewById(R.id.ivShopProfilePic);
        btnSelect = findViewById(R.id.btnSelectImage);
        btnSelect.setOnClickListener(v -> pickImage(imagePicker1));

        spinnerShopType = findViewById(R.id.spinnerShopType);

        etOpeningTime = findViewById(R.id.etOpeningTime);
        etClosingTime = findViewById(R.id.etClosingTime);

        etOpeningTime.setOnClickListener(v -> showTimePicker(etOpeningTime, false));
        etClosingTime.setOnClickListener(v -> showTimePicker(etClosingTime, false));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    private void pickImage(ActivityResultLauncher<Intent> launcher) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    private void showTimePicker(EditText target, boolean is24HourView) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    cal.set(Calendar.MINUTE, minute1);
                    String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.getTime());
                    target.setText(time);
                },
                hour, minute, true); // true for 24-hour

        timePickerDialog.show();
    }

    FusedLocationProviderClient fusedLocationProviderClient;
    double latitude = 0.0, longitude = 0.0;

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 10000L) // intervalMillis
                .setMinUpdateIntervalMillis(5000L)
                .setMaxUpdates(1)
                .build();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.d("SmartAlerts", "Lat: " + latitude + ", Lng: " + longitude);
                } else {
                    Toast.makeText(BusinessSignupActivity.this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
                }
            }
        }, Looper.getMainLooper());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateAllFields() {
        if (etFirstName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your first name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etLastName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your last name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etEmail.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etPassword.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!etPassword.getText().toString().equals(etRetypePassword.getText().toString())) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etAddress1.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your address", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etCity.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your city", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etPostalCode.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your postal code", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etShopName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your shop name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etContactNo.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a contact number", Toast.LENGTH_LONG).show();
            return false;
        }

        // Shop-specific fields
        if (spinnerShopType.getSelectedItem().toString().equals("Shop Type")) {
            Toast.makeText(this, "Please select a valid Shop Type", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etShopManagerName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter Manager Name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etShopContact.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter Shop Contact Number", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etOpeningTime.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter Opening Time", Toast.LENGTH_LONG).show();
            return false;
        }
        if (etClosingTime.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter Closing Time", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void OnSignupInBusinessClick(View view){
        if (validateAllFields()) {
            SignupInBusiness();
        }
    }

    private void SignupInBusiness(){
        try {
            Retrofit retrofit= new Retrofit.Builder().baseUrl(CommonVariable.ApibaseUrl).addConverterFactory(GsonConverterFactory.create()).build();
            APIService apiService = retrofit.create(APIService.class);

            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            String retypePassword = etRetypePassword.getText().toString();
            String contactNo = etContactNo.getText().toString().trim();
            String address1 = etAddress1.getText().toString().trim();
            String address2 = etAddress2.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String postalCode = etPostalCode.getText().toString().trim();
            String shopName=etShopName.getText().toString().trim();


            // Prepare signup request
            SignupRequest signupRequest = new SignupRequest();
            signupRequest.FirstName = firstName;
            signupRequest.LastName = lastName;
            signupRequest.Email = email;
            signupRequest.Password = password;
            signupRequest.Address1 = address1;
            signupRequest.Address2 = address2.isEmpty() ? "" : address2;
            signupRequest.City = city;
            signupRequest.PostalCode = postalCode;
            signupRequest.ContactNo = contactNo;
            signupRequest.StateOrProvince="Tamil Nadu";
            signupRequest.Country="India";
            signupRequest.ShopName=shopName;

            Log.d("SmartAlerts", "SignupRequest JSON: " + new Gson().toJson(signupRequest));
            Call<APIResult<SignupResponse>> call = apiService.RegisterUser(signupRequest);

            call.enqueue(new Callback<APIResult<SignupResponse>>() {
                @Override
                public void onResponse(Call<APIResult<SignupResponse>> call, Response<APIResult<SignupResponse>> response) {
                    Log.d("Signup Debug", "Response code: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        APIResult<SignupResponse> result = response.body();
                        Log.d("Signup Debug", "Response body: " + new Gson().toJson(result));

                        if (result.ResponseData != null && !result.ResponseData.isEmpty()) {
                            CommonVariable.RegisteredUserID = result.ResponseData.get(0).UserID;
                            runOnUiThread(() -> {
                                Toast.makeText(BusinessSignupActivity.this, "Level1 Details Successful", Toast.LENGTH_LONG).show();
                            });
                            GetBusinessDetails();
                            /*Intent actMain = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(actMain);
                            BusinessSignupActivity.this.finish();*/
                        } else {
                            runOnUiThread(() -> Toast.makeText(BusinessSignupActivity.this, result.ResponseMessage, Toast.LENGTH_LONG).show());
                        }
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                Log.e("Signup Debug", "Error body: " + response.errorBody().string());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(() -> Toast.makeText(BusinessSignupActivity.this, "Signup failed. Try again.", Toast.LENGTH_LONG).show());
                    }
                }



                @Override
                public void onFailure(Call<APIResult<SignupResponse>> call, Throwable t) {
                    runOnUiThread(() -> Toast.makeText(BusinessSignupActivity.this, "Error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show());
                }
            });

        } catch (Exception e) {
            Log.e("Smart Alerts", "Signup Error: " + e.getMessage());
            Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_LONG).show();
        }

    }

    private void GetBusinessDetails() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CommonVariable.ApibaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            APIService apiService = retrofit.create(APIService.class);

            String shopType = spinnerShopType.getSelectedItem().toString();
            String websiteURL = etWebsiteURL.getText().toString().trim();
            String shopManagerName = etShopManagerName.getText().toString().trim();
            String contactStr = etShopContact.getText().toString().trim();
            String shopProfilePictureURL = encodeImageToBase64(ShopProfilePicUri);
            String openingTimeStr = etOpeningTime.getText().toString().trim();
            String closingTimeStr = etClosingTime.getText().toString().trim();


            if (CommonVariable.RegisteredUserID == 0 || CommonVariable.RegisteredUserID == -1) {
                Log.e("SmartAlerts", "Invalid or missing RegisteredUserID");
                Toast.makeText(this, "User ID is invalid. Please log in again.", Toast.LENGTH_LONG).show();
                return;
            }

            // ✅ Parse to HH:mm:ss format
            DateTimeFormatter inputFormatter = null;       // User input format
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                inputFormatter = DateTimeFormatter.ofPattern("HH:mm");
            }
            DateTimeFormatter apiFormatter = null;      // Format required by C# API
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                apiFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            }

            LocalTime openingTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                openingTime = LocalTime.parse(openingTimeStr, inputFormatter);
            }
            LocalTime closingTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                closingTime = LocalTime.parse(closingTimeStr, inputFormatter);
            }

            ShopDetailRequest shopDetailRequest = new ShopDetailRequest();
            shopDetailRequest.UserID = CommonVariable.RegisteredUserID;
            shopDetailRequest.ShopType = shopType;
            shopDetailRequest.WebsiteURL = websiteURL;
            shopDetailRequest.ShopManagerName = shopManagerName;
            shopDetailRequest.ShopContact = contactStr;
            shopDetailRequest.ShopProfilePicURL = shopProfilePictureURL;

            // ✅ Format LocalTime to string for TimeSpan compatibility
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                shopDetailRequest.OpeningTime = apiFormatter.format(openingTime);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                shopDetailRequest.ClosingTime = apiFormatter.format(closingTime);
            }

            shopDetailRequest.Latitude=String.valueOf(latitude);
            shopDetailRequest.Longitude=String.valueOf(longitude);

            Log.d("SmartAlerts", "ShopDetailRequest JSON: " + new Gson().toJson(shopDetailRequest));

            Call<APIResult<ShopDetailResponse>> call = apiService.AddShopDetail(shopDetailRequest);
            Log.d("SmartAlerts", "➡️ Requesting URL: " + call.request().url());

            call.enqueue(new Callback<APIResult<ShopDetailResponse>>() {
                @Override
                public void onResponse(Call<APIResult<ShopDetailResponse>> call, Response<APIResult<ShopDetailResponse>> response) {
                    Log.d("SmartAlerts", "Response Code: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        APIResult<ShopDetailResponse> result = response.body();
                        Log.d("SmartAlerts", "Response Message: " + result.ResponseMessage);
                        ShopDetailResponse businessData = result.ResponseData.get(0);

                        if (result.ResponseData != null && !result.ResponseData.isEmpty()) {
                            CommonVariable.ShopProfilePicUrl = businessData.ShopProfilePicUrl;
                            SharedPreferences prefs = getSharedPreferences("SmartAlertsPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("ShopProfilePicUrl", businessData.ShopProfilePicUrl);
                            editor.apply();
                            runOnUiThread(() -> {
                                Toast.makeText(BusinessSignupActivity.this, "Level2 Details Successful", Toast.LENGTH_LONG).show();
                            });
                            Intent intent = new Intent(BusinessSignupActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            runOnUiThread(() -> Toast.makeText(BusinessSignupActivity.this, "⚠️ " + result.ResponseMessage, Toast.LENGTH_LONG).show());
                            Log.w("SmartAlerts", "ResponseData is empty or null");
                        }
                    } else {
                        String errorMsg = "Unknown error";
                        try {
                            if (response.errorBody() != null) {
                                errorMsg = response.errorBody().string();
                                Log.e("SmartAlerts", "❌ Error Body: " + errorMsg);
                            } else {
                                Log.e("SmartAlerts", "❌ Response was not successful, but no error body found.");
                            }
                        } catch (IOException e) {
                            Log.e("SmartAlerts", "❌ Exception while reading error body", e);
                        }

                        String finalErrorMsg = errorMsg;
                        runOnUiThread(() -> Toast.makeText(BusinessSignupActivity.this, "❌ Failed: " + finalErrorMsg, Toast.LENGTH_LONG).show());
                    }
                }

                @Override
                public void onFailure(Call<APIResult<ShopDetailResponse>> call, Throwable t) {
                    Log.e("SmartAlerts", "🚫 API Failure: " + t.getLocalizedMessage(), t);
                    runOnUiThread(() -> Toast.makeText(BusinessSignupActivity.this, "🚫 Error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show());
                }
            });

        } catch (Exception e) {
            Log.e("SmartAlerts", "Business Signup Error: " + e.getMessage(), e);
            Toast.makeText(this, "Unexpected error occurred", Toast.LENGTH_LONG).show();
        }
    }

    private String encodeImageToBase64(Uri imageUri) {
        try {
            if (imageUri == null) return null;

            // Decode bitmap with bounds only to get dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();

            // Calculate inSampleSize to scale down
            int maxDimension = 512; // Resize to max 512px width or height
            int scale = 1;
            while (options.outWidth / scale > maxDimension || options.outHeight / scale > maxDimension) {
                scale *= 2;
            }

            // Decode actual bitmap with scaling
            BitmapFactory.Options scaledOptions = new BitmapFactory.Options();
            scaledOptions.inSampleSize = scale;
            inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap scaledBitmap = BitmapFactory.decodeStream(inputStream, null, scaledOptions);
            inputStream.close();

            // Compress to JPEG with very low quality (20%)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] imageBytes = baos.toByteArray();

            return "data:image/jpeg;base64," + Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public void SignOut(View obj){
        Intent actSignup = new Intent(getApplicationContext(),SignupActivity.class);
        startActivity(actSignup);
        BusinessSignupActivity.this.finish();
    }
}
