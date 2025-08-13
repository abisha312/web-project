package com.smart.alerts;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.smart.alerts.models.APIResult;
import com.smart.alerts.models.CommonVariable;
import com.smart.alerts.models.CustomerDetailRequest;
import com.smart.alerts.models.CustomerDetailResponse;
import com.smart.alerts.models.SignupRequest;
import com.smart.alerts.models.SignupResponse;
import com.smart.alerts.services.APIService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserSignupActivity extends AppCompatActivity {
    EditText etFirstName, etLastName, etEmail, etPassword, etRetypePassword, etAddress1, etAddress2, etContactNo, etCity, etPostalCode;
    EditText etDOB, etAnniversary;
    ImageView ivProfilePic;
    Button btnSelectImage;
    Uri profilePicUri;

    final ActivityResultLauncher<Intent> imagePicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    profilePicUri = result.getData().getData();
                    ivProfilePic.setImageURI(profilePicUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_signup);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        fetchFCMToken();
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
        Log.d("SmartAlerts", "🚀 UserSignupActivity started");
        fetchFCMToken(); // runs every time screen becomes visible
    }


    private void initViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etRetypePassword = findViewById(R.id.etRetypePassword);
        etAddress1 = findViewById(R.id.etAddress1);
        etAddress2 = findViewById(R.id.etAddress2);
        etContactNo = findViewById(R.id.etContactNo);
        etCity = findViewById(R.id.etCity);
        etPostalCode = findViewById(R.id.etPostalCode);
        etDOB = findViewById(R.id.etDOB);
        etAnniversary = findViewById(R.id.etAnniversary);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        btnSelectImage.setOnClickListener(v -> pickImage());
        etDOB.setOnClickListener(v -> showDatePickerDialog(etDOB));
        etAnniversary.setOnClickListener(v -> showDatePickerDialog(etAnniversary));
    }

    private void fetchFCMToken() {
        Log.d("SmartAlerts", "📡 Requesting FCM token...");
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    Log.d("SmartAlerts", "📡 Token task completed. Success? " + task.isSuccessful());
                    if (task.isSuccessful()) {
                        CommonVariable.DeviceFCMToken = task.getResult();
                        Log.d("SmartAlerts", "✅ FCM Token: " + CommonVariable.DeviceFCMToken);
                    } else {
                        Log.w("SmartAlerts", "❌ FCM token fetch failed", task.getException());
                    }
                });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePicker.launch(intent);
    }

    private void showDatePickerDialog(EditText target) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH), day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int y, int m, int d) -> target.setText(String.format("%04d-%02d-%02d", y, m + 1, d)),
                year, month, day);
        datePickerDialog.show();
    }

    public void OnSignupInCustomerClick(View view) {
        if (validateAllFields()) {
            SignupInUser();
        }
    }

    private boolean validateAllFields() {
        if (etFirstName.getText().toString().trim().isEmpty() ||
                etLastName.getText().toString().trim().isEmpty() ||
                etEmail.getText().toString().trim().isEmpty() ||
                etPassword.getText().toString().trim().isEmpty() ||
                etRetypePassword.getText().toString().trim().isEmpty() ||
                etAddress1.getText().toString().trim().isEmpty() ||
                etContactNo.getText().toString().trim().isEmpty() ||
                etCity.getText().toString().trim().isEmpty() ||
                etPostalCode.getText().toString().trim().isEmpty()) {

            Toast.makeText(this, "⚠️ Please fill in all required fields", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!etPassword.getText().toString().equals(etRetypePassword.getText().toString())) {
            Toast.makeText(this, "❌ Passwords do not match", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void SignupInUser() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonVariable.ApibaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService apiService = retrofit.create(APIService.class);

        SignupRequest request = new SignupRequest();
        request.FirstName = etFirstName.getText().toString().trim();
        request.LastName = etLastName.getText().toString().trim();
        request.Email = etEmail.getText().toString().trim();
        request.Password = etPassword.getText().toString();
        request.Address1 = etAddress1.getText().toString().trim();
        request.Address2 = etAddress2.getText().toString().trim();
        request.City = etCity.getText().toString().trim();
        request.PostalCode = etPostalCode.getText().toString().trim();
        request.ContactNo = etContactNo.getText().toString().trim();
        request.StateOrProvince = "Tamil Nadu";
        request.Country = "India";
        request.ShopName = "";

        Log.d("SmartAlerts", "📤 SignupRequest: " + new Gson().toJson(request));

        apiService.RegisterUser(request).enqueue(new Callback<APIResult<SignupResponse>>() {
            @Override
            public void onResponse(Call<APIResult<SignupResponse>> call, Response<APIResult<SignupResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().ResponseData.isEmpty()) {
                    CommonVariable.RegisteredUserID = response.body().ResponseData.get(0).UserID;
                    Log.d("SmartAlerts", "✅ Signup Success. UserID: " + CommonVariable.RegisteredUserID);
                    AddCustomerDetails();
                } else {
                    try {
                        Log.e("SmartAlerts", "❌ Signup failed: " + (response.errorBody() != null ? response.errorBody().string() : "Unknown"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(UserSignupActivity.this, "Signup failed. Please try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<APIResult<SignupResponse>> call, Throwable t) {
                Log.e("SmartAlerts", "❌ Signup Error: " + t.getLocalizedMessage(), t);
                Toast.makeText(UserSignupActivity.this, "Error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void AddCustomerDetails() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonVariable.ApibaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        APIService apiService = retrofit.create(APIService.class);

        CustomerDetailRequest detail = new CustomerDetailRequest();
        detail.UserID = CommonVariable.RegisteredUserID;
        detail.ProfilePicURL = encodeImageToBase64(profilePicUri);
        detail.CustomerDOB = etDOB.getText().toString().trim();
        detail.CustomerAnniversary = etAnniversary.getText().toString().trim().isEmpty() ? null : etAnniversary.getText().toString().trim();
        detail.DeviceFCMToken = CommonVariable.DeviceFCMToken;

        Log.d("SmartAlerts", "📤 CustomerDetailRequest: " + new Gson().toJson(detail));

        apiService.AddCustomerDetail(detail).enqueue(new Callback<APIResult<CustomerDetailResponse>>() {
            @Override
            public void onResponse(Call<APIResult<CustomerDetailResponse>> call, Response<APIResult<CustomerDetailResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().ResponseData.isEmpty()) {
                    Log.d("SmartAlerts", "✅ Customer details updated.");
                    Toast.makeText(UserSignupActivity.this, "🎉 Profile Completed", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(UserSignupActivity.this, MainActivity.class));
                    finish();
                } else {
                    Log.w("SmartAlerts", "⚠️ Customer detail response invalid");
                    Toast.makeText(UserSignupActivity.this, "Customer details failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<APIResult<CustomerDetailResponse>> call, Throwable t) {
                Log.e("SmartAlerts", "❌ CustomerDetail API Failure: " + t.getLocalizedMessage(), t);
                Toast.makeText(UserSignupActivity.this, "Error: " + t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
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

    public void SignOut(View obj) {
        startActivity(new Intent(getApplicationContext(), SignupActivity.class));
        finish();
    }
}
