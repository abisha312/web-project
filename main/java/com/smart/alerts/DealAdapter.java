package com.smart.alerts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import com.smart.alerts.models.APIResult;
import com.smart.alerts.models.CommonVariable;
import com.smart.alerts.models.ExistingDealModel;
import com.smart.alerts.services.APIService;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DealAdapter extends BaseAdapter {

    private final Context context;
    private final List<ExistingDealModel> deals;

    public DealAdapter(Context context, List<ExistingDealModel> deals) {
        this.context = context;
        this.deals = deals;
    }

    @Override
    public int getCount() {
        return deals.size();
    }

    @Override
    public Object getItem(int position) {
        return deals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int dp(int valueDp) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return Math.round(valueDp * dm.density);
    }

    static class ViewHolder {
        ImageView img1, img2, img3;
        LinearLayout imgRow, imgRowBottom;
        TextView name, validity, discount;
        Button updateBtn;
        Button deleteBtn;
        CardView card;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.deal_list_item, parent, false);
            h = new ViewHolder();
            h.img1 = convertView.findViewById(R.id.dealImage1);
            h.img2 = convertView.findViewById(R.id.dealImage2);
            h.img3 = convertView.findViewById(R.id.dealImage3);
            h.imgRow = convertView.findViewById(R.id.dealImageRow);
            h.imgRowBottom = convertView.findViewById(R.id.dealImageRowBottom);
            h.name = convertView.findViewById(R.id.dealName);
            h.validity = convertView.findViewById(R.id.dealValidity);
            h.discount = convertView.findViewById(R.id.dealDiscount);
            h.updateBtn = convertView.findViewById(R.id.updateButton);
            h.deleteBtn = convertView.findViewById(R.id.deleteButton);
            h.card = convertView.findViewById(R.id.cardContainer);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        ExistingDealModel deal = deals.get(position);

        h.name.setText(deal.DealName);

        String from = deal.DealStartDate != null ? deal.DealStartDate.split("T")[0] : "";
        String to = deal.DealEndDate != null ? deal.DealEndDate.split("T")[0] : "";
        h.validity.setText("Valid from " + from + " to " + to);

        StringBuilder sb = new StringBuilder();
        if (deal.DealPercent != null) sb.append(deal.DealPercent.intValue()).append("% OFF");
        if (deal.DealPrice != null) sb.append(" - ₹").append(deal.DealPrice.intValue());
        h.discount.setText(sb.toString());

        boolean has1 = deal.DealImage1 != null && !deal.DealImage1.isEmpty();
        boolean has2 = deal.DealImage2 != null && !deal.DealImage2.isEmpty();
        boolean has3 = deal.DealImage3 != null && !deal.DealImage3.isEmpty();

        h.img1.setVisibility(View.GONE);
        h.img2.setVisibility(View.GONE);
        h.img3.setVisibility(View.GONE);
        h.imgRow.setVisibility(View.GONE);
        h.imgRowBottom.setVisibility(View.GONE);

        if (has1) {
            h.imgRow.setVisibility(View.VISIBLE);
            h.img1.setVisibility(View.VISIBLE);
            Picasso.get().load(deal.DealImage1)
                    .placeholder(R.drawable.clothing)
                    .error(R.drawable.clothing)
                    .into(h.img1);
        } else {
            h.img1.setVisibility(View.VISIBLE);
            h.img1.setImageResource(R.drawable.clothing);
        }

        ViewGroup.LayoutParams p = h.img1.getLayoutParams();
        p.height = (!has2 && !has3) ? dp(180) : dp(90);
        h.img1.setLayoutParams(p);

        if (has2 || has3) {
            h.imgRowBottom.setVisibility(View.VISIBLE);

            if (has2) {
                h.img2.setVisibility(View.VISIBLE);
                Picasso.get().load(deal.DealImage2)
                        .placeholder(R.drawable.clothing)
                        .error(R.drawable.clothing)
                        .into(h.img2);
            }

            if (has3) {
                h.img3.setVisibility(View.VISIBLE);
                Picasso.get().load(deal.DealImage3)
                        .placeholder(R.drawable.clothing)
                        .error(R.drawable.clothing)
                        .into(h.img3);
            }
        }

        boolean isExpired = isDealExpired(deal.DealEndDate);

        if (isExpired) {
            h.card.setCardBackgroundColor(Color.parseColor("#EEEEEE"));
            h.updateBtn.setVisibility(View.GONE);
            h.deleteBtn.setVisibility(View.GONE);
        } else {
            h.card.setCardBackgroundColor(Color.WHITE);
            h.updateBtn.setVisibility(View.VISIBLE);
            h.updateBtn.setOnClickListener(v -> {
                Log.d("✅ IntentPassing", "Passing ShopDealID: " + deal.ShopDealID);  // LOG ID being passed

                Intent intent = new Intent(context, DealActivity.class);
                intent.putExtra("isUpdate", true);
                intent.putExtra("ShopDealID", deal.ShopDealID); // This now works with Long
                intent.putExtra("DealName", deal.DealName);
                intent.putExtra("DealPrice", deal.DealPrice);
                intent.putExtra("DealPercent", deal.DealPercent);
                intent.putExtra("DealStartDate", deal.DealStartDate);
                intent.putExtra("DealEndDate", deal.DealEndDate);
                intent.putExtra("DealImage1", deal.DealImage1);
                intent.putExtra("DealImage2", deal.DealImage2);
                intent.putExtra("DealImage3", deal.DealImage3);
                context.startActivity(intent);
            });
            h.deleteBtn.setVisibility(View.VISIBLE);
            h.deleteBtn.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Deal")
                        .setMessage("Are you sure you want to delete this deal?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            deleteDealFromServer(deal.ShopDealID, position);
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

        }

        return convertView;
    }

    private boolean isDealExpired(String endDateStr) {
        if (endDateStr == null || endDateStr.isEmpty()) return false;
        try {
            String dateOnly = endDateStr.split("T")[0];
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date endDate = sdf.parse(dateOnly);
            Date today = new Date();
            return endDate != null && endDate.before(today);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void deleteDealFromServer(long shopDealId, int position) {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CommonVariable.ApibaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            APIService apiService = retrofit.create(APIService.class);
            Call<APIResult<String>> call = apiService.deleteDeal(shopDealId);

            Log.d("SmartAlerts", "➡️ Requesting URL: " + call.request().url());

            call.enqueue(new Callback<APIResult<String>>() {
                @Override
                public void onResponse(Call<APIResult<String>> call, Response<APIResult<String>> response) {
                    Log.d("SmartAlerts", "Response Code: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        APIResult<String> result = response.body();
                        Log.d("SmartAlerts", "Response Message: " + result.ResponseMessage);

                        if (result.ResponseData != null && !result.ResponseData.isEmpty()) {
                            deals.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Deal deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Delete failed: " + result.ResponseMessage, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Failed to delete deal", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<APIResult<String>> call, Throwable t) {
                    Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(context, "Unexpected error occurred", Toast.LENGTH_LONG).show();
        }
    }
}
