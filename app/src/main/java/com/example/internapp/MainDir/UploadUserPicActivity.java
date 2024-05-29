package com.example.internapp.MainDir;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.internapp.R;
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
    private final ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
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

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0; // Default value if not found
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_upload_user_pic);

        ConstraintLayout constraintLayout = findViewById(R.id.parentConstraint); // Replace with your actual layout ID

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) constraintLayout.getLayoutParams();
        params.topMargin = getStatusBarHeight();

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
        RequestBuilder<Drawable> requestBuilder = Glide.with(UploadUserPicActivity.this).asDrawable().sizeMultiplier(0.1f);
        Glide.with(UploadUserPicActivity.this).load(uri).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(requestBuilder).fitCenter().centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(profilePic);
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
            StorageReference fileReference = storageReference.child(Objects.requireNonNull(authProfile.getCurrentUser()).getUid() + "." + getFileExtension(uriImage));

            // Upload file to Firebase Storage.
            fileReference.putFile(uriImage).addOnSuccessListener(taskSnapshot -> {
                // Show progress bar during the upload process.
                progressBar.setVisibility(View.VISIBLE);

                // Retrieve download URL of the uploaded file.
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    firebaseUser = authProfile.getCurrentUser();

                    // Update user profile with new photo URL.
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                    firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UploadUserPicActivity.this, "Upload successful!", Toast.LENGTH_LONG).show();
                        Uri uri1 = firebaseUser.getPhotoUrl();

                        DatabaseReference reference, referencePic;
                        if (UserProfileActivity.ROLE.equals("HR")) {
                            reference = FirebaseDatabase.getInstance().getReference("Registered users/HRs");
                            referencePic = FirebaseDatabase.getInstance().getReference("Registered users/HRs/" + firebaseUser.getUid() + "/userPic");

                        } else {
                            reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
                            referencePic = FirebaseDatabase.getInstance().getReference("Registered users/Students/" + firebaseUser.getUid() + "/userPic");

                        }

                        // Update database reference for HR users with new profile picture URL.
                        reference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    referencePic.setValue(uri1.toString()).addOnCompleteListener(task -> {
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