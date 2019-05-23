package com.example.mudu.warnabruv.Firebase;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import com.example.mudu.warnabruv.UserProfile;
import com.example.mudu.warnabruv.adapter.RecyclerViewAdapter;
import com.example.mudu.warnabruv.Helper.Helper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FirebaseDatabaseHelper {

    private static final String TAG = FirebaseDatabaseHelper.class.getSimpleName();

    private DatabaseReference databaseReference;

    public FirebaseDatabaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void createUserInFirebaseDatabase(String userId, FirebaseUserEntity firebaseUserEntity) {
        Map<String, FirebaseUserEntity> user = new HashMap<String, FirebaseUserEntity>();
        user.put(userId, firebaseUserEntity);
        databaseReference.child("users").setValue(user);
    }

    public void isUserKeyExist(final String uid, final Context context, final RecyclerView recyclerView) {
        databaseReference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                System.out.println("User login 1 " + dataSnapshot.getKey() + " " + dataSnapshot.getValue());
                List<UserProfile> userData = adapterSourceData(dataSnapshot, uid);
                System.out.println("User login size " + userData.size());
                RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(context, userData);
                recyclerView.setAdapter(recyclerViewAdapter);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                List<UserProfile> userData = adapterSourceData(dataSnapshot, uid);
                System.out.println("User login Size " + userData.size());
                RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter((Activity)context, userData);
                recyclerView.setAdapter(recyclerViewAdapter);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private List<UserProfile> adapterSourceData(DataSnapshot dataSnapshot, String uId) {
        List<UserProfile> allUserData = new ArrayList<UserProfile>();
        if (Objects.requireNonNull(dataSnapshot.getKey()).equals(uId)) {
            FirebaseUserEntity userInformation = dataSnapshot.getValue(FirebaseUserEntity.class);
            assert userInformation != null;
            allUserData.add(new UserProfile(Helper.NAME, userInformation.getName()));
            allUserData.add(new UserProfile(Helper.EMAIL, userInformation.getEmail()));
            allUserData.add(new UserProfile(Helper.PHONE, userInformation.getPhone()));
        }
        return allUserData;
    }
}
