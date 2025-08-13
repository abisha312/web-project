package com.smart.alerts;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class SignupActivity extends AppCompatActivity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void SignOut(View obj){
        Intent actMain = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(actMain);
        SignupActivity.this.finish();
    }

    public void OnBusinessSignupClick(View view){ BusinessSignup();}

    private void BusinessSignup(){
        Intent intent = new Intent(SignupActivity.this, BusinessSignupActivity.class);
        startActivity(intent);
        finish();
    }

    public void OnUserSignupClick(View view){ UserSignup();}

    private void UserSignup(){
        Intent intent = new Intent(SignupActivity.this, UserSignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
