package com.example.internapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class ChangePasswordActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private FirebaseUser firebaseUser;
    private TextView txtAuthenticated;
    private String oldPassword, newPassword, verifyNewPassword;
    private TextInputEditText edtOldPassword, edtNewPassword, edtVerifyNewPwd;
    private Button btnUpdatePassword, btnAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        progressBar = findViewById(R.id.progressBar);
        txtAuthenticated = findViewById(R.id.titleUpdPwdAuth);
        edtOldPassword = findViewById(R.id.oldPassword);
        edtNewPassword = findViewById(R.id.newPassword);
        edtVerifyNewPwd = findViewById(R.id.verifyNewPassword);
        btnUpdatePassword = findViewById(R.id.updPasswordBtn);
        btnAuth = findViewById(R.id.updPasswordAuth);


        btnUpdatePassword.setEnabled(false);
        edtNewPassword.setEnabled(false);
        edtVerifyNewPwd.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();


        if (firebaseUser.equals("")) {
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong. User details are not available", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }
    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        btnAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPassword = edtOldPassword.getText().toString();
                if (TextUtils.isEmpty(oldPassword)) {
                    Toast.makeText(ChangePasswordActivity.this, "Please enter your current password", Toast.LENGTH_LONG).show();
                    edtOldPassword.setError("Password is required");
                    edtOldPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), oldPassword);

                    firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                edtOldPassword.setEnabled(false);
                                edtNewPassword.setEnabled(true);
                                edtVerifyNewPwd.setEnabled(true);
                                btnAuth.setEnabled(false);
                                btnUpdatePassword.setEnabled(true);

                                txtAuthenticated.setText("You are authenticated. You can change your password now");
                                Toast.makeText(ChangePasswordActivity.this, "You can update your password now", Toast.LENGTH_LONG).show();

                                btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePwd(firebaseUser);
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        newPassword = edtNewPassword.getText().toString();
        verifyNewPassword = edtVerifyNewPwd.getText().toString();

        if(TextUtils.isEmpty(newPassword)){
            Toast.makeText(ChangePasswordActivity.this, "Please enter your new password", Toast.LENGTH_LONG).show();
            edtNewPassword.setError("Enter your new password");
            edtNewPassword.requestFocus();
        } else if (TextUtils.isEmpty(verifyNewPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "Please verify your new password", Toast.LENGTH_LONG).show();
            edtNewPassword.setError("Verify your new password");
            edtNewPassword.requestFocus();
        } else if (!verifyNewPassword.matches(newPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "Password did not match", Toast.LENGTH_LONG).show();
            edtVerifyNewPwd.setError("Password and verification do not match");
            edtVerifyNewPwd.requestFocus();
        } else if (oldPassword.matches(newPassword)) {
            Toast.makeText(ChangePasswordActivity.this, "New password can not be the same as old password", Toast.LENGTH_LONG).show();
            edtNewPassword.setError("New password matches the old password");
            edtNewPassword.requestFocus();
        } else{
            progressBar.setVisibility(View.VISIBLE);
            firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}