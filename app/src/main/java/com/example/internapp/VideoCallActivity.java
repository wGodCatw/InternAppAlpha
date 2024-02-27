package com.example.internapp;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
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


public class VideoCallActivity extends AppCompatActivity implements MainRepository.Listener {

    private ActivityVideoCallBinding views;
    private MainRepository mainRepository;
    private Boolean isSpeakerOn = false;
    private Boolean isCameraMuted = false;
    private Boolean isMicrophoneMuted = false;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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

            InputMethodManager imm = (InputMethodManager) VideoCallActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //Find the currently focused view, so we can grab the correct window token from it.
            View view = VideoCallActivity.this.getCurrentFocus();
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = new View(VideoCallActivity.this);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            //start a call request here
            mainRepository.sendCallRequest(views.targetUserNameEt.getText().toString(), () -> {
                Toast.makeText(this, "couldn't find the target", Toast.LENGTH_SHORT).show();
            });

        });
        mainRepository.initLocalView(views.localView);
        mainRepository.initRemoteView(views.remoteView);
        mainRepository.listener = this;


        mainRepository.subscribeForLatestEvent(data -> {
            if (data.getType() == DataModelType.StartCall) {
                runOnUiThread(() -> {
                    views.incomingNameTV.setText(data.getSender() + " is Calling you");

                    views.incomingCallLayout.setVisibility(View.VISIBLE);
                    views.acceptButton.setOnClickListener(v -> {
                        //star the call here
                        mainRepository.startCall(data.getSender());
                        views.incomingCallLayout.setVisibility(View.GONE);
                    });
                    views.rejectButton.setOnClickListener(v -> {
                        views.incomingCallLayout.setVisibility(View.GONE);
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