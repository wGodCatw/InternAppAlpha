package com.example.internapp.MainDir;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;

import com.example.internapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

/**
 * The DeleteUserActivity class allows users to delete their account and associated data after re-authenticating themselves.
 */
public class DeleteUserActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private String password;
    private Button btnAuth, btnDeleteAccount;
    private TextInputEditText edtPassword;
    private TextView titleAuth;
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


        setContentView(R.layout.activity_delete_user);

        ConstraintLayout constraintLayout = findViewById(R.id.parentConstraint); // Replace with your actual layout ID

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) constraintLayout.getLayoutParams();
        params.topMargin = getStatusBarHeight();

        progressBar = findViewById(R.id.progressBar);
        btnAuth = findViewById(R.id.deleteAccountAuth);
        btnDeleteAccount = findViewById(R.id.deleteAccountBtn);
        edtPassword = findViewById(R.id.password);
        titleAuth = findViewById(R.id.titleDeleteAccountAuth);

        btnDeleteAccount.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals("")) {
            Toast.makeText(DeleteUserActivity.this, "Something went wrong. User details are not available", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DeleteUserActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }
    }

    /**
     * Prompts the user to re-authenticate themselves by entering their current password.
     * If the authentication is successful, it enables the "Delete Account" button.
     *
     * @param firebaseUser The current FirebaseUser instance.
     */
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        btnAuth.setOnClickListener(v -> {
            password = Objects.requireNonNull(edtPassword.getText()).toString();
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(DeleteUserActivity.this, "Please enter your current password", Toast.LENGTH_LONG).show();
                edtPassword.setError("Password is required");
                edtPassword.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                // Create an AuthCredential object with the user's email and current password
                AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()), password);

                // Re-authenticate the user with their current password
                firebaseUser.reauthenticate(authCredential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        edtPassword.setEnabled(false);
                        btnAuth.setEnabled(false);
                        btnDeleteAccount.setEnabled(true);

                        titleAuth.setText("You are authenticated. You can delete your account now");
                        Toast.makeText(DeleteUserActivity.this, "You can delete your account. Be careful, this action can not be undone", Toast.LENGTH_LONG).show();

                        // Set an OnClickListener for the "Delete Account" button
                        btnDeleteAccount.setOnClickListener(v1 -> showAlertDialog());
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(DeleteUserActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
    }

    /**
     * Displays an AlertDialog to confirm if the user wants to delete their account and associated data.
     */
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteUserActivity.this);
        builder.setTitle("Delete account and related data");
        builder.setMessage("Do you really want to delete your account? This action is irreversible");

        // When the user confirms, call the deleteUserData method
        builder.setPositiveButton("Yes, delete the account", (dialog, which) -> deleteUserData(firebaseUser));

        // When the user cancels, navigate back to the SettingsActivity
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            Intent intent = new Intent(DeleteUserActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        });

        AlertDialog alertDialog = builder.create();

        // Change the color of the positive button (Delete Account)
        alertDialog.setOnShowListener(dialog -> alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red, getTheme())));
        alertDialog.show();
    }

    /**
     * Deletes the user's account from Firebase Authentication.
     */
    private void deleteUser() {
        firebaseUser.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                authProfile.signOut();
                Toast.makeText(DeleteUserActivity.this, "The account has been deleted", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(DeleteUserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (Exception e) {
                    Toast.makeText(DeleteUserActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    /**
     * Deletes the user's data from Firebase Realtime Database and Firebase Storage.
     *
     * @param firebaseUser The current FirebaseUser instance.
     */
    private void deleteUserData(FirebaseUser firebaseUser) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(Objects.requireNonNull(firebaseUser.getPhotoUrl()).toString());

        // Delete the user's profile picture from Firebase Storage
        if (firebaseUser.getPhotoUrl() != null) {
            storageReference.delete().addOnSuccessListener(unused -> Log.d("Data deletion", "Profile picture deleted")).addOnFailureListener(e -> {
                Log.e("Data deletion", Objects.requireNonNull(e.getMessage()));
                Toast.makeText(DeleteUserActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }

        // Delete the user's data from the "HRs" node in Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered users/HRs");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(unused -> {
            deleteUser();
            Log.d("Data deletion", "User data deleted");
        });

        // Delete the user's data from the "Students" node in Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(unused -> Log.d("Data deletion", "User data deleted"));
    }
}