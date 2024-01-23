package com.example.internapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;

public class HomepageActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    Button logoutBtn;

    Button searchBtn;
    Button favoritesBtn;
    TextView welcomeText;

    SpeedDialView speedDialView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        auth = FirebaseAuth.getInstance();
        logoutBtn = findViewById(R.id.logoutBtn);
        searchBtn = findViewById(R.id.searchBtn);
        favoritesBtn = findViewById(R.id.favoritesBtn);
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

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        favoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FavoritesActivity.class);
                startActivity(intent);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });


        speedDialView = findViewById(R.id.speedDialView);
        speedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.action_settings, R.drawable.ic_settings)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.lightblue, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.blue, getTheme())).create());

        speedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.action_home, R.drawable.ic_home)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.lightblue, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.blue, getTheme())).create());

        speedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.action_favorites, R.drawable.ic_favorites)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.lightblue, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.blue, getTheme())).create());

        speedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.action_schedule, R.drawable.ic_schedule)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.lightblue, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.blue, getTheme())).create());

        speedDialView.addActionItem(new SpeedDialActionItem.Builder(R.id.action_search, R.drawable.ic_search)
                .setFabBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.lightblue, getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(getResources(), R.color.blue, getTheme())).create());



        speedDialView.inflate(R.menu.main_menu);
        speedDialView.setExpansionMode(SpeedDialView.ExpansionMode.LEFT);
        speedDialView.setUseReverseAnimationOnClose(true);
        speedDialView.getMainFab().setCustomSize(200);
        speedDialView.setOrientation(LinearLayout.VERTICAL);
//        getMenuInflater().inflate(R.menu.main_menu, ActionMenuView.menu);
        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                if (actionItem.getId() == R.id.action_settings) {
                    Toast.makeText(getApplicationContext(), "You clicked: " + actionItem.getId() + "Label: " + actionItem.getLabel(getApplicationContext()), Toast.LENGTH_SHORT).show();
                    speedDialView.close(true);
                } else if (actionItem.getId() == R.id.action_home) {
                    Toast.makeText(getApplicationContext(), "You're here already!", Toast.LENGTH_SHORT).show();
                    speedDialView.close(true);
                } else if (actionItem.getId() == R.id.action_favorites) {
                    speedDialView.close(true);
                    Intent intent = new Intent(getApplicationContext(), FavoritesActivity.class);
                    startActivity(intent);
                } else if (actionItem.getId() == R.id.action_schedule) {
                    Toast.makeText(getApplicationContext(), "You clicked: " + actionItem.getId() + "Label: " + actionItem.getLabel(getApplicationContext()), Toast.LENGTH_SHORT).show();
                    speedDialView.close(true);
                } else if (actionItem.getId() == R.id.action_search) {
                    speedDialView.close(true);
                    Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }
}