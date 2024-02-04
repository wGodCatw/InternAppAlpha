package com.example.internapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmailActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private FirebaseUser firebaseUser;
    private TextView txtAuthenticated;
    private TextInputEditText oldEmail, updEmail, password;
    private Button btnUpdateEmail, btnAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        progressBar = findViewById(R.id.progressBar);
        txtAuthenticated = findViewById(R.id.titleUpdEmailAuth);
        oldEmail = findViewById(R.id.updEmailEmail);
        updEmail = findViewById(R.id.updEmailNewEmail);
        password = findViewById(R.id.updEmailPassword);
        btnUpdateEmail = findViewById(R.id.updEmailBtnUpdate);
        btnAuth = findViewById(R.id.updEmailBtnAuth);
    }
}