package com.smart.alerts;

import android.content.Intent;
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

import com.smart.alerts.models.APIResult;
import com.smart.alerts.models.CommonVariable;
import com.smart.alerts.models.ForgotPwRequest;
import com.smart.alerts.models.ForgotPwResponse;
import com.smart.alerts.models.LoginRequest;
import com.smart.alerts.models.SigninResponse;
import com.smart.alerts.services.APIService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText etUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        etUserName = findViewById(R.id.etUsername);
    }

    public void OnGeneratePwClick(View view) {
        try {
            GeneratePwd();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Unexpected error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void GeneratePwd() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CommonVariable.ApibaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            APIService apiService = retrofit.create(APIService.class);

            ForgotPwRequest forgotPasswordRequest = new ForgotPwRequest();
            forgotPasswordRequest.Email = etUserName.getText().toString(); // Get email from input

            Call<APIResult<ForgotPwResponse>> call = apiService.ForgotPassword(forgotPasswordRequest);
            call.enqueue(new Callback<APIResult<ForgotPwResponse>>() {
                @Override
                public void onResponse(Call<APIResult<ForgotPwResponse>> call, Response<APIResult<ForgotPwResponse>> response) {
                    if (response.isSuccessful()) {
                        APIResult<ForgotPwResponse> forgotPasswordResponse = response.body();
                        runOnUiThread(() -> {
                            Toast.makeText(ForgotPasswordActivity.this, "Reset link sent to your email", Toast.LENGTH_LONG).show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset link", Toast.LENGTH_LONG).show();
                        });
                    }
                }

                @Override
                public void onFailure(Call<APIResult<ForgotPwResponse>> call, Throwable t) {
                    runOnUiThread(() -> {
                        Toast.makeText(ForgotPasswordActivity.this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            });

        } catch (Exception ex) {
            Log.d("Smart Alerts", "ForgotPassword: " + ex.getLocalizedMessage());
            Toast.makeText(ForgotPasswordActivity.this, "Error occurred", Toast.LENGTH_LONG).show();
        }
    }
    public void SignOut(View obj){
        Intent actMain = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(actMain);
        ForgotPasswordActivity.this.finish();
    }
}
