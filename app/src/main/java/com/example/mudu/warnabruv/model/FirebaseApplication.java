package com.example.mudu.warnabruv.model;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.example.mudu.warnabruv.Home;
import com.example.mudu.warnabruv.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseApplication extends Application {
    public static final String TAG = FirebaseApplication.class.getSimpleName();

    public FirebaseAuth firebaseAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth = FirebaseAuth.getInstance();
    }

    public String getFirebaseUserAuthenticatedId() {
        String userId = null;

        if (firebaseAuth.getCurrentUser() != null) {
            userId = firebaseAuth.getCurrentUser().getUid();
        }
        return userId;
    }
    public void checkUserLogin(final Context context) {
        if (firebaseAuth.getCurrentUser() != null) {
            Intent homeIntent = new Intent(context, Home.class);
            context.startActivity(homeIntent);
        }
    }
    public void isUserCurrentlyLoggedIn(final Context context) {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (null != user) {
                    Intent homeIntent = new Intent(context, Home.class);
                    context.startActivity(homeIntent);
                } else {
                    Intent loginIntent = new Intent(context, MainActivity.class);
                    context.startActivity(loginIntent);
                }
            }
        };
    }
}
