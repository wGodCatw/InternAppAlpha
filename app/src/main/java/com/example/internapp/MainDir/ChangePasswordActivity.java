package com.example.internapp.MainDir;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;

import com.example.internapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

/**
 * The ChangePasswordActivity class allows users to change their password by re-authenticating themselves
 * and then providing a new password.
 */
public class ChangePasswordActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView txtAuthenticated;
    private String oldPassword;
    private TextInputEditText edtOldPassword, edtNewPassword, edtVerifyNewPwd;
    private Button btnUpdatePassword, btnAuth;

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0; // Default value if not found
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

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

        ConstraintLayout constraintLayout = findViewById(R.id.parentConstraint); // Replace with your actual layout ID

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) constraintLayout.getLayoutParams();
        params.topMargin = getStatusBarHeight();



        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        assert firebaseUser != null;
        if (firebaseUser.equals("")) {
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong. User details are not available", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }
    }

    /**
     * Prompts the user to re-authenticate themselves by entering their current password.
     * If the authentication is successful, it enables the fields to enter a new password.
     *
     * @param firebaseUser The current FirebaseUser instance.
     */
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        btnAuth.setOnClickListener(v -> {
            oldPassword = Objects.requireNonNull(edtOldPassword.getText()).toString();
            if (TextUtils.isEmpty(oldPassword)) {
                Toast.makeText(ChangePasswordActivity.this, "Please enter your current password", Toast.LENGTH_LONG).show();
                edtOldPassword.setError("Password is required");
                edtOldPassword.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                // Create an AuthCredential object with the user's email and current password
                AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()), oldPassword);

                // Re-authenticate the user with their current password
                firebaseUser.reauthenticate(authCredential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        edtOldPassword.setEnabled(false);
                        edtNewPassword.setEnabled(true);
                        edtVerifyNewPwd.setEnabled(true);
                        btnAuth.setEnabled(false);
                        btnUpdatePassword.setEnabled(true);

                        txtAuthenticated.setText("You are authenticated. You can change your password now");
                        Toast.makeText(ChangePasswordActivity.this, "You can update your password now", Toast.LENGTH_LONG).show();

                        // Set an OnClickListener for the "Update Password" button
                        btnUpdatePassword.setOnClickListener(v1 -> changePwd(firebaseUser));
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    /**
     * Allows the user to change their password by providing a new password and confirming it.
     *
     * @param firebaseUser The current FirebaseUser instance.
     */
    private void changePwd(FirebaseUser firebaseUser) {
        String newPassword = Objects.requireNonNull(edtNewPassword.getText()).toString();
        String verifyNewPassword = Objects.requireNonNull(edtVerifyNewPwd.getText()).toString();

        if (TextUtils.isEmpty(newPassword)) {
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
        } else {
            progressBar.setVisibility(View.VISIBLE);
            // Update the user's password with the new password
            firebaseUser.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ChangePasswordActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {
                        Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            });
        }
    }
}