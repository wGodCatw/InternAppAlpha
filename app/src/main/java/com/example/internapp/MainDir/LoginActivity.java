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
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.example.internapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

/**
 * Activity for handling user login functionality.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivityV2";
    FirebaseAuth authProfile;
    private TextInputEditText emailField, pwdField;
    private ProgressBar progressBar;

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0; // Default value if not found
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null) {
            Toast.makeText(LoginActivity.this, "You're already logged in", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);


        setContentView(R.layout.activity_login);

        ScrollView scrollView = findViewById(R.id.parentScroll);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) scrollView.getLayoutParams();
        params.topMargin = getStatusBarHeight();

        emailField = findViewById(R.id.txtFieldEmail);
        pwdField = findViewById(R.id.txtFieldPassword);
        progressBar = findViewById(R.id.progress_bar_login);

        Button btnForgotPassword = findViewById(R.id.button_forgot_password);

        btnForgotPassword.setOnClickListener(v -> {
            Toast.makeText(LoginActivity.this, "You can now reset your password", Toast.LENGTH_LONG).show();
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });


        authProfile = FirebaseAuth.getInstance();

        Button login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            String textEmail = Objects.requireNonNull(emailField.getText()).toString();
            String textPwd = Objects.requireNonNull(pwdField.getText()).toString();

            if (TextUtils.isEmpty(textEmail)) {
                Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                emailField.setError("Email is required");
                emailField.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                Toast.makeText(LoginActivity.this, "Email is not valid", Toast.LENGTH_LONG).show();
                emailField.setError("Valid email is required");
                emailField.requestFocus();
            } else if (TextUtils.isEmpty(textPwd)) {
                Toast.makeText(LoginActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
                pwdField.setError("Password is required");
                pwdField.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                loginUser(textEmail, textPwd);
            }
        });

        Button register = findViewById(R.id.register);
        register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Logs in the user with the provided email and password.
     *
     * @param textEmail The email entered by the user.
     * @param textPwd   The password entered by the user.
     */
    private void loginUser(String textEmail, String textPwd) {
        authProfile.signInWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(LoginActivity.this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = authProfile.getCurrentUser();

                assert firebaseUser != null;
                if (firebaseUser.isEmailVerified()) {
                    Toast.makeText(LoginActivity.this, "Log in successful!", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
                    finish();
                } else {
                    firebaseUser.sendEmailVerification();
                    authProfile.signOut();
                    showAlertDialog();
                }
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthInvalidUserException e) {
                    emailField.setError("User doesn't exist or is no longer valid. Please register again");
                    emailField.requestFocus();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    emailField.setError("Invalid credentials, re-enter and try to login again");
                    emailField.requestFocus();
                } catch (Exception e) {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                }
                Toast.makeText(LoginActivity.this, "Log in failed", Toast.LENGTH_LONG).show();
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    /**
     * Shows an alert dialog informing the user that email verification is required.
     */
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You can not login without email verification");

        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}