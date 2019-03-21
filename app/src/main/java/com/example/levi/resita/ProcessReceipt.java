package com.example.levi.resita;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ProcessReceipt extends AppCompatActivity {
    private final static String TAG = "ProcessReceipt";
    private ProgressBar mProgressBar;
    private ImageView mReceiptImage;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_receipt);

        mProgressBar = findViewById(R.id.processReceiptProgressBar);
        mReceiptImage = findViewById(R.id.receiptImage);
//
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mProgressBar.setVisibility(View.VISIBLE);
                try (InputStream is = new FileInputStream(getIntent().getStringExtra("receipt_image"))) {
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    mReceiptImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Log.d(TAG, e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mProgressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Please give your permission.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
