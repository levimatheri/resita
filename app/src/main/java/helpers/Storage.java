package helpers;

import com.google.firebase.storage.FirebaseStorage;

public abstract class Storage extends CloudFunctions {
    private String customBucketPath;
    Storage(String customBucketPath) {
        this.customBucketPath = customBucketPath;
    }
    @Override
    Object setup() {
        return FirebaseStorage.getInstance(customBucketPath);
    }

    public abstract Object upload();
}
