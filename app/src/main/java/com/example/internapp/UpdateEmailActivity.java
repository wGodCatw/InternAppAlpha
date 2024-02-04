package com.example.internapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmailActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private FirebaseUser firebaseUser;
    private TextView txtAuthenticated;
    private String oldEmail, updEmail, password;
    private TextInputEditText edtoldEmail, edtupdEmail, edtpassword;
    private Button btnUpdateEmail, btnAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        progressBar = findViewById(R.id.progressBar);
        txtAuthenticated = findViewById(R.id.titleUpdEmailAuth);
        edtoldEmail = findViewById(R.id.updEmailEmail);
        edtupdEmail = findViewById(R.id.updEmailNewEmail);
        edtpassword = findViewById(R.id.updEmailPassword);
        btnUpdateEmail = findViewById(R.id.updEmailBtnUpdate);
        btnAuth = findViewById(R.id.updEmailBtnAuth);


        btnUpdateEmail.setEnabled(false);
        edtupdEmail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        oldEmail = firebaseUser.getEmail();
        edtoldEmail.setText(oldEmail);

        if (firebaseUser.equals("")) {
            Toast.makeText(UpdateEmailActivity.this, "Something went wrong, user details not available", Toast.LENGTH_LONG).show();
        } else {
            reAuthenticate(firebaseUser);
        }

    }

    private void reAuthenticate(FirebaseUser firebaseUser) {
        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = edtpassword.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(UpdateEmailActivity.this, "Please, enter your password", Toast.LENGTH_LONG).show();
                    edtpassword.requestFocus();
                    edtpassword.setError("Password is required");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, password);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(UpdateEmailActivity.this, "Password has been verified, you can update your email now", Toast.LENGTH_LONG).show();
                                txtAuthenticated.setText("You are authenticated. You can update your email now");

                                edtoldEmail.setEnabled(false);
                                edtpassword.setEnabled(false);

                                edtupdEmail.setEnabled(true);
                                btnAuth.setEnabled(false);
                                btnUpdateEmail.setEnabled(true);

                                btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        updEmail = edtupdEmail.getText().toString();
                                        if (TextUtils.isEmpty(updEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "Please, enter your new email", Toast.LENGTH_LONG).show();
                                            edtupdEmail.requestFocus();
                                            edtupdEmail.setError("Write your new email here");
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(updEmail).matches()) {
                                            Toast.makeText(UpdateEmailActivity.this, "Please, enter valid email", Toast.LENGTH_LONG).show();
                                            edtupdEmail.requestFocus();
                                            edtupdEmail.setError("Valid email is required");
                                        } else if (updEmail.matches(oldEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "New email cannot be the same as the old one", Toast.LENGTH_LONG).show();
                                            edtupdEmail.requestFocus();
                                            edtupdEmail.setError("Please enter new email");
                                        } else {
                                            progressBar.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser, updEmail);
                                        }
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                    });


                }
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser, String email) {
        firebaseUser.verifyBeforeUpdateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Email has been updated. Please check you inbox to verify it", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Log.e("TAG", e.getMessage());
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
        Intent intent = new Intent(UpdateEmailActivity.this, LoginActivity.class);
        authProfile.signOut();
        startActivity(intent);
        finish();
    }
}