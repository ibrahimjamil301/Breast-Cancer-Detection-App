package com.example.Breast_Cancer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {
    ImageView home_btn, doctor_btn,profile_btn;
    Button btnAdminPanel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        home_btn = findViewById(R.id.home_icon);
        doctor_btn = findViewById(R.id.doc_icon);
        profile_btn = findViewById(R.id.profile_icon);
        btnAdminPanel = findViewById(R.id.btn_Admin_Panel);

        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isAdmin = sharedPreferences.getBoolean("isAdmin", true);

       if (isAdmin){
            btnAdminPanel.setVisibility(View.VISIBLE);
            btnAdminPanel.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, AdminPanelActivity.class);
                startActivity(intent);
            });

        } else {
            btnAdminPanel.setVisibility(View.GONE);
        }

        doctor_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               Intent intent = new Intent(HomeActivity.this, ScanActivity.class);
                intent.putExtra("fromHome", true);
               startActivity(intent);
            }
        });

        profile_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent new_intent = new Intent(HomeActivity.this,ProfileActivity.class );
                startActivity(new_intent);
            }
        });
    }
}