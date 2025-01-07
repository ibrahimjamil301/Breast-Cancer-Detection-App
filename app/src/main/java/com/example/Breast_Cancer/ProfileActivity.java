package com.example.Breast_Cancer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
     FirebaseAuth mAuth;
     FirebaseFirestore db;
     ImageView back_btn,profile_btn_update;
     TextView FullName_tv, Email_tv;
     TextInputEditText FullName_et, Email_et, Password_et;
     Button update_btn, LogOut_btn, deleteAccount_btn;
     boolean fromHome , fromScan, fromResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FullName_et = findViewById(R.id.Full_name_et);
        Email_et = findViewById(R.id.Email_et);
        Password_et = findViewById(R.id.Pass_et);
        FullName_tv = findViewById(R.id.full_name);
        Email_tv = findViewById(R.id.Emailtv);
        back_btn = findViewById(R.id.back_icon);
        profile_btn_update = findViewById(R.id.profile_img);
        update_btn = findViewById(R.id.update_btn);
        LogOut_btn = findViewById(R.id.logout_btn);
        deleteAccount_btn = findViewById(R.id.deleteAccount);

        fromHome = getIntent().getBooleanExtra("fromHome", true);
        fromScan = getIntent().getBooleanExtra("fromScan", true);
        fromResult = getIntent().getBooleanExtra("fromResult", true);
        Log.d("ProfileActivity", "fromHome flag: " + fromHome);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;
                if (fromHome) {
                    Log.d("ProfileActivity", "Navigating back to HomeActivity");
                    intent = new Intent(ProfileActivity.this, HomeActivity.class);

                } else if(fromScan) {
                    Log.d("ProfileActivity", "Navigating to ScanActivity");
                    intent = new Intent(ProfileActivity.this, ScanActivity.class );

                } else if(fromResult) {
                    Log.d("ProfileActivity", "Navigating to ResultActivity");
                    intent = new Intent(ProfileActivity.this, ResultActivity.class );

                } else {
                    intent = new Intent(ProfileActivity.this, ResultActivity2.class );
                }
                startActivity(intent);
                finish();
            }
        });

        update_btn.setOnClickListener(view -> {
            String FullName = FullName_et.getText().toString().trim();
            String Email = Email_et.getText().toString().trim();
            String Password = Password_et.getText().toString().trim();

            if (TextUtils.isEmpty(FullName) && TextUtils.isEmpty(Email) && TextUtils.isEmpty(Password)){
                Toast.makeText(ProfileActivity.this, "Please fill at least one field to update.", Toast.LENGTH_SHORT).show();
                return;

            }
            updateProfile(FullName, Email, Password);

        });

        deleteAccount_btn.setOnClickListener(view -> deleteAccount());
        LogOut_btn.setOnClickListener(view -> logOut());
        loadUserProfile();

    }
    private void updateProfile(String fullName, String email, String password) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(fullName)) {
            String userId = user.getUid();
            Map<String, Object> updates = new HashMap<>();
            updates.put("fullName", fullName);

            db.collection("users").document(userId).update(updates)
                    .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "FullName updated successfully.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Failed to update FullName: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        if (!TextUtils.isEmpty(email)) {
            user.updateEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Email updated successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to update Email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        if (!TextUtils.isEmpty(password)) {
            user.updatePassword(password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Password updated successfully.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to update Password: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void logOut(){
        mAuth.signOut();
        Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void deleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getUid();

        db.collection("users").document(userId).delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Step 2: Delete the user from Firebase Authentication
                        user.delete()
                                .addOnCompleteListener(deleteTask -> {
                                    if (deleteTask.isSuccessful()) {
                                        Toast.makeText(ProfileActivity.this, "Account deleted successfully.",
                                                Toast.LENGTH_SHORT).show();
                                        redirectToMainPage();
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Failed to delete account: " + deleteTask.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to delete user data: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void redirectToMainPage() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No user is logged in.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fullName = documentSnapshot.getString("fullName");
                        String email = documentSnapshot.getString("email");
                        String password = documentSnapshot.getString("password");

                        FullName_et.setText(fullName);
                        Email_et.setText(email);
                        Password_et.setText(password);
                        FullName_tv.setText(fullName);
                        Email_tv.setText(email);
                    } else {
                        Toast.makeText(ProfileActivity.this, "User profile not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}