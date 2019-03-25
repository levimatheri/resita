package com.example.levi.resita;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import java.io.File;
import helpers.PictureDialog;

public class MainActivity extends AppCompatActivity
        implements PictureDialog.DialogClickListener {
    private final static String TAG = "MainActivity";

    private String mSelectedImagePath;
    private Bitmap mPhoto;
    private ProgressBar mProgressBar;

    private final static int CAMERA_REQUEST_CODE = 65537;
    private final static int PICK_FROM_GALLERY_CODE = 65538;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = findViewById(R.id.mainActivityProgressBar);
        Button mStartButton = findViewById(R.id.startButton);

        mStartButton.setOnClickListener(view -> {
            // show picture dialog
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            PictureDialog pictureDialog = new PictureDialog();
            pictureDialog.show(transaction, "pictureDialog");
        });
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void navigateToProcessReceipt(int code) {
        Intent processIntent = new Intent(this, ProcessReceipt.class);
        if (code == PICK_FROM_GALLERY_CODE) processIntent.putExtra("receipt_image_path", mSelectedImagePath);
        if (code == CAMERA_REQUEST_CODE) processIntent.putExtra("receipt_image", mPhoto);
        startActivity(processIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mProgressBar.setVisibility(View.VISIBLE);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    mPhoto = (Bitmap) data.getExtras().get("data");
                    navigateToProcessReceipt(CAMERA_REQUEST_CODE);
                }
            }
        }

        if (requestCode == PICK_FROM_GALLERY_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();

                if (selectedImageUri != null) {
                    File file = new File(getRealPathFromURI(selectedImageUri));
                    getImagePath(file.getPath());
                }
                // navigate to ProcessReceipt activity
                navigateToProcessReceipt(PICK_FROM_GALLERY_CODE);
            }
        }
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void getImagePath(String imagePath) {
        if (!imagePath.equals("")) {
            Log.d(TAG, "image path is: " + imagePath);
            mSelectedImagePath = imagePath;
        }
    }
}
