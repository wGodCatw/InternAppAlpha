package com.example.internapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leinardi.android.speeddial.SpeedDialView;

public class UserProfileActivity extends AppCompatActivity {

    BroadcastReceiver broadcastReceiverWifi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                wifiState.setImageResource(R.drawable.ic_wifi_enabled);
            } else {
                wifiState.setImageResource(R.drawable.ic_wifi_disabled);
            }
        }
    };
    private TextInputEditText edt_fullName, edt_email, edt_phone, edt_role, edt_dob, edt_uniCompany, edt_faculty;
    private ProgressBar progressBar;
    private SpeedDialView speedDialView;
    private TextInputLayout layout_faculty, layout_uniCompany;
    private ImageView profilePic, wifiState, refresh;
    private String fullName, email, phone, role, dob, uniCompany, faculty;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        edt_fullName = findViewById(R.id.fullName);
        edt_email = findViewById(R.id.email);
        edt_phone = findViewById(R.id.phone);
        edt_role = findViewById(R.id.role);
        edt_dob = findViewById(R.id.dateOfBirth);
        progressBar = findViewById(R.id.progressBar);
        refresh = findViewById(R.id.refresh);
        wifiState = findViewById(R.id.wifi_state);
        edt_uniCompany = findViewById(R.id.uni_company);
        edt_faculty = findViewById(R.id.faculty);
        layout_faculty = findViewById(R.id.layout_faculty);
        layout_uniCompany = findViewById(R.id.layout_uni_company);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(getIntent());
                finish();
                overridePendingTransition(0, 0);
            }
        });




        speedDialView = findViewById(R.id.speedDialView);
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), UserProfileActivity.this);
        speedDialView.setOrientation(LinearLayout.HORIZONTAL);


        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(UserProfileActivity.this, "Something went wrong, user details are not available", Toast.LENGTH_LONG).show();
        } else {
            checkEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

    }

    private void checkEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()) {
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You can not login without email verification next time");

        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/HRs");
        String userID = firebaseUser.getUid();

        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);

                    if (readUserDetails != null) {
                        fullName = firebaseUser.getDisplayName();
                        email = firebaseUser.getEmail();
                        dob = readUserDetails.doB;
                        role = readUserDetails.role;
                        phone = readUserDetails.mobile;
                        uniCompany = readUserDetails.company;

                        layout_uniCompany.setHint("Company");
                        edt_uniCompany.setText(uniCompany);
                        edt_fullName.setText(fullName);
                        edt_email.setText(email);
                        edt_dob.setText(dob);
                        edt_role.setText(role);
                        edt_phone.setText(phone);
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);

                    if (readUserDetails != null) {
                        fullName = firebaseUser.getDisplayName();
                        email = firebaseUser.getEmail();
                        dob = readUserDetails.doB;
                        role = readUserDetails.role;
                        phone = readUserDetails.mobile;
                        uniCompany = readUserDetails.university;
                        faculty = readUserDetails.faculty;

                        edt_uniCompany.setText(uniCompany);
                        layout_uniCompany.setHint("University");
                        layout_faculty.setVisibility(View.VISIBLE);
                        edt_faculty.setText(faculty);
                        edt_fullName.setText(fullName);
                        edt_email.setText(email);
                        edt_dob.setText(dob);
                        edt_role.setText(role);
                        edt_phone.setText(phone);
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(broadcastReceiverWifi, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiverWifi);
    }
}