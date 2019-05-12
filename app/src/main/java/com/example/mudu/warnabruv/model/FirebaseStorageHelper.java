package com.example.mudu.warnabruv.model;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mudu.warnabruv.other.CircleTransform;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.Objects;

public class FirebaseStorageHelper {

    private static final String TAG = FirebaseStorageHelper.class.getCanonicalName();

    private FirebaseStorage firebaseStorage;

    private StorageReference rootReference;

    private Context context;

    public FirebaseStorageHelper(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        this.firebaseStorage = FirebaseStorage.getInstance();
        rootReference = firebaseStorage.getReferenceFromUrl("https://warn-a-bruv-222623.firebaseio.com/");
    }

    public void saveProfileImageToCloud(String userId, Uri selectedImageUri, final ImageView imageView) {

        StorageReference photoParentRef = rootReference.child(userId);
        StorageReference photoRef = photoParentRef.child(Objects.requireNonNull(selectedImageUri.getLastPathSegment()));
        UploadTask uploadTask = photoRef.putFile(selectedImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "OnFailure " + e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUrl = uri.toString();
                                Glide.with(context).load(downloadUrl)
                                        .crossFade()
                                        .thumbnail(0.5f)
                                        .bitmapTransform(new CircleTransform(context))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(imageView);
                            }
                        });
                    }
                }
            }
        });

    }
}
