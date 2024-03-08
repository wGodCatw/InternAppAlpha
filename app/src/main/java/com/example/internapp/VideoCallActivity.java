package com.example.internapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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


public class VideoCallActivity extends AppCompatActivity implements MainRepository.Listener {

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private ActivityVideoCallBinding views;
    private MainRepository mainRepository;
    private Boolean isSpeakerOn = false;
    private Boolean isCameraMuted = false;
    private Boolean isMicrophoneMuted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityVideoCallBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        init();
    }

    private void init() {
        mainRepository = MainRepository.getInstance();

        views.callBtn.setOnClickListener(v -> {

            //Hide keyboard
            InputMethodManager imm = (InputMethodManager) VideoCallActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = VideoCallActivity.this.getCurrentFocus();
            if (view == null) {
                view = new View(VideoCallActivity.this);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            //start a call request here
            mainRepository.sendCallRequest(views.targetUserNameEt.getText().toString().trim(), () -> {
                Toast.makeText(this, "couldn't find the target", Toast.LENGTH_SHORT).show();
            });

        });

        mainRepository.initLocalView(views.localView);
        mainRepository.initRemoteView(views.remoteView);
        mainRepository.listener = this;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String studentUsername = extras.getString("studentUsername");
            //log student username
            views.targetUserNameEt.setText(studentUsername);

            views.callBtn.performClick();

        }

        mainRepository.subscribeForLatestEvent(data -> {
            if (data.getType() == DataModelType.StartCall) {
                runOnUiThread(() -> {
                    views.incomingNameTV.setText(data.getSender() + " is Calling you");

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
                    reference.orderByChild("username").equalTo(data.getSender()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    Uri photo = Uri.parse(snap.child("userPic").getValue().toString());

                                    Picasso.get().load(photo).into(views.callerPic);
                                }

                            } else{
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/HRs");
                                reference.orderByChild("username").equalTo(data.getSender()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot snap : snapshot.getChildren()) {
                                                Uri photo = Uri.parse(snap.child("userPic").getValue().toString());

                                                Picasso.get().load(photo).into(views.callerPic);
                                            }

                                        } else{
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

                    views.incomingCallLayout.setVisibility(View.VISIBLE);
                    views.acceptButton.setOnClickListener(v -> {
                        // Stop background service
                        Intent backgroundCheckIntent = new Intent(this, BackgroundCheck.class);

                        stopService(backgroundCheckIntent);

                        // Start the call here
                        mainRepository.startCall(data.getSender());
                        views.incomingCallLayout.setVisibility(View.GONE);
                    });

                    views.rejectButton.setOnClickListener(v -> {

                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
                        dbRef.child(data.getTarget()).setValue("");
                        dbRef.child(data.getSender()).setValue("");
                        views.incomingCallLayout.setVisibility(View.GONE);
                        finish();
                    });
                });
            }
        });

        views.switchCameraButton.setOnClickListener(v -> {
            mainRepository.switchCamera();
        });

        views.switchSpeakerButton.setOnClickListener(v -> {
            if (isSpeakerOn) {
                views.switchSpeakerButton.setImageResource(R.drawable.ic_speaker_on);
            } else {
                views.switchSpeakerButton.setImageResource(R.drawable.ic_speaker_off);
            }
            mainRepository.toggleSpeaker(isSpeakerOn);
            isSpeakerOn = !isSpeakerOn;
        });

        views.micButton.setOnClickListener(v -> {
            if (isMicrophoneMuted) {
                views.micButton.setImageResource(R.drawable.ic_mic);
            } else {
                views.micButton.setImageResource(R.drawable.ic_mic_off);
            }
            mainRepository.toggleAudio(isMicrophoneMuted);
            isMicrophoneMuted = !isMicrophoneMuted;
        });

        views.videoButton.setOnClickListener(v -> {
            if (isCameraMuted) {
                views.videoButton.setImageResource(R.drawable.ic_video);
            } else {
                views.videoButton.setImageResource(R.drawable.ic_video_off);
            }
            mainRepository.toggleVideo(isCameraMuted);
            isCameraMuted = !isCameraMuted;
        });

        views.endCallButton.setOnClickListener(v -> {
            Intent backgroundCheck = new Intent(this, BackgroundCheck.class);
            if (!BackgroundCheck.isServiceRunning()) {
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
            views.incomingCallLayout.setVisibility(View.GONE);
            views.whoToCallLayout.setVisibility(View.GONE);
            views.callLayout.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void webrtcClosed() {
        runOnUiThread(this::finish);
    }
}