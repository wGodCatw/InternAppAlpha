package com.example.internapp.MainDir;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.internapp.R;
import com.example.internapp.VideoCall.MainRepository;
import com.example.internapp.VideoCall.VideoCallActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leinardi.android.speeddial.SpeedDialView;
import com.permissionx.guolindev.PermissionX;

import java.util.Calendar;
import java.util.Objects;

/**
 * The UserProfileActivity class is an AppCompatActivity that displays and allows editing of user profile information.
 * It fetches and displays user data from Firebase Realtime Database, and provides functionality for updating user profile details.
 */
public class UserProfileActivity extends AppCompatActivity {



    /**
     * BroadcastReceiver to monitor network state changes and update the WiFi icon accordingly.
     */
    BroadcastReceiver broadcastReceiverWifi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    boolean hasWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                    boolean hasMobile = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);

                    if (!hasWifi && !hasMobile) {
                        // No WiFi and no mobile network available
                        wifiState.setImageResource(R.drawable.ic_wifi_disabled);
                    } else {
                        // Either WiFi or mobile network is available
                        wifiState.setImageResource(R.drawable.ic_wifi_enabled);
                    }
                } else {
                    // No active network
                    wifiState.setImageResource(R.drawable.ic_wifi_disabled);
                }
            }
        }
    };


    private TextInputEditText edt_fullName, edt_email, edt_phone, edt_role, edt_dob, edt_uniCompany, edt_faculty, edt_username;
    private ProgressBar progressBar;
    private DatePickerDialog picker;
    private TextInputLayout layout_faculty;
    private TextInputLayout layout_uniCompany;
    private ImageView profilePic, wifiState;
    private String fullName, email, phone, role, dob, uniCompany, faculty, username;
    private String dateBirth;
    private SwipeRefreshLayout swipeContainer;
    private MainRepository mainRepository;

    public static String ROLE = "";

    /**
     * Creates a notification channel for incoming call notifications.
     */
    private void createNotificationChannel() {
        CharSequence name = "Call Channel";
        String description = "Channel for incoming calls notifications";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("1", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

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

        setContentView(R.layout.activity_user_profile);
        mainRepository = MainRepository.getInstance();


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered users/Students/" + firebaseUser.getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ROLE = "STUDENT";
                } else {
                    ROLE = "HR";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ConstraintLayout constraintLayout = findViewById(R.id.parentConstraint); // Replace with your actual layout ID

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) constraintLayout.getLayoutParams();
        params.topMargin = getStatusBarHeight();


        FirebaseAuth authProfile = FirebaseAuth.getInstance();

        createNotificationChannel();

        // Start the BackgroundCheck service if it's not running
        Intent backgroundCheck = new Intent(this, BackgroundCheck.class);
        if (BackgroundCheck.isServiceRunning()) {
            startService(backgroundCheck);
        }

        swipeToRefresh();

        edt_fullName = findViewById(R.id.fullName);
        edt_email = findViewById(R.id.email);
        edt_phone = findViewById(R.id.phone);
        edt_role = findViewById(R.id.role);
        edt_dob = findViewById(R.id.dateOfBirth);
        progressBar = findViewById(R.id.progressBar);
        wifiState = findViewById(R.id.wifi_state);
        edt_uniCompany = findViewById(R.id.uni_company);
        edt_faculty = findViewById(R.id.faculty);
        layout_faculty = findViewById(R.id.layout_faculty);
        layout_uniCompany = findViewById(R.id.layout_uni_company);
        profilePic = findViewById(R.id.profilePicture);
        edt_username = findViewById(R.id.username);

        TextInputLayout layout_fullName = findViewById(R.id.layout_fullName);
        TextInputLayout layout_dateOfBirth = findViewById(R.id.layout_dateOfBirth);
        Button callBtn = findViewById(R.id.callBtn);

        // Request necessary permissions for camera, audio, and notifications
        PermissionX.init(this).permissions(android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.POST_NOTIFICATIONS).request(((allGranted, grantedList, deniedList) -> {
            if (!allGranted) {
                Toast.makeText(this, "You won't be able to make calls", Toast.LENGTH_SHORT).show();
            }
        }));

        /*
          Starts the VideoCallActivity when the callBtn is clicked.
          Retrieves the user's username from the Firebase database and logs in using the MainRepository before starting the activity.
         */
        callBtn.setOnClickListener(v -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/HRs");
            assert firebaseUser != null;
            reference.orderByKey().equalTo(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String username = Objects.requireNonNull(snap.child("username").getValue()).toString();
                            mainRepository.login(username, getApplicationContext(), () -> startActivity(new Intent(UserProfileActivity.this, VideoCallActivity.class)));
                        }

                    } else {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
                        reference.orderByKey().equalTo(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                        String username = Objects.requireNonNull(snap.child("username").getValue()).toString();
                                        mainRepository.login(username, getApplicationContext(), () -> startActivity(new Intent(UserProfileActivity.this, VideoCallActivity.class)));
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        /*
          Starts the UploadUserPicActivity when the profilePic is clicked, allowing the user to upload a new profile picture.
         */
        profilePic.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, UploadUserPicActivity.class);
            startActivity(intent);
        });


        SpeedDialView speedDialView = findViewById(R.id.speedDialView);
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), UserProfileActivity.this);
        speedDialView.setOrientation(LinearLayout.VERTICAL);


        if (firebaseUser == null) {
            Toast.makeText(UserProfileActivity.this, "Something went wrong, user details are not available", Toast.LENGTH_LONG).show();
        } else {
            checkEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

        /*
          Updates the user's full name in Firebase Authentication and Firebase Realtime Database.
          @param v The View that was clicked.
         */
        layout_fullName.setEndIconOnClickListener(v -> {
            String textFullName = Objects.requireNonNull(edt_fullName.getText()).toString();
            if (TextUtils.isEmpty(textFullName)) {
                Toast.makeText(UserProfileActivity.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                edt_fullName.setError("Full name is required");
                edt_fullName.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                assert firebaseUser != null;
                firebaseUser.updateProfile(profileChangeRequest).addOnCompleteListener(task -> Toast.makeText(UserProfileActivity.this, "Name updated, it is now " + firebaseUser.getDisplayName(), Toast.LENGTH_LONG).show());
                DatabaseReference referenceProfile;
                if (TextUtils.equals(Objects.requireNonNull(edt_role.getText()).toString(), "HR Specialist")) {
                    referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/HRs/" + firebaseUser.getUid() + "/name");
                } else {
                    referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students/" + firebaseUser.getUid() + "/name");
                }
                progressBar.setVisibility(View.VISIBLE);

                referenceProfile.setValue(textFullName).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(Objects.requireNonNull(edt_fullName.getText()).toString()).build();
                        firebaseUser.updateProfile(userProfileChangeRequest);
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                progressBar.setVisibility(View.GONE);
            }
        });

        /*
          Displays a date picker dialog when the edt_dob EditText is clicked, allowing the user to select their date of birth.
         */
        edt_dob.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            picker = new DatePickerDialog(UserProfileActivity.this, (view, year1, month1, dayOfMonth) -> edt_dob.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);
            picker.show();
        });

        /*
          Updates the user's date of birth in the Firebase Realtime Database.
          @param v The View that was clicked.
         */
        layout_dateOfBirth.setEndIconOnClickListener(v -> {
            if (TextUtils.isEmpty(Objects.requireNonNull(edt_dob.getText()).toString())) {
                Toast.makeText(UserProfileActivity.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                edt_dob.setError("Date of birth is required");
                edt_dob.requestFocus();
            } else {
                dateBirth = edt_dob.getText().toString();
                DatabaseReference referenceProfile;
                if (TextUtils.equals(Objects.requireNonNull(edt_role.getText()).toString(), "HR Specialist")) {
                    assert firebaseUser != null;
                    referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/HRs/" + firebaseUser.getUid() + "/doB");
                } else {
                    assert firebaseUser != null;
                    referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students/" + firebaseUser.getUid() + "/doB");
                }
                progressBar.setVisibility(View.VISIBLE);

                referenceProfile.setValue(dateBirth).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(Objects.requireNonNull(edt_fullName.getText()).toString()).build();
                        firebaseUser.updateProfile(userProfileChangeRequest);
                        Toast.makeText(UserProfileActivity.this, "Date of birth updated", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }
        });

        /*
          Starts the UpdateEmailActivity when the edt_email EditText is clicked, allowing the user to update their email address.
         */
        edt_email.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Sets up a SwipeRefreshLayout to allow refreshing the activity by swiping down.
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
     * Checks if the user's email is verified, and displays an alert dialog prompting the user to verify their email if it is not verified.
     * @param firebaseUser The current FirebaseUser instance.
     */
    private void checkEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()) {
            showAlertDialog();
        }
    }

    /**
     * Displays an alert dialog prompting the user to verify their email.
     */
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You can not login without email verification next time");

        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

