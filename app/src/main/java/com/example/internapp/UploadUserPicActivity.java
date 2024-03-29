package com.example.internapp;

import android.app.Activity;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    private ProgressBar progressBar;
    private ImageView profilePic;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private Uri uriImage;

    /**
     * Activity result launcher to handle the result of image picking intent.
     */
    private final ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            uriImage = data.getData();
                            profilePic.setImageURI(uriImage);
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_user_pic);

        // Initializing the SpeedDialView for quick actions.
        SpeedDialView speedDialView = findViewById(R.id.speedDialView);
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), UploadUserPicActivity.this);

        authProfile = FirebaseAuth.getInstance();

        // Getting UI elements by their ID.
        Button btnPicChoose = findViewById(R.id.buttonPictureChoose);
        Button btnUploadPic = findViewById(R.id.buttonUploadTo);

        progressBar = findViewById(R.id.progress_bar);

        profilePic = findViewById(R.id.profilePicture);

        firebaseUser = authProfile.getCurrentUser();

        // Setting up Firebase storage reference for profile pictures.
        storageReference = FirebaseStorage.getInstance().getReference("ProfilePictures");

        // Loading current user's profile picture if available.
        Uri uri = firebaseUser.getPhotoUrl();
        Picasso.get().load(uri).into(profilePic);

        // Setting listeners for buttons.
        btnPicChoose.setOnClickListener(v -> openFileChooser());
        btnUploadPic.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            uploadPic();
        });
    }

    /**
     * Uploads the selected image to Firebase Storage and updates user's profile picture URL.
     */
    private void uploadPic() {
        if (uriImage != null) {
            // Generate file name with user's UID and the file extension.
            StorageReference fileReference = storageReference.child(Objects.requireNonNull(authProfile.getCurrentUser()).getUid()
                    + "." + getFileExtension(uriImage));

            // Upload file to Firebase Storage.
            fileReference.putFile(uriImage).addOnSuccessListener(taskSnapshot -> {
                // Show progress bar during the upload process.
                progressBar.setVisibility(View.VISIBLE);

                // Retrieve download URL of the uploaded file.
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    firebaseUser = authProfile.getCurrentUser();

                    // Update user profile with new photo URL.
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri).build();
                    firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UploadUserPicActivity.this, "Upload successful!", Toast.LENGTH_LONG).show();
                        Uri uri1 = firebaseUser.getPhotoUrl();

                        // Update database reference for HR users with new profile picture URL.
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
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        // Update database reference for student users with new profile picture URL.
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
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    });
                });

                // Redirect to UserProfileActivity after successful upload.
                Intent intent = new Intent(UploadUserPicActivity.this, UserProfileActivity.class);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> Toast.makeText(UploadUserPicActivity.this, e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            Toast.makeText(UploadUserPicActivity.this, "No file was selected", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Retrieves the file extension of the image URI.
     *
     * @param uriImage The URI of the image.
     * @return The file extension as a String.
     */
    private String getFileExtension(Uri uriImage) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uriImage));
    }

    /**
     * Launches an intent to pick an image from the user's device.
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mGetContent.launch(intent);
    }
}