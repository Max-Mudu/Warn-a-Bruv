package com.example.mudu.warnabruv;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.example.mudu.warnabruv.Firebase.FirebaseUserEntity;
import com.example.mudu.warnabruv.Helper.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import java.util.Objects;
import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn, btnRegister;
    RelativeLayout rootLayout;

    //Init Firebase
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_main);

        // Check the current user
        auth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (auth.getCurrentUser() != null) {
                    startActivity(new Intent(MainActivity.this, Home.class));
                }
            }
        };

        db = FirebaseDatabase.getInstance();
        users = db.getReference("Users");

        //Init view
        btnSignIn = findViewById(R.id.btnSignIn);
        btnRegister = findViewById(R.id.btnRegister);
        rootLayout = findViewById(R.id.rootLayout);

        //Event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegistrationDialog();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });
    }

    private void showLoginDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN ")
                .setMessage("Please use email to sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login, null);

        final MaterialEditText editEmail = login_layout.findViewById(R.id.editEmail);
        final MaterialEditText editPassword = login_layout.findViewById(R.id.editPassword);

        dialog.setView(login_layout);

        //Set Button
        dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //Disable Sign In button when processing(Loading)
                //btnSignIn.setEnabled(false);

                //Check validation
                if (TextUtils.isEmpty(Objects.requireNonNull(editEmail.getText()).toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (Helper.isValidEmail(editEmail.getText().toString())){
                    Helper.displayMessageToast(MainActivity.this, "Invalid email entered");
                }
                if (TextUtils.isEmpty(Objects.requireNonNull(editPassword.getText()).toString())) {
                    Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (editPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "Password too short !!!", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                final android.app.AlertDialog waitingDialog = new SpotsDialog(MainActivity.this, R.style.waiting_dialog);
                waitingDialog.show();

                //Login user
                auth.signInWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                waitingDialog.dismiss();
                                Intent intent = new Intent(MainActivity.this, Home.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        waitingDialog.dismiss();
                        Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();

                        //Activate Sign In button
                        //btnSignIn.setEnabled(true);
                    }
                });
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    private void showRegistrationDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER ")
                .setMessage("Please use email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register, null);

        final MaterialEditText editEmail = register_layout.findViewById(R.id.editEmail);
        final MaterialEditText editPassword = register_layout.findViewById(R.id.editPassword);
        final MaterialEditText editName = register_layout.findViewById(R.id.editName);
        final MaterialEditText editPhone = register_layout.findViewById(R.id.editPhone);

        dialog.setView(register_layout);

        //Set Button
        dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                //Check validation
                if (TextUtils.isEmpty(Objects.requireNonNull(editEmail.getText()).toString())) {
                    Snackbar.make(rootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Objects.requireNonNull(editPhone.getText()).toString())) {
                    Snackbar.make(rootLayout, "Please enter phone number", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Objects.requireNonNull(editPassword.getText()).toString())) {
                    Snackbar.make(rootLayout, "Please enter password", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (Helper.isValidEmail(editEmail.getText().toString())){
                    Helper.displayMessageToast(MainActivity.this, "Invalid email entered");
                }
                if (editPassword.getText().toString().length() < 6) {
                    Snackbar.make(rootLayout, "Password too short !!!", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Objects.requireNonNull(editName.getText()).toString())) {
                    Snackbar.make(rootLayout, "Please enter your name", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                final android.app.AlertDialog waitingDialog = new SpotsDialog(MainActivity.this, R.style.waiting_dialog);
                waitingDialog.setTitle("Creating an Account");
                waitingDialog.show();

                //Register new user
                auth.createUserWithEmailAndPassword(editEmail.getText().toString(), editPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                waitingDialog.dismiss();
                                //Save userEntity to database
                                FirebaseUserEntity userEntity = new FirebaseUserEntity();
                                userEntity.setEmail(editEmail.getText().toString());
                                userEntity.setName(editName.getText().toString());
                                userEntity.setPhone(editPhone.getText().toString());
                                userEntity.setPassword(editPassword.getText().toString());

                                //Use email to key
                                users.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                        .setValue(userEntity)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout, "Registration Successful !!!", Snackbar.LENGTH_SHORT).show();
                                                showLoginDialog();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                waitingDialog.dismiss();
                                Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
    }
}

