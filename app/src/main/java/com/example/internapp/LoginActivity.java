package com.example.internapp;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static final int REQ_ONE_TAP = 100;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private EditText usernameEdt;
    private EditText passwordEdt;
    private Button loginBtn;
    private Button toRegister;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId("1084322055281-rk1so85ivs3qb30tq3ak6pc2ct0d31oh.apps.googleusercontent.com")
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();


        Button buttonGoogleSignIn = findViewById(R.id.googleSignIn);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == Activity.RESULT_OK) {
                            Log.d("true", String.valueOf(true));


                            try {
                                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(o.getData());
                                Log.d("sign in", String.valueOf(String.valueOf(oneTapClient.getSignInCredentialFromIntent(o.getData()))));

                                String idToken = credential.getGoogleIdToken();
                                String username = credential.getId();
                                String password = credential.getPassword();
                                mAuth.signInWithCredential(GoogleAuthProvider.getCredential(credential.getGoogleIdToken(), null))
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                currentUser = mAuth.getCurrentUser();
                                                Log.d("blyat", String.valueOf(currentUser));

                                                if (currentUser != null) {
                                                    Log.d("inside", String.valueOf(currentUser));

                                                    Intent intent = o.getData();
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "Fuck!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });


                                if (idToken != null) {
                                    // Got an ID token from Google. Use it to authenticate
                                    // with your backend.
                                    Log.d(TAG, "Got ID token.");
                                } else if (password != null) {
                                    // Got a saved username and password. Use them to authenticate
                                    // with your backend.
                                    Log.d(TAG, "Got password.");
                                }
                            } catch (ApiException e) {
                                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }
        );
        buttonGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneTapClient.beginSignIn(signInRequest)
                        .addOnSuccessListener(new OnSuccessListener<BeginSignInResult>() {
                            @Override
                            public void onSuccess(BeginSignInResult result) {
//                                try {
                                currentUser = mAuth.getCurrentUser();
                                Log.d("blyatyyyyy", String.valueOf(currentUser));

                                activityResultLauncher.launch(new Intent(getApplicationContext(), HomepageActivity.class));
//                                    startIntentSenderForResult(
//                                            result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
//                                            null, 0, 0, 0);
//                                } catch (IntentSender.SendIntentException e) {
//                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // No saved credentials found. Launch the One Tap sign-up flow, or
                                // do nothing and continue presenting the signed-out UI.
                                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });


        toRegister = findViewById(R.id.ToRegister);
        loginBtn = findViewById(R.id.loginBtn);
        usernameEdt = findViewById(R.id.usernameEdt);
        passwordEdt = findViewById(R.id.passwordEdt);

        toRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = String.valueOf(usernameEdt.getText());
                password = String.valueOf(passwordEdt.getText());

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "No Email broski", Toast.LENGTH_LONG).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "No Password broski", Toast.LENGTH_LONG).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    currentUser = mAuth.getCurrentUser();
                                    Toast.makeText(getApplicationContext(), "Login successful Brother", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQ_ONE_TAP) {
//            try {
//                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
//                String idToken = credential.getGoogleIdToken();
//                String username = credential.getId();
//                String password = credential.getPassword();
//                mAuth.signInWithCredential(GoogleAuthProvider.getCredential(credential.getGoogleIdToken(), null))
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                FirebaseUser currentUser = mAuth.getCurrentUser();
//                                Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        });
//
//
//                if (idToken != null) {
//                    // Got an ID token from Google. Use it to authenticate
//                    // with your backend.
//                    Log.d(TAG, "Got ID token.");
//                } else if (password != null) {
//                    // Got a saved username and password. Use them to authenticate
//                    // with your backend.
//                    Log.d(TAG, "Got password.");
//                }
//            } catch (ApiException e) {
//                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}