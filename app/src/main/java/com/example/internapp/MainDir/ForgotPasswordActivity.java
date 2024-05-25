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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;

import com.example.internapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.Objects;

/**
 * Activity for handling password reset functionality.
 */
public class ForgotPasswordActivity extends AppCompatActivity {
    private final static String TAG = "ForgotPasswordActivity";
    private TextInputEditText edtResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;

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

        setContentView(R.layout.activity_forgot_password);

        ConstraintLayout constraintLayout = findViewById(R.id.parentConstraint); // Replace with your actual layout ID

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) constraintLayout.getLayoutParams();
        params.topMargin = getStatusBarHeight();

        Button btnReset = findViewById(R.id.btnReset);
        edtResetPassword = findViewById(R.id.text_reset_email);
        progressBar = findViewById(R.id.progress_bar_reset);

        SpeedDialView speedDialView = findViewById(R.id.speedDialView);
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), ForgotPasswordActivity.this);


        btnReset.setOnClickListener(v -> {
            String email = Objects.requireNonNull(edtResetPassword.getText()).toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ForgotPasswordActivity.this, "Please, enter your registered user email", Toast.LENGTH_LONG).show();
                edtResetPassword.setError("User email is required");
                edtResetPassword.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(ForgotPasswordActivity.this, "Please, enter valid registered user email", Toast.LENGTH_LONG).show();
                edtResetPassword.setError("Valid email is required");
                edtResetPassword.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                resetPassword(email);
            }
        });
    }

    /**
     * Resets password for the given email.
     *
     * @param email The email for which the password is to be reset.
     */
    private void resetPassword(String email) {
        authProfile = FirebaseAuth.getInstance();

        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ForgotPasswordActivity.this, "Please, check your inbox for password reset link", Toast.LENGTH_LONG).show();

                authProfile.signOut();
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthInvalidUserException e) {
                    edtResetPassword.setError("User doesn't exist or is no longer valid. Please register again");
                } catch (Exception e) {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                    Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            progressBar.setVisibility(View.GONE);
        });
    }
}