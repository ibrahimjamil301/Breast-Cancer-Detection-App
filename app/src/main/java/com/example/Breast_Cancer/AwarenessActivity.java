package com.example.Breast_Cancer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AwarenessActivity extends AppCompatActivity {

    ImageView home_btn, doctor_btn,profile_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_awareness);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        home_btn = findViewById(R.id.home_icon);
        doctor_btn = findViewById(R.id.doc_icon);
        profile_btn = findViewById(R.id.profile_icon);


        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent = new Intent(AwarenessActivity.this,HomeActivity.class);
                startActivity(new_intent);
            }
        });

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent = new Intent(AwarenessActivity.this,ProfileActivity.class);
                startActivity(new_intent);
            }
        });

        doctor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent = new Intent(AwarenessActivity.this,ScanActivity.class);
                startActivity(new_intent);
            }
        });

    }
}