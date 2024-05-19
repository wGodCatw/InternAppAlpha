package com.example.internapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.leinardi.android.speeddial.SpeedDialView;

/**
 * Activity for managing user settings.
 */
public class SettingsActivity extends AppCompatActivity {
    FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        authProfile = FirebaseAuth.getInstance();

        // Initialize speed dial menu
        SpeedDialView speedDialView = findViewById(R.id.speedDialView);
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), SettingsActivity.this);

        // Button initialization
        Button logOut = findViewById(R.id.log_out);
        Button updateProfile = findViewById(R.id.update_profile);
        Button updatePicture = findViewById(R.id.update_picture);
        Button changePassword = findViewById(R.id.change_password);
        Button deleteProfile = findViewById(R.id.delete_profile);
        Button updateEmail = findViewById(R.id.update_email);

        // OnClickListener for updating user email
        updateEmail.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
        });

        // OnClickListener for deleting the user profile
        deleteProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, DeleteUserActivity.class);
            startActivity(intent);
        });

        // OnClickListener for logging out the user
        logOut.setOnClickListener(v -> {
            authProfile.signOut();

            Intent backgroundCheck = new Intent(this, BackgroundCheck.class);
            stopService(backgroundCheck);

            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // OnClickListener for updating user profile picture
        updatePicture.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, UploadUserPicActivity.class);
            startActivity(intent);
        });

        // OnClickListener for updating user profile
        updateProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        // OnClickListener for changing user password
        changePassword.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
    }
}
