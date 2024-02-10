package com.example.internapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class VideoCall extends AppCompatActivity {
    private MainRepository mainRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        Button callBtn = findViewById(R.id.callBtn);
        EditText edtUsername = findViewById(R.id.targetUsername);
        callBtn.setOnClickListener(v -> {
            mainRepository.sendCallRequest(edtUsername.getText().toString(), () -> {
                Toast.makeText(VideoCall.this, "Couldn't find a target", Toast.LENGTH_SHORT).show();
            });
        });

        mainRepository.subscribeForLatestEvent(data -> {
            if (data.getType() == DataModelType.StartCall) {
                Toast.makeText(VideoCall.this, "Call started", Toast.LENGTH_SHORT).show();
            }
        });
    }

}