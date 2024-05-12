package com.example.internapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.internapp.databinding.ActivityVideoCallBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * Activity for handling video calls.
 */
public class VideoCallActivity extends AppCompatActivity implements MainRepository.Listener {

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private ActivityVideoCallBinding views;
    private MainRepository mainRepository;
    private Boolean isSpeakerOn = false;

    private ProgressBar progressBar;

    private Handler callTimeoutHandler = new Handler(Looper.getMainLooper());
    private Runnable callTimeoutRunnable;
    private DataModel currentDataModel;

    private Boolean isCameraMuted = false;
    private Boolean isMicrophoneMuted = false;

    // Added a flag to track if the call is connected
    private boolean isCallConnected = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (callTimeoutRunnable != null) {
            callTimeoutHandler.removeCallbacks(callTimeoutRunnable);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (currentDataModel != null) {
            // Logic to delete the DataModel, e.g., remove from database
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
            dbRef.child(currentDataModel.getTarget()).removeValue();
            dbRef.child(currentDataModel.getSender()).removeValue();
        }
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
        if (callTimeoutRunnable != null) {
            callTimeoutHandler.removeCallbacks(callTimeoutRunnable);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityVideoCallBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        progressBar = findViewById(R.id.progress_bar);

        init();
    }

    /**
     * Initializes the activity.
     */
    private void init() {
        mainRepository = MainRepository.getInstance();

        views.remoteView.setOnClickListener(v -> {
            if (views.controls.getVisibility() == View.VISIBLE) {
                views.controls.setVisibility(View.GONE);
            } else {
                views.controls.setVisibility(View.VISIBLE);
            }
        });

        views.callBtn.setOnClickListener(v -> {
            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) VideoCallActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = VideoCallActivity.this.getCurrentFocus();
            if (view == null) {
                view = new View(VideoCallActivity.this);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(VideoCallActivity.this, "Calling your target", Toast.LENGTH_LONG).show();

            // Start a call request with timeout
            String targetUser = views.targetUserNameEt.getText().toString().trim();
            DataModel callDataModel = new DataModel(targetUser, firebaseUser.getUid(), null, DataModelType.StartCall);
            currentDataModel = callDataModel; // Save the current DataModel
            mainRepository.sendCallRequest(targetUser, () -> {
                Toast.makeText(this, "couldn't find the target", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            });

            // Setup the timeout for the call request
            callTimeoutRunnable = () -> {
                if (progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(VideoCallActivity.this, "Call did not succeed", Toast.LENGTH_LONG).show();
                }
            };
            callTimeoutHandler.postDelayed(callTimeoutRunnable, 60000); // 1 minute timeout
        });

        progressBar.setVisibility(View.GONE);

        // Initialization of local and remote views
        mainRepository.initLocalView(views.localView);
        mainRepository.initRemoteView(views.remoteView);
        mainRepository.listener = this;

        // Handling incoming call event
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String studentUsername = extras.getString("studentUsername");
            views.targetUserNameEt.setText(studentUsername);
            views.callBtn.performClick();
        }

        // Subscribing for latest event
        mainRepository.subscribeForLatestEvent(data -> {
            if (data.getType() == DataModelType.StartCall) {
                currentDataModel = data; // Save the current DataModel
                runOnUiThread(() -> {
                    // Setting up incoming call UI
                    views.incomingNameTV.setText(data.getSender());
                    // Loading caller's photo
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
                    reference.orderByChild("username").equalTo(data.getSender()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    Uri photo = Uri.parse(snap.child("userPic").getValue().toString());
                                    if (!(photo == null) && !photo.toString().equals("none")) {
                                        Picasso.get().load(photo).into(views.callerPic);
                                    }
                                }
                            } else {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/HRs");
                                reference.orderByChild("username").equalTo(data.getSender()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot snap : snapshot.getChildren()) {
                                                Uri photo = Uri.parse(snap.child("userPic").getValue().toString());
                                                if (!(photo == null) && !photo.toString().equals("none")) {
                                                    Picasso.get().load(photo).into(views.callerPic);
                                                }
                                            }
                                        } else {
                                            Log.e("VideoCallActivity", "onDataChange: user not found");
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

                    // Handling accept and reject call actions
                    views.incomingCallLayout.setVisibility(View.VISIBLE);
                    views.acceptButton.setOnClickListener(v -> {
                        // Stop background service
                        Intent backgroundCheckIntent = new Intent(this, BackgroundCheck.class);
                        stopService(backgroundCheckIntent);

                        // Show progress bar while waiting for the connection to establish
                        progressBar.setVisibility(View.VISIBLE);

                        // Start the call
                        mainRepository.startCall(data.getSender());
                    });

                    views.rejectButton.setOnClickListener(v -> {
                        // Remove call entries from database and finish activity
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
                        dbRef.child(data.getTarget()).setValue("");
                        dbRef.child(data.getSender()).setValue("");
                        views.incomingCallLayout.setVisibility(View.GONE);
                        finish();
                    });
                });
            }
        });

        // Switching camera
        views.switchCameraButton.setOnClickListener(v -> {
            mainRepository.switchCamera();
        });

        // Toggling speaker
        views.switchSpeakerButton.setOnClickListener(v -> {
            if (isSpeakerOn) {
                views.switchSpeakerButton.setImageResource(R.drawable.ic_speaker_on);
            } else {
                views.switchSpeakerButton.setImageResource(R.drawable.ic_speaker_off);
            }
            mainRepository.toggleSpeaker(isSpeakerOn);
            isSpeakerOn = !isSpeakerOn;
        });

        // Toggling microphone
        views.micButton.setOnClickListener(v -> {
            if (isMicrophoneMuted) {
                views.micButton.setImageResource(R.drawable.ic_mic);
            } else {
                views.micButton.setImageResource(R.drawable.ic_mic_off);
            }
            mainRepository.toggleAudio(isMicrophoneMuted);
            isMicrophoneMuted = !isMicrophoneMuted;
        });

        // Toggling video
        views.videoButton.setOnClickListener(v -> {
            if (isCameraMuted) {
                views.videoButton.setImageResource(R.drawable.ic_video);
            } else {
                views.videoButton.setImageResource(R.drawable.ic_video_off);
            }
            mainRepository.toggleVideo(isCameraMuted);
            isCameraMuted = !isCameraMuted;
        });

        // Ending the call
        views.endCallButton.setOnClickListener(v -> {
            Intent backgroundCheck = new Intent(this, BackgroundCheck.class);
            if (BackgroundCheck.isServiceRunning()) {
                startService(backgroundCheck);
            }

            mainRepository.endCall();

            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/HRs");
            reference.orderByKey().equalTo(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String username = snap.child("username").getValue().toString();
                            dbRef.child(username).setValue("");
                        }
                    } else {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
                        reference.orderByKey().equalTo(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                        String username = snap.child("username").getValue().toString();
                                        dbRef.child(username).setValue("");
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

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();

            finish();
        });
    }

    @Override
    public void webrtcConnected() {
        runOnUiThread(() -> {
            if (callTimeoutRunnable != null) {
                callTimeoutHandler.removeCallbacks(callTimeoutRunnable);
            }
            views.incomingCallLayout.setVisibility(View.GONE);
            views.whoToCallLayout.setVisibility(View.GONE);
            views.callLayout.setVisibility(View.VISIBLE);

            // Hide the progress bar when the call is connected
            progressBar.setVisibility(View.GONE);

            // Set the flag to indicate that the call is connected
            isCallConnected = true;
        });
    }

    @Override
    public void webrtcClosed() {
        runOnUiThread(() -> {
            // If the call is disconnected due to an exception, try to re-establish the connection
            if (!isCallConnected) {
                Log.e("VideoCallActivity", "WebRTC connection failed. Attempting to re-establish.");

                // Reset the views and try to re-establish the connection
                views.localView.release();
                views.remoteView.release();
                mainRepository.initLocalView(views.localView);
                mainRepository.initRemoteView(views.remoteView);
                mainRepository.startCall(currentDataModel.getSender());
            } else {
                // If the call was already connected and then disconnected, finish the activity
                finish();
            }
        });
    }
}