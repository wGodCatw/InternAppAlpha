package com.example.internapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.leinardi.android.speeddial.SpeedDialView;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UploadUserPicActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ProgressBar progressBar;
    private ImageView profilePic;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_user_pic);

        SpeedDialView speedDialView = findViewById(R.id.speedDialView);
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), UploadUserPicActivity.this);

        authProfile = FirebaseAuth.getInstance();

        Button btnPicChoose = findViewById(R.id.buttonPictureChoose);
        Button btnUploadPic = findViewById(R.id.buttonUploadTo);

        progressBar = findViewById(R.id.progress_bar);

        profilePic = findViewById(R.id.profilePicture);

        firebaseUser = authProfile.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("ProfilePictures");

        Uri uri = firebaseUser.getPhotoUrl();
        Picasso.get().load(uri).into(profilePic);

        btnPicChoose.setOnClickListener(v -> openFileChooser());

        btnUploadPic.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            uploadPic();
        });
    }

    private void uploadPic() {
        if (uriImage != null) {
            StorageReference fileReference = storageReference.child(Objects.requireNonNull(authProfile.getCurrentUser()).getUid()
                    + "." + getFileExtension(uriImage));

            fileReference.putFile(uriImage).addOnSuccessListener(taskSnapshot -> {
                progressBar.setVisibility(View.VISIBLE);

                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    firebaseUser = authProfile.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri).build();
                    firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UploadUserPicActivity.this, "Upload successful!", Toast.LENGTH_LONG).show();
                        Uri uri1 = firebaseUser.getPhotoUrl();

                        DatabaseReference referenceHR = FirebaseDatabase.getInstance().getReference("Registered users/HRs");
                        referenceHR.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    DatabaseReference referenceHRPic = FirebaseDatabase.getInstance().getReference("Registered users/HRs/" + firebaseUser.getUid() + "/userPic");
                                    referenceHRPic.setValue(uri1.toString()).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(firebaseUser.getDisplayName()).build();
                                            firebaseUser.updateProfile(userProfileChangeRequest);
                                        } else {
                                            try {
                                                throw Objects.requireNonNull(task.getException());
                                            } catch (Exception e) {
                                                Toast.makeText(UploadUserPicActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        progressBar.setVisibility(View.GONE);
                                    });
                                    ;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        DatabaseReference referenceStudent = FirebaseDatabase.getInstance().getReference("Registered users/Students");
                        referenceStudent.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    DatabaseReference referenceStudentPic = FirebaseDatabase.getInstance().getReference("Registered users/Students/" + firebaseUser.getUid() + "/userPic");

                                    referenceStudentPic.setValue(uri1.toString()).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(firebaseUser.getDisplayName()).build();
                                            firebaseUser.updateProfile(userProfileChangeRequest);
                                        } else {
                                            try {
                                                throw Objects.requireNonNull(task.getException());
                                            } catch (Exception e) {
                                                Toast.makeText(UploadUserPicActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        progressBar.setVisibility(View.GONE);
                                    });
                                    ;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    });
                });


                Intent intent = new Intent(UploadUserPicActivity.this, UserProfileActivity.class);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> Toast.makeText(UploadUserPicActivity.this, e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            Toast.makeText(UploadUserPicActivity.this, "No file was selected", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private String getFileExtension(Uri uriImage) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uriImage));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            profilePic.setImageURI(uriImage);
        }
    }
}