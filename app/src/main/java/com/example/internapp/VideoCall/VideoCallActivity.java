package com.example.internapp.VideoCall;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.internapp.MainDir.BackgroundCheck;
import com.example.internapp.R;
import com.example.internapp.databinding.ActivityVideoCallBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * Activity for handling video calls.
 */
public class VideoCallActivity extends AppCompatActivity implements MainRepository.Listener {

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private ActivityVideoCallBinding views;
    private MainRepository mainRepository;
    private Boolean isSpeakerOn = false;
    private Boolean isCameraMuted = false;
    private Boolean isMicrophoneMuted = false;

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0; // Default value if not found
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        views = ActivityVideoCallBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        RelativeLayout relativeLayout = findViewById(R.id.parentRelative); // Replace with your actual layout ID

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) relativeLayout.getLayoutParams();
        params.topMargin = getStatusBarHeight();

        init();
    }

    /**
     * Initializes the activity.
     */
    private void init() {
        mainRepository = MainRepository.getInstance();

        // Handling incoming call event
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String studentUsername = extras.getString("studentUsername");
            views.targetUserName.setText(studentUsername);
            views.callBtn.performClick();
        }

        views.remoteView.setOnClickListener(v -> {
            if (views.controls.getVisibility() == View.VISIBLE) {
                views.controls.setVisibility(View.GONE);
            } else {
                views.controls.setVisibility(View.VISIBLE);
            }
        });

        views.callBtn.setOnClickListener(v -> {

            views.whoToCallLayout.setVisibility(View.GONE);
            views.outcomingCallLayout.setVisibility(View.VISIBLE);

            // Hide keyboard
            InputMethodManager imm = (InputMethodManager) VideoCallActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = VideoCallActivity.this.getCurrentFocus();
            if (view == null) {
                view = new View(VideoCallActivity.this);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            views.outcomingNameTV.setText(views.targetUserName.getText().toString().trim());
            // Loading caller's photo
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
            reference.orderByChild("username").equalTo(views.targetUserName.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Uri photo = Uri.parse(snap.child("userPic").getValue().toString());
                            if (!(photo == null) && !photo.toString().equals("none")) {
                                RequestBuilder<Drawable> requestBuilder = Glide.with(getApplicationContext()).asDrawable().sizeMultiplier(0.1f);
                                Glide.with(getApplicationContext()).load(photo).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(requestBuilder).fitCenter().centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(views.receiverPic);
                            }

                        }
                    } else {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/HRs");
                        reference.orderByChild("username").equalTo(views.targetUserName.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                        Uri photo = Uri.parse(snap.child("userPic").getValue().toString());
                                        if (!(photo == null) && !photo.toString().equals("none")) {
                                            RequestBuilder<Drawable> requestBuilder = Glide.with(getApplicationContext()).asDrawable().sizeMultiplier(0.1f);
                                            Glide.with(getApplicationContext()).load(photo).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(requestBuilder).fitCenter().centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(views.receiverPic);
                                        }
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




            // Start a call request
            mainRepository.sendCallRequest(views.targetUserName.getText().toString().trim(), () -> {
                Toast.makeText(this, "couldn't find the target", Toast.LENGTH_SHORT).show();
            });


            //wait for 5 seconds
            final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
            dbRef.orderByChild(mainRepository.currentUsername).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for (DataSnapshot snap : snapshot.getChildren()){
                            String value = Objects.requireNonNull(snap.getValue()).toString();
                            if(value.equals("declined")){
                                views.whoToCallLayout.setVisibility(View.VISIBLE);
                                views.outcomingCallLayout.setVisibility(View.GONE);
                                Toast.makeText(VideoCallActivity.this, "The call did not succeed", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });


        views.localView.setBackground(getDrawable(R.drawable.rounded_corners));
        views.localView.setClipToOutline(true);

        // Initialization of local and remote views
        mainRepository.initLocalView(views.localView);
        mainRepository.initRemoteView(views.remoteView);
        mainRepository.listener = this;

        final DatabaseReference endCallRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        endCallRef.orderByChild(mainRepository.currentUsername).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot snap : snapshot.getChildren()){
                        String value = Objects.requireNonNull(snap.getValue()).toString();
                        if(value.equals("ended")){
                            Intent backgroundCheck = new Intent(VideoCallActivity.this, BackgroundCheck.class);
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
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Subscribing for latest event
        mainRepository.subscribeForLatestEvent(data -> {
            if (data.getType() == DataModelType.StartCall) {
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
                                        RequestBuilder<Drawable> requestBuilder = Glide.with(getApplicationContext()).asDrawable().sizeMultiplier(0.1f);
                                        Glide.with(getApplicationContext()).load(photo).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(requestBuilder).fitCenter().centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(views.callerPic);
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
                                                    RequestBuilder<Drawable> requestBuilder = Glide.with(getApplicationContext()).asDrawable().sizeMultiplier(0.1f);
                                                    Glide.with(getApplicationContext()).load(photo).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(requestBuilder).fitCenter().centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(views.callerPic);
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
                        // Start the call
                        mainRepository.startCall(data.getSender());
                        views.incomingCallLayout.setVisibility(View.GONE);
                    });

                    views.rejectButton.setOnClickListener(v -> {
                        // Remove call entries from database and finish activity
                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
                        dbRef.child(data.getTarget()).setValue("");
                        dbRef.child(data.getSender()).setValue("declined");
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
                            dbRef.child(username).setValue("ended");
                        }
                    } else {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
                        reference.orderByKey().equalTo(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                        String username = snap.child("username").getValue().toString();
                                        dbRef.child(username).setValue("ended");
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