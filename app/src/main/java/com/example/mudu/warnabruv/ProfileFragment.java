package com.example.mudu.warnabruv;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mudu.warnabruv.Firebase.FirebaseDatabaseHelper;
import com.example.mudu.warnabruv.Helper.Helper;
import com.example.mudu.warnabruv.Helper.SimpleDividerItemDecoration;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.util.Objects;
import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private ImageView profilephoto;
    private TextView profileName;
    private RecyclerView recyclerView;
    private StorageReference userProfileImageRef;
    DatabaseReference databaseReference;
    private String id;
    private static final int REQUEST_READ_PERMISSION = 120;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private String currentUserId;
    // Root Database Name for Firebase Database.
    String Database_Path = "All_Image_Uploads_Database";
    // Folder path for Firebase Storage.
    String Storage_Path = "All_Image_Uploads/";
    Uri selectedImageUri;

    ProgressDialog progressDialog;

    public ProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        setUpFirebaseAuth();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Objects.requireNonNull(getActivity()).setTitle("My Profile");

        currentUserId = auth.getCurrentUser().getUid();

        profileName = view.findViewById(R.id.profile_name);
        profileName.setVisibility(View.GONE);

        userProfileImageRef = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(getActivity());

        profilephoto = view.findViewById(R.id.circleView);
        profilephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryIntent, "Please select Image"), Helper.SELECT_PICTURE);
            }
        });

        recyclerView = view.findViewById(R.id.profile_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new
                SimpleDividerItemDecoration(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ((FirebaseApplication)getActivity().getApplication()).getFirebaseAuth();
        id = ((FirebaseApplication)getActivity().getApplication()).getFirebaseUserAuthenticatedId();

        FirebaseDatabaseHelper firebaseDatabaseHelper = new FirebaseDatabaseHelper();
        firebaseDatabaseHelper.isUserKeyExist(id, getActivity(), recyclerView);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("user id has entered onActivityResult ");
        if (requestCode == Helper.SELECT_PICTURE && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), selectedImageUri);
                // Setting up bitmap selected image into ImageView.
                profilephoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadImageFileToFirebaseStorage();
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    private String getFileExtension(Uri uri) {

        ContentResolver contentResolver = getActivity().getApplicationContext().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;
    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void uploadImageFileToFirebaseStorage() {
        // Checking whether selectedImageUri Is empty or not.
        if (selectedImageUri != null) {
            progressDialog.setTitle("Image is uploading...");
            progressDialog.show();

            // Creating second StorageReference.
            StorageReference storageReference2nd = userProfileImageRef.child(Storage_Path + System.currentTimeMillis()
                                                            + "." + getFileExtension(selectedImageUri));
            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();
                            Toast.makeText(getActivity().getApplicationContext(), "Image Uploaded Successfully", Toast.LENGTH_LONG).show();

                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(taskSnapshot.getStorage().getDownloadUrl().toString());
                            // Adding image upload id s child element into databaseReference.
                            if (currentUserId != null) {
                                databaseReference.child("Users").child(currentUserId).push().setValue(imageUploadInfo);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Hiding the progressDialog.
                            progressDialog.dismiss();
                            // Showing exception error message.
                            Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    // On progress change upload time.
                      .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                          @Override
                          public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                              // Setting progressDialog Title.
                              progressDialog.setTitle("Image is Uploading...");
                          }
                      });
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Please select an Image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getActivity(), "Sorry! You can't use this app without granting this permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setUpFirebaseAuth() {
        Log.d(TAG, "setUpFirebaseAuth: Setting up firebase auth.");

        auth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = auth.getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (currentUser != null) {
                    Log.d(TAG, "onAuthStateChanged signed in : " + currentUser.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged signed out");
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
    }
}
