package com.example.Breast_Cancer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.annotation.NonNull;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import android.Manifest;


public class ScanActivity extends AppCompatActivity {
     String classificationResult = null;
     private static final int CAMERA_REQUEST_CODE = 100;
     private static final int PERMISSION_REQUEST_CODE = 100;
     private static final int GALLERY_REQUEST_CODE = 1001;
     Button Start_btn ;
     TextView captureImgBtn;
     ImageView home_btn,profile_btn, imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        captureImgBtn = findViewById(R.id.capture_img);
        imageView = findViewById(R.id.upload_area);
        Start_btn = findViewById(R.id.start_btn);
        home_btn = findViewById(R.id.home_icon);
        profile_btn = findViewById(R.id.profile_icon);

        Start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (classificationResult == null || classificationResult.trim().isEmpty()) {
                    Toast.makeText(ScanActivity.this,
                            "No result yet! Please capture an image or wait a while.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (classificationResult.equalsIgnoreCase("Benign")) {
                    Intent intent = new Intent(ScanActivity.this, ResultActivity.class);
                    startActivity(intent);

                } else if (classificationResult.equalsIgnoreCase("Malignant")) {
                    Intent intent = new Intent(ScanActivity.this, ResultActivity2.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(ScanActivity.this,
                            "Unknown value: " + classificationResult,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent = new Intent(ScanActivity.this,HomeActivity.class);
                startActivity(new_intent);
            }
        });

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent1 = new Intent(ScanActivity.this,ProfileActivity.class);
                new_intent1.putExtra("fromHome", false);
                startActivity(new_intent1);
            }
        });

        captureImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceDialog();

            }
        });
    }
    private void showImageSourceDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
        builder.setTitle("please chose ");
        builder.setItems(new CharSequence[] {"Open Camera", "Open Gallery"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            checkPermissionsAndOpenCamera();

                        } else if (which == 1) {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                            Log.d("GalleryDebug", "Gallery intent started.");

                        }
                    }
                });
        builder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onActivityResult", "Request Code: " + requestCode + ", Result Code: " + resultCode);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                if (data != null && data.getExtras() != null) {

                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

                        if (imageBitmap != null) {
                            imageView.setImageBitmap(imageBitmap);
                            sendImage(imageBitmap);
                        } else {
                            Toast.makeText(this, "Failed to capture image.", Toast.LENGTH_SHORT).show();
                        }
                }
            } else if (requestCode == GALLERY_REQUEST_CODE) {

                if (data != null ) {
                    Uri selectedImage = data.getData();
                    Log.d("GalleryDebug", "Selected Image URI: " + selectedImage);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        imageView.setImageBitmap(bitmap);
                        sendImage(bitmap);

                    } catch (Exception e) {
                        Log.e("GalleryError", "Error processing gallery image", e);
                    }
                }
            }
            else {
                Toast.makeText(this, "Action canceled.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void sendImage(Bitmap bitmap) {

        String serverUrl = "http://192.168.1.10:5000/predict";
        ImageUploader.uploadImageAsync(bitmap, serverUrl, new UploadCallback() {
            @Override
            public void onSuccess(String responseBody) {
                runOnUiThread(() -> {

                    Toast.makeText(ScanActivity.this,
                            "Upload success: " + responseBody,
                            Toast.LENGTH_LONG).show();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        String prediction = json.optString("prediction", "");
                        double confidence = json.optDouble("confidence", 0.0);
                        classificationResult = prediction;

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ScanActivity.this,
                                "JSON parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onError(Exception e) {

                runOnUiThread(() -> {
                    Toast.makeText(ScanActivity.this,
                            "Upload error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    private void checkPermissionsAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            openCamera();
        }
    }
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to use this feature.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}