package helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.example.levi.resita.R;

import java.io.File;
import java.util.Objects;


public class PictureDialog extends DialogFragment {
    private final static String TAG = "PictureDialog";
    private final static int CAMERA_REQUEST_CODE = 1;
    private final static int PICK_FROM_GALLERY_CODE = 2;
    DialogClickListener mListenter;
    public PictureDialog() {}
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use builder class for convenient construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.picture_dialog_title)
               .setItems(R.array.picture_options, (dialogInterface, which) -> {
                   switch (which) {
                       case 0:
                           // start camera
                           startCamera();
                           break;
                       case 1:
                           // open gallery
                           openGallery();
                           break;
                   }
               });
        return builder.create();
    }

    private void startCamera() {
        Log.d(TAG, "starting camera.");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        getDialog().dismiss();
    }

    private void openGallery() {
        Log.d(TAG, "opening gallery.");
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_FROM_GALLERY_CODE);
        getDialog().dismiss();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            Log.d(TAG, "attaching");
            if (context instanceof Activity) mListenter = (DialogClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement listeners");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Fragment oar called");
    }

    public interface DialogClickListener {
        void getBitmapImage(Bitmap bitmap);
        void getImagePath(String imagePath);
    }
}
