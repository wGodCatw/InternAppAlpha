package com.example.internapp;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.internapp.databinding.ActivityVideoCallBinding;
import com.google.firebase.database.DatabaseReference;
import com.permissionx.guolindev.PermissionX;


public class VideoCallActivity extends AppCompatActivity implements MainRepository.Listener {

    private ActivityVideoCallBinding views;
    private MainRepository mainRepository;
    private Boolean isCameraMuted = false;
    private Boolean isMicrophoneMuted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityVideoCallBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        init();
    }

    private void init(){
        mainRepository = MainRepository.getInstance();
        views.callBtn.setOnClickListener(v->{
            //start a call request here
            mainRepository.sendCallRequest(views.targetUserNameEt.getText().toString(),()->{
                Toast.makeText(this, "couldn't find the target", Toast.LENGTH_SHORT).show();
            });

        });
        mainRepository.initLocalView(views.localView);
        mainRepository.initRemoteView(views.remoteView);
        mainRepository.listener = this;

        mainRepository.subscribeForLatestEvent(data->{
            if (data.getType()== DataModelType.StartCall){
                runOnUiThread(()->{
                    views.incomingNameTV.setText(data.getSender()+" is Calling you");
                    views.incomingCallLayout.setVisibility(View.VISIBLE);
                    views.acceptButton.setOnClickListener(v->{
                        //star the call here
                        mainRepository.startCall(data.getSender());
                        views.incomingCallLayout.setVisibility(View.GONE);
                    });
                    views.rejectButton.setOnClickListener(v->{
                        views.incomingCallLayout.setVisibility(View.GONE);
                    });
                });
            }
        });

        views.switchCameraButton.setOnClickListener(v->{
            mainRepository.switchCamera();
        });

        views.micButton.setOnClickListener(v->{
            if (isMicrophoneMuted){
                views.micButton.setImageResource(R.drawable.ic_mic);
            }else {
                views.micButton.setImageResource(R.drawable.ic_mic_off);
            }
            mainRepository.toggleAudio(isMicrophoneMuted);
            isMicrophoneMuted=!isMicrophoneMuted;
        });

        views.videoButton.setOnClickListener(v->{
            if (isCameraMuted){
                views.videoButton.setImageResource(R.drawable.ic_video);
            }else {
                views.videoButton.setImageResource(R.drawable.ic_video_off);
            }
            mainRepository.toggleVideo(isCameraMuted);
            isCameraMuted=!isCameraMuted;
        });

        views.endCallButton.setOnClickListener(v->{
            mainRepository.endCall();
            finish();
        });
    }

    @Override
    public void webrtcConnected() {
        runOnUiThread(()->{
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