package com.example.internapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.Objects;

/**
 * Activity that displays a list of favorite students for the current HR user.
 */
public class UploadProjectActivity extends AppCompatActivity {


    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private final ProjectsRecViewAdapter adapter = new ProjectsRecViewAdapter(UploadProjectActivity.this);
    private Button btnUploadProject, btnChoosePicture;
    private TextInputEditText projectLink, projectDescription, projectTitle;
    private ImageView projectImage;
    private ProgressBar progressBar;
    private String username = "";
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
                    projectImage.setImageURI(uriImage);
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_project);


        RecyclerView projectsRecView = findViewById(R.id.projectsRecView);
        btnUploadProject = findViewById(R.id.buttonProjectUpload);
        projectImage = findViewById(R.id.imgProject);
        projectTitle = findViewById(R.id.projectTitle);
        projectLink = findViewById(R.id.projectLink);
        btnChoosePicture = findViewById(R.id.buttonProjectPicture);
        projectDescription = findViewById(R.id.projectDescription);
        progressBar = findViewById(R.id.progress_bar);


        btnChoosePicture.setOnClickListener(v -> openFileChooser());

        btnUploadProject.setOnClickListener(v -> {
            if (TextUtils.isEmpty(projectTitle.getText())) {
                projectTitle.setError("Title can't be empty");
                Toast.makeText(UploadProjectActivity.this, "Project title can't be empty", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(projectLink.getText())) {
                projectLink.setError("Link to project can't be empty");
                Toast.makeText(UploadProjectActivity.this, "Project link can't be empty", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.WEB_URL.matcher(projectLink.getText()).matches()) {
                projectLink.setError("Project link has to be a proper URL");
                Toast.makeText(UploadProjectActivity.this, "Project link has to be a proper URL", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(projectDescription.getText())) {
                projectDescription.setError("Project description can't be empty");
                Toast.makeText(UploadProjectActivity.this, "Project description can't be empty", Toast.LENGTH_SHORT).show();
            } else if (uriImage == null) {
                Toast.makeText(UploadProjectActivity.this, "Project image can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                uploadImageToFirebaseStorage(uriImage);
            }
        });




        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered users/Students/" + firebaseUser.getUid());
        ref.orderByChild("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    username = Objects.requireNonNull(snapshot.child("username").getValue()).toString();

                    ProjectsRecViewAdapter.projects.clear();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Projects/" + username);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    Project project = snap.getValue(Project.class);
                                    adapter.addProject(project);
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


        projectsRecView.setVisibility(RecyclerView.VISIBLE);
        projectsRecView.setAdapter(adapter);
        projectsRecView.setLayoutManager(new LinearLayoutManager(UploadProjectActivity.this, LinearLayoutManager.VERTICAL, false));

        SpeedDialView speedDialView = findViewById(R.id.speedDialView);
        // Initialize the SpeedDial view
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), UploadProjectActivity.this);

    }


    private void uploadNewProject(Project project) {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Projects/" + project.getStudentUsername() + "/");
        DatabaseReference newProjectRef = reference.push(); // This creates a unique ID for each new project

        newProjectRef.setValue(project).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UploadProjectActivity.this, "Project uploaded successfully!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                projectDescription.setText("");
                projectImage.setImageResource(R.drawable.ic_projects);
                projectLink.setText("");
                projectTitle.setText("");
                adapter.addProject(project);
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UploadProjectActivity.this, "Project upload failed, try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void uploadImageToFirebaseStorage(Uri imageUri) {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/" + System.currentTimeMillis() + ".jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveProjectInfo(imageUrl);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(UploadProjectActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        }
    }

    private void saveProjectInfo(String imageUrl) {
        String title = projectTitle.getText().toString();
        String link = projectLink.getText().toString();
        String description = projectDescription.getText().toString();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered users/Students/" + firebaseUser.getUid());
        ref.orderByChild("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    username = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                    Project project = new Project(username, title, link, imageUrl, description);
                    uploadNewProject(project);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UploadProjectActivity.this, "Failed to save project info", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mGetContent.launch(intent);
    }
}