package com.example.internapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.leinardi.android.speeddial.SpeedDialView;


public class HomepageActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    Button logoutBtn;
    TextView welcomeText;

    ImageView userIcon;

    ImageView studentImg1;

    ImageView studentImg2;

    SpeedDialView speedDialView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        auth = FirebaseAuth.getInstance();
        logoutBtn = findViewById(R.id.logoutBtn);
        welcomeText = findViewById(R.id.welcomeText);

        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (user.getDisplayName() != null)
                welcomeText.setText("Hello, " + user.getDisplayName());
        }

        userIcon = findViewById(R.id.userIcon);
        Glide.with(getApplicationContext()).asBitmap().load(user.getPhotoUrl()).into(userIcon);

        studentImg1 = findViewById(R.id.studentImg1);
        studentImg2 = findViewById(R.id.studentImg2);


        Glide.with(getApplicationContext()).load("https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large").into(studentImg2);
        Glide.with(getApplicationContext()).load("https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large").into(studentImg1);

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        speedDialView = findViewById(R.id.speedDialView);
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), HomepageActivity.this);
    }
}