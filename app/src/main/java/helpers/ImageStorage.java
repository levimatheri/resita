package helpers;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class ImageStorage extends Storage {
    private final static String TAG = "ImageStorage";
    private StorageReference imageRef;
    private ImageView imageView;
    public ImageStorage(String customBucketPath, ImageView imageView) {
        super(customBucketPath);
        imageRef = (StorageReference) setup();
        this.imageView = imageView;
    }

    @Override
    Object setup() {
        FirebaseStorage storage = (FirebaseStorage) super.setup();
        return storage.getReference().child("receipt_images/receipt_"
                + System.currentTimeMillis() + ".jpg");
    }

    @Override
    public Object[] upload() {
        // get data from ImageView as bytes
        if (imageView != null) {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] data = baos.toByteArray();

            if (imageRef != null) {
                return new Object[]{imageRef ,imageRef.putBytes(data)};
            }
        }
        return null;
    }
}
