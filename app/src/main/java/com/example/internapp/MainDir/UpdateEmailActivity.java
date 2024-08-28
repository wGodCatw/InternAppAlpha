package com.example.internapp.MainDir;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
 * Activity for updating user email address after re-authentication.
 */
public class UpdateEmailActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private TextView txtAuthenticated;
    private String oldEmail, updEmail, password;
    private TextInputEditText edtoldEmail, edtupdEmail, edtpassword;
    private Button btnUpdateEmail, btnAuth;

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

        setContentView(R.layout.activity_update_email);

        ConstraintLayout constraintLayout = findViewById(R.id.parentConstraint); // Replace with your actual layout ID

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) constraintLayout.getLayoutParams();
        params.topMargin = getStatusBarHeight();

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
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        assert firebaseUser != null;
        oldEmail = firebaseUser.getEmail();
        edtoldEmail.setText(oldEmail);

        if (firebaseUser.equals("")) {
            Toast.makeText(UpdateEmailActivity.this, "Something went wrong, user details not available", Toast.LENGTH_LONG).show();
        } else {
            reAuthenticate(firebaseUser);
        }
    }

    /**
     * Handles re-authentication of the user with their password.
     *
     * @param firebaseUser The current Firebase user.
     */
    private void reAuthenticate(FirebaseUser firebaseUser) {
        btnAuth.setOnClickListener(v -> {
            password = Objects.requireNonNull(edtpassword.getText()).toString();
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(UpdateEmailActivity.this, "Please, enter your password", Toast.LENGTH_LONG).show();
                edtpassword.requestFocus();
                edtpassword.setError("Password is required");
            } else {
                progressBar.setVisibility(View.VISIBLE);
                AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, password);
                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UpdateEmailActivity.this, "Password has been verified, you can update your email now", Toast.LENGTH_LONG).show();
                        txtAuthenticated.setText("You are authenticated. You can update your email now");

                        edtoldEmail.setEnabled(false);
                        edtpassword.setEnabled(false);

                        edtupdEmail.setEnabled(true);
                        btnAuth.setEnabled(false);
                        btnUpdateEmail.setEnabled(true);

                        btnUpdateEmail.setOnClickListener(v1 -> {
                            updEmail = Objects.requireNonNull(edtupdEmail.getText()).toString();
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
                        });
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    /**
     * Updates the email address of the user after successful re-authentication.
     *
     * @param firebaseUser The current Firebase user.
     * @param email        The new email address to be updated.
     */
    private void updateEmail(FirebaseUser firebaseUser, String email) {
        firebaseUser.verifyBeforeUpdateEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Email has been updated. Please check your inbox to verify it", Toast.LENGTH_LONG).show();
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (Exception e) {
                    Log.e("TAG", Objects.requireNonNull(e.getMessage()));
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            progressBar.setVisibility(View.GONE);
        });
        Intent intent = new Intent(UpdateEmailActivity.this, LoginActivity.class);
        authProfile.signOut();
        startActivity(intent);
        finish();
    }
}
