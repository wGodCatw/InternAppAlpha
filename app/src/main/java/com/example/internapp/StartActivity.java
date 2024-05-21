package com.example.internapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity that serves as the entry point of the application.
 * This activity displays a splash screen for a certain duration and then navigates to the login screen.
 */
public class StartActivity extends AppCompatActivity {

    /**
     * Called when the activity is starting.
     * Sets the content view to the layout defined in activity_start.xml.
     * Initiates a delay of 2000 milliseconds before navigating to the LoginActivity.
     * @param savedInstanceState a Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        
        if(firebaseUser != null){
            // Start the BackgroundCheck service
            Intent serviceIntent = new Intent(this, BackgroundCheck.class);
            startService(serviceIntent);
        }


        // Create a delayed runnable to start LoginActivity after 2000 milliseconds (2 seconds)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Create an intent to navigate to LoginActivity
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            // Start the LoginActivity
            startActivity(intent);
            // Finish this activity to prevent going back to it when pressing back button
            finish();
        }, 2000);
    }
}