/**
 * Retrieves and displays the user's profile information from the Firebase Realtime Database.
 * @param firebaseUser The current FirebaseUser instance.
 */
private void showUserProfile(FirebaseUser firebaseUser) {
    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/HRs");
    String userID = firebaseUser.getUid();
    referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);

                if (readUserDetails != null) {
                    username = readUserDetails.username;
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    dob = readUserDetails.doB;
                    role = readUserDetails.role;
                    phone = readUserDetails.mobile;
                    uniCompany = readUserDetails.company;

                    // Load user's profile picture from Firebase Storage
                    Uri uri = firebaseUser.getPhotoUrl();
                    if(!(uri == null) &&!uri.toString().equals("none")){
                        RequestBuilder<Drawable> requestBuilder = Glide.with(UserProfileActivity.this).asDrawable().sizeMultiplier(0.1f);
                        Glide.with(UserProfileActivity.this).load(uri).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(requestBuilder).fitCenter().centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(profilePic);
                    }

                    edt_username.setText("@" + username);
                    layout_uniCompany.setHint("Company");
                    edt_uniCompany.setText(uniCompany);
                    edt_fullName.setText(fullName);
                    edt_email.setText(email);
                    edt_dob.setText(dob);
                    edt_role.setText(role);
                    edt_phone.setText(phone);

                } else {
                    Toast.makeText(UserProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
                    username = readUserDetails.username;
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    dob = readUserDetails.doB;
                    role = readUserDetails.role;
                    phone = readUserDetails.mobile;
                    uniCompany = readUserDetails.university;
                    faculty = readUserDetails.faculty;

                    // Load user's profile picture from Firebase Storage
                    Uri uri = firebaseUser.getPhotoUrl();
                    RequestBuilder<Drawable> requestBuilder = Glide.with(UserProfileActivity.this).asDrawable().sizeMultiplier(0.1f);
                    Glide.with(UserProfileActivity.this).load(uri).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(requestBuilder).fitCenter().centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(profilePic);

                    edt_username.setText("@" + username);
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

    /**
     * Registers the broadcastReceiverWifi to listen for WiFi state changes when the activity starts.
     */
    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(broadcastReceiverWifi, intentFilter);
    }

    /**
     * Unregisters the broadcastReceiverWifi when the activity stops.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the BroadcastReceiver to listen for changes in network connectivity
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiverWifi, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the BroadcastReceiver when the activity is not in the foreground
        unregisterReceiver(broadcastReceiverWifi);
    }

}