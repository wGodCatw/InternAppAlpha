package com.example.internapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leinardi.android.speeddial.SpeedDialView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * Activity for displaying student profile details.
 * Allows viewing student information and initiating video calls.
 */
public class StudentProfileActivity extends AppCompatActivity {

    // UI elements
    private TextInputEditText edt_fullName, edt_phone, edt_role, edt_dob, edt_uniCompany, edt_username;
    private ProgressBar progressBar;
    private TextInputLayout layout_uniCompany;
    private ImageView profilePic;
    private SwipeRefreshLayout swipeContainer;

    // Variables
    private String fullName, phone, role, dob, uniCompany, username;

    /**
     * Called when the activity is starting.
     * Sets the content view to the layout defined in activity_student_profile.xml.
     * Initializes UI elements and retrieves user information from Firebase Database.
     * Initializes the swipe-to-refresh functionality.
     * Initializes the floating action button for additional actions.
     *
     * @param savedInstanceState a Bundle containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);


        // Set up swipe-to-refresh functionality
        swipeToRefresh();

        // Initialize UI elements
        edt_fullName = findViewById(R.id.fullName);
        edt_phone = findViewById(R.id.phone);
        edt_role = findViewById(R.id.role);
        edt_dob = findViewById(R.id.dateOfBirth);
        progressBar = findViewById(R.id.progressBar);
        edt_uniCompany = findViewById(R.id.uni_company);
        layout_uniCompany = findViewById(R.id.layout_uni_company);
        profilePic = findViewById(R.id.profilePicture);
        edt_username = findViewById(R.id.username);
        ImageView btnCallStudent = findViewById(R.id.btnCallStudent);

        // Set click listener for calling the student
        btnCallStudent.setOnClickListener(v -> {
            String studentUsername = Objects.requireNonNull(edt_username.getText()).toString().substring(1);
            Intent call = new Intent(StudentProfileActivity.this, VideoCallActivity.class);
            call.putExtra("studentUsername", studentUsername);
            startActivity(call);
        });

        // Initialize floating action button
        SpeedDialView speedDialView = findViewById(R.id.speedDialView);
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), StudentProfileActivity.this);
        speedDialView.setOrientation(LinearLayout.VERTICAL);

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Retrieve and display user profile
        showUserProfile();
    }

    /**
     * Sets up the swipe-to-refresh functionality.
     * This method refreshes the activity when the user swipes down.
     */
    private void swipeToRefresh() {
        swipeContainer = findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(() -> {
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
            swipeContainer.setRefreshing(false);
        });
    }

    /**
     * Retrieves and displays the user profile from Firebase Database.
     */
    private void showUserProfile() {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students");
        Bundle extras = getIntent().getExtras();
        String userID = "";
        if (extras != null) {
            userID = extras.getString("UID");
        }

        assert userID != null;
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);

                    if (readUserDetails != null) {
                        username = readUserDetails.username;
                        fullName = readUserDetails.name;
                        dob = readUserDetails.doB;
                        role = readUserDetails.role;
                        phone = readUserDetails.mobile;
                        uniCompany = readUserDetails.university;

                        Uri uri = Uri.parse(readUserDetails.userPic);

                        // Load profile picture if available
                        if (!uri.toString().equals("none")) {
                            Picasso.get().load(uri).into(profilePic);
                        }

                        // Set user details
                        edt_username.setText("@" + username);
                        layout_uniCompany.setHint("University");
                        edt_uniCompany.setText(uniCompany);
                        edt_fullName.setText(fullName);
                        edt_dob.setText(dob);
                        edt_role.setText(role);
                        edt_phone.setText(phone);

                    } else {
                        Toast.makeText(StudentProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
