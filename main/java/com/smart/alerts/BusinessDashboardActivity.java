package com.smart.alerts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.smart.alerts.models.APIResult;
import com.smart.alerts.models.CommonVariable;
import com.smart.alerts.models.DealListResult;
import com.smart.alerts.models.ExistingDealModel;
import com.smart.alerts.services.APIService;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BusinessDashboardActivity extends AppCompatActivity {
    TextView tvShopName;
    ImageView imageViewProfile;
    ListView dealListView;

    private static final int ADD_DEAL_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvShopName = findViewById(R.id.tvShopName);
        imageViewProfile = findViewById(R.id.imageView3);
        dealListView = findViewById(R.id.dealListView);

        // Set shop name
        if (CommonVariable.SigninUserName != null) {
            tvShopName.setText("Hello " + CommonVariable.SigninUserName + "!");
        }

        // Load profile image
        if (CommonVariable.ShopProfilePicUrl != null && !CommonVariable.ShopProfilePicUrl.isEmpty()) {
            try {
                Log.d("SmartAlerts", "🖼 Loading ShopProfilePic URL: " + CommonVariable.ShopProfilePicUrl);
                Picasso.get()
                        .load(CommonVariable.ShopProfilePicUrl)
                        .placeholder(R.drawable.default_shop_pic)
                        .error(R.drawable.default_shop_pic)
                        .into(imageViewProfile);
            } catch (Exception e) {
                Log.e("SmartAlerts", "❌ Failed to load profile image: " + e.getMessage());
                imageViewProfile.setImageResource(R.drawable.default_shop_pic);
            }
        } else {
            imageViewProfile.setImageResource(R.drawable.default_shop_pic);
        }

        loadDeals(); // Initial load
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDeals(); // Reload deals when returning to this screen
    }

    private void loadDeals() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CommonVariable.ApibaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);
        Call<APIResult<DealListResult>> call = apiService.ViewDeals(CommonVariable.SigninUserSpecificID);

        call.enqueue(new Callback<APIResult<DealListResult>>() {
            @Override
            public void onResponse(Call<APIResult<DealListResult>> call, Response<APIResult<DealListResult>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().ResponseData != null && !response.body().ResponseData.isEmpty()) {
                    List<ExistingDealModel> deals = response.body().ResponseData.get(0).Deals;

                    // 🪵 Log full JSON and each deal ID
                    Log.d("SmartAlerts", "📦 Full API Response: " + new Gson().toJson(response.body()));
                    for (ExistingDealModel deal : deals) {
                        Log.d("SmartAlerts", "🆔 ShopDealID: " + deal.ShopDealID + " | Name: " + deal.DealName);
                    }

                    DealAdapter adapter = new DealAdapter(BusinessDashboardActivity.this, deals);
                    dealListView.setAdapter(adapter);
                    Log.d("SmartAlerts", "✅ Loaded " + deals.size() + " deals.");
                } else {
                    Log.d("SmartAlerts", "⚠️ No deals found or null response.");
                    Toast.makeText(BusinessDashboardActivity.this, "No deals found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<APIResult<DealListResult>> call, Throwable t) {
                Log.e("SmartAlerts", "🚫 Failed to fetch deals: " + t.getMessage());
                Toast.makeText(BusinessDashboardActivity.this, "Failed to fetch deals", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void OnAddDealClick(View view) {
        startActivity(new Intent(this, DealActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_DEAL_REQUEST_CODE && resultCode == RESULT_OK) {
            loadDeals(); // Only reload if a deal was actually added
        }
    }

    public void SignOut(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
