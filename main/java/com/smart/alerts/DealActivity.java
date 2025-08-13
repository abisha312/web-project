package com.smart.alerts;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.smart.alerts.models.APIResult;
import com.smart.alerts.models.CommonVariable;
import com.smart.alerts.models.DealModel;
import com.smart.alerts.models.ExistingDealModel;
import com.smart.alerts.services.APIService;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DealActivity extends AppCompatActivity {

    /* ───── Views ───── */
    private ImageView img1, img2, img3;
    private EditText  etStartDate, etEndDate, etDealName, etPrice, etPercent;
    private Button    btnPost;

    /* ───── State ───── */
    private boolean isUpdateMode = false;
    private long    shopDealId   = -1L;

    /* URLs of existing images (for update mode) */
    private String existingImg1, existingImg2, existingImg3;

    /* URIs of newly‑picked images */
    private Uri imageUri1, imageUri2, imageUri3;

    /* ───────────────────────── image pickers */
    private final ActivityResultLauncher<Intent> picker1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            r -> { if (r.getResultCode()==RESULT_OK && r.getData()!=null) {
                imageUri1 = r.getData().getData();
                img1.setImageURI(imageUri1);
            } });

    private final ActivityResultLauncher<Intent> picker2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            r -> { if (r.getResultCode()==RESULT_OK && r.getData()!=null) {
                imageUri2 = r.getData().getData();
                img2.setImageURI(imageUri2);
            } });

    private final ActivityResultLauncher<Intent> picker3 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            r -> { if (r.getResultCode()==RESULT_OK && r.getData()!=null) {
                imageUri3 = r.getData().getData();
                img3.setImageURI(imageUri3);
            } });

    /* ───────────────────────── lifecycle */
    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deal);

        /* safe insets */
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v,in)->{ Insets s=in.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(s.left,s.top,s.right,s.bottom); return in; });

        /* find views */
        img1 = findViewById(R.id.imgPreview1);
        img2 = findViewById(R.id.imgPreview2);
        img3 = findViewById(R.id.imgPreview3);
        etDealName = findViewById(R.id.etDealName);
        etPrice    = findViewById(R.id.etDealPrice);
        etPercent  = findViewById(R.id.etDealPercent);
        etStartDate= findViewById(R.id.etStartDate);
        etEndDate  = findViewById(R.id.etEndDate);
        btnPost    = findViewById(R.id.btnPost);

        /* pick buttons */
        findViewById(R.id.btnSelectImage1).setOnClickListener(v->pick(picker1));
        findViewById(R.id.btnSelectImage2).setOnClickListener(v->pick(picker2));
        findViewById(R.id.btnSelectImage3).setOnClickListener(v->pick(picker3));

        etStartDate.setOnClickListener(v->showDateTimePicker(etStartDate));
        etEndDate  .setOnClickListener(v->showDateTimePicker(etEndDate));

        /* ——— check if opened in update mode ——— */
        Intent in = getIntent();
        if (in != null && in.getBooleanExtra("isUpdate", false)) {
            isUpdateMode = true;
            shopDealId   = in.getLongExtra("ShopDealID", -1);
            Log.d("✅ ShopDealID","Intent value = "+shopDealId);

            etDealName .setText(in.getStringExtra("DealName"));
            etPrice    .setText(String.valueOf(in.getDoubleExtra("DealPrice",0)));
            etPercent  .setText(String.valueOf(in.getDoubleExtra("DealPercent",0)));
            etStartDate.setText(in.getStringExtra("DealStartDate"));
            etEndDate  .setText(in.getStringExtra("DealEndDate"));

            /* existing image URLs */
            existingImg1 = in.getStringExtra("DealImage1");
            existingImg2 = in.getStringExtra("DealImage2");
            existingImg3 = in.getStringExtra("DealImage3");

            if (existingImg1 != null && !existingImg1.isEmpty())
                Picasso.get().load(existingImg1).placeholder(R.drawable.clothing).error(R.drawable.clothing).into(img1);
            if (existingImg2 != null && !existingImg2.isEmpty())
                Picasso.get().load(existingImg2).placeholder(R.drawable.clothing).error(R.drawable.clothing).into(img2);
            if (existingImg3 != null && !existingImg3.isEmpty())
                Picasso.get().load(existingImg3).placeholder(R.drawable.clothing).error(R.drawable.clothing).into(img3);

            btnPost.setText("Update Deal");
        }

        btnPost.setOnClickListener(v -> {
            if (isUpdateMode) updateDeal(); else addDeal();
        });
    }

    /* ───────────────────────── add new deal ───────────────────────── */
    private void addDeal() {
        DealModel m = makeBaseDealModel();
        callApi(getApi().AddDeal(m));
    }

    /* ───────────────────────── update existing ────────────────────── */
    private void updateDeal() {
        ExistingDealModel up = new ExistingDealModel();
        up.ShopDealID    = shopDealId;
        up.DealName      = etDealName.getText().toString().trim();
        up.DealStartDate = etStartDate.getText().toString().trim();
        up.DealEndDate   = etEndDate  .getText().toString().trim();
        up.DealPrice     = parse(etPrice.getText().toString());
        up.DealPercent   = parse(etPercent.getText().toString());

        /* ---- image #1 (required) ---- */
        if (imageUri1 != null) {            // user picked new picture
            up.DealImage1 = imgToBase64(imageUri1);
        } else {                            // reuse displayed bitmap
            up.DealImage1 = viewToBase64(img1);
        }
        if (up.DealImage1 == null) {
            Toast.makeText(this,"Image1 is required",Toast.LENGTH_SHORT).show();
            return;
        }

        /* ---- image #2 (optional) ---- */
        up.DealImage2 = (imageUri2 != null) ? imgToBase64(imageUri2)
                : viewToBase64(img2);

        /* ---- image #3 (optional) ---- */
        up.DealImage3 = (imageUri3 != null) ? imgToBase64(imageUri3)
                : viewToBase64(img3);

        callApi(getApi().UpdateDeal(up));
    }

    /* ───────────────────────── commons ────────────────────── */
    private DealModel makeBaseDealModel(){
        DealModel m = new DealModel();
        m.UserID        = CommonVariable.SigninUserID;
        m.DealName      = etDealName.getText().toString().trim();
        m.DealStartDate = etStartDate.getText().toString().trim();
        m.DealEndDate   = etEndDate  .getText().toString().trim();
        m.DealPrice     = parse(etPrice  .getText().toString());
        m.DealPercent   = parse(etPercent.getText().toString());
        m.DealImage1    = imgToBase64(imageUri1);
        m.DealImage2    = imgToBase64(imageUri2);
        m.DealImage3    = imgToBase64(imageUri3);
        return m;
    }

    private Double parse(String s){ try{return s.isEmpty()?null:Double.parseDouble(s);}catch(Exception e){return null;} }

    private void callApi(Call<APIResult<ExistingDealModel>> call){
        call.enqueue(new Callback<APIResult<ExistingDealModel>>() {
            @Override public void onResponse(Call<APIResult<ExistingDealModel>> c, Response<APIResult<ExistingDealModel>> r){
                if (r.isSuccessful() && r.body()!=null) {
                    Log.d("✅ DealAPI", new Gson().toJson(r.body()));
                    Toast.makeText(DealActivity.this,r.body().ResponseMessage,Toast.LENGTH_LONG).show();
                    finish();
                } else showErr(r);
            }
            @Override public void onFailure(Call<APIResult<ExistingDealModel>> c, Throwable t) {
                Toast.makeText(DealActivity.this,"Error: "+t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showErr(Response<?> r){
        try {
            String e = (r.errorBody()!=null) ? r.errorBody().string() : "Unknown";
            Log.e("❌ DealAPI", e);
            Toast.makeText(this,"Failed: "+e,Toast.LENGTH_LONG).show();
        } catch (Exception ignore) {}
    }

    /* ------------ api / retrofit ------------ */
    private APIService getApi(){
        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(log).build();
        return new Retrofit.Builder()
                .baseUrl(CommonVariable.ApibaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(APIService.class);
    }

    /* ------------ encode picked image ------------ */
    private String imgToBase64(Uri uri){
        try{
            if (uri == null) return null;
            InputStream in = getContentResolver().openInputStream(uri);
            Bitmap bmp = BitmapFactory.decodeStream(in); in.close();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,20,out);
            return "data:image/jpeg;base64," + Base64.encodeToString(out.toByteArray(),Base64.NO_WRAP);
        }catch(Exception e){ return null; }
    }

    /* ------------ encode bitmap that is already in ImageView ------------ */
    private String viewToBase64(ImageView iv){
        try{
            Bitmap bmp = ((BitmapDrawable) iv.getDrawable()).getBitmap();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,20,out);
            return "data:image/jpeg;base64," + Base64.encodeToString(out.toByteArray(),Base64.NO_WRAP);
        }catch(Exception e){ return null; } // drawable was null or not a bitmap
    }

    /* ------------ datetime picker ------------ */
    private void showDateTimePicker(EditText target){
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this,(v,y,m,d)->{
            c.set(y,m,d);
            new TimePickerDialog(this,(tv,h,min)->{
                c.set(Calendar.HOUR_OF_DAY,h); c.set(Calendar.MINUTE,min);
                String val = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",Locale.getDefault())
                        .format(c.getTime());
                target.setText(val);
            },c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show();
        },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
    }

    /* pick from gallery */
    private void pick(ActivityResultLauncher<Intent> launcher){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*"); launcher.launch(i);
    }

    /* back btn */
    public void Back(View v){ startActivity(new Intent(this,BusinessDashboardActivity.class)); finish(); }
}
