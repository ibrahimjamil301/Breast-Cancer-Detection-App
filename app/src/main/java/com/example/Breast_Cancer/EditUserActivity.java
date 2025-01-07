package com.example.Breast_Cancer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditUserActivity extends AppCompatActivity {
    private EditText fullNameEditText, emailEditText;
    private Button saveButton;
    private FirebaseFirestore firestore;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fullNameEditText = findViewById(R.id.fullNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        saveButton = findViewById(R.id.saveButton);

        userId = getIntent().getStringExtra("userId");
        String fullName = getIntent().getStringExtra("fullName");
        String email = getIntent().getStringExtra("email");

        fullNameEditText.setText(fullName);
        emailEditText.setText(email);
        firestore = FirebaseFirestore.getInstance();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFullName = fullNameEditText.getText().toString().trim();
                String newEmail = emailEditText.getText().toString().trim();
                if (TextUtils.isEmpty(newFullName) || TextUtils.isEmpty(newEmail)) {
                    Toast.makeText(EditUserActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateUserInFirestore(userId, newFullName, newEmail);
            }
        });
    }
    private void updateUserInFirestore(String userId, String fullName, String email) {
        firestore.collection("users")
                .document(userId)
                .update("fullName", fullName, "email", email)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditUserActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(EditUserActivity.this, "Error updating user", Toast.LENGTH_SHORT).show());
    }
}