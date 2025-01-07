package com.example.Breast_Cancer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResultActivity2 extends AppCompatActivity {


    private ImageView home_btn, doctor_btn,profile_btn ,back_btn;
    Button next_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        home_btn = findViewById(R.id.home_icon);
        doctor_btn = findViewById(R.id.doc_icon);
        profile_btn = findViewById(R.id.profile_icon);
        back_btn = findViewById(R.id.back_icon);
        next_btn = findViewById(R.id.Awareness_btn);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity2.this,AwarenessActivity.class);
                startActivity(intent);
            }
        });

        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent = new Intent(ResultActivity2.this,HomeActivity.class);
                startActivity(new_intent);
            }
        });

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent1 = new Intent(ResultActivity2.this,ProfileActivity.class);
                new_intent1.putExtra("fromHome", false);
                new_intent1.putExtra("fromScan",false);
                new_intent1.putExtra("fromResult",false);
                startActivity(new_intent1);
            }
        });

        doctor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent = new Intent(ResultActivity2.this,ScanActivity.class);
                startActivity(new_intent);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent = new Intent(ResultActivity2.this, ScanActivity.class);
                startActivity(new_intent);
            }
        });
    }
}