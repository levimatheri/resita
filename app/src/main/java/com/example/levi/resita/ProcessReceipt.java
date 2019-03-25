package com.example.levi.resita;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import helpers.ImageStorage;

public class ProcessReceipt extends AppCompatActivity {
    private final static String TAG = "ProcessReceipt";
    public ProgressBar mProgressBarTop, mProgressBarBottom;
    private ImageView mReceiptImage;
    private String downloadUrl;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_receipt);

        mProgressBarTop = findViewById(R.id.loadReceiptImage);
        mProgressBarBottom = findViewById(R.id.processingProgress);
        mReceiptImage = findViewById(R.id.receiptImage);
        Button mProcess = findViewById(R.id.process_button);
        downloadUrl = null;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            setReceiptImageView();
        }
        mProcess.setOnClickListener(view -> {

            // send picture to firebase storage
            ImageStorage storage = new ImageStorage("gs://resita-d3ff2.appspot.com", mReceiptImage);
            Object[] responseObj = storage.upload();
            UploadTask uploadTask = (UploadTask) responseObj[1];
            StorageReference imageRef = (StorageReference) responseObj[0];
            if (uploadTask != null) {
                mProgressBarBottom.setVisibility(View.VISIBLE);
                uploadTask.continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        Log.d(TAG, "Upload failed");
                        throw Objects.requireNonNull(task.getException());
                    }

                    // continue with task to get download URL
                    return imageRef.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    mProgressBarBottom.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Upload successful");
                        if (task.getResult() != null) {
                            downloadUrl = task.getResult().toString();
                            Log.d(TAG, downloadUrl);
                        }
                    }
                });
            }
        });
    }

    private void setReceiptImageView() {
        mProgressBarTop.setVisibility(View.VISIBLE);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String imagePath = extras.getString("receipt_image_path");
            Bitmap image = (Bitmap) extras.get("receipt_image");
            if (imagePath != null) {
                try (InputStream is = new FileInputStream(imagePath)) {
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    mReceiptImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                }
            }
            else if (image != null) {
                mReceiptImage.setImageBitmap(image);
            }
        }
        mProgressBarTop.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setReceiptImageView();
            } else {
                Toast.makeText(this, "Please give your permission.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
