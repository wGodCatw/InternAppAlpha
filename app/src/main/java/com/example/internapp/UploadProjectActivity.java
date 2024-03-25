package com.example.internapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.leinardi.android.speeddial.SpeedDialView;

/**
 * Activity that displays a list of favorite students for the current HR user.
 */
public class UploadProjectActivity extends AppCompatActivity {


    private Button btnUploadProject, btnChoosePicture;
    private TextInputEditText projectLink, projectDescription, projectTitle;
    private ImageView projectImage;
    private ProgressBar progressBar;
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
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
    private final ProjectsRecViewAdapter adapter = new ProjectsRecViewAdapter(UploadProjectActivity.this);

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
            Log.e("HERE", "HERE");

            if (TextUtils.isEmpty(projectTitle.getText())) {
                projectTitle.setError("Title can't be empty");
            } else if (TextUtils.isEmpty(projectLink.getText())) {
                projectLink.setError("Link to project can't be empty");
            } else if (!Patterns.WEB_URL.matcher(projectLink.getText()).matches()) {
                projectLink.setError("Project link has to be a proper URL");
            } else if (TextUtils.isEmpty(projectDescription.getText())) {
                projectDescription.setError("Project description can't be empty");
            } else if (TextUtils.isEmpty(uriImage.toString())) {
                Toast.makeText(UploadProjectActivity.this, "Project image can't be empty", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("HERE", "STARTED");
                String title = projectTitle.getText().toString();
                String link = projectLink.getText().toString();
                String image = uriImage.toString();
                String description = projectDescription.getText().toString();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered users/Students/" + firebaseUser.getUid());
                ref.orderByChild("username").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                username = snapshot.child("username").getValue().toString();
                                Log.e("HERE", username);
                                Project project = new Project(username, title, link, image, description);
                                progressBar.setVisibility(View.VISIBLE);
                                uploadNewProject(project);
                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Projects");
        reference.orderByChild(username).addValueEventListener(new ValueEventListener() {
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

        projectsRecView.setVisibility(RecyclerView.VISIBLE);
        projectsRecView.setAdapter(adapter);
        projectsRecView.setLayoutManager(new LinearLayoutManager(UploadProjectActivity.this, LinearLayoutManager.VERTICAL, false));

        SpeedDialView speedDialView = findViewById(R.id.speedDialView);
        // Initialize the SpeedDial view
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), UploadProjectActivity.this);

    }

    private void uploadNewProject(Project project) {
        Log.e("HERE", "Uploading");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Projects/" + project.getStudentUsername() + "/");

        reference.setValue(project).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.e("HERE", "Uploaded");
                Toast.makeText(UploadProjectActivity.this, "Project uploaded successfully!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                projectDescription.clearComposingText();
                projectImage.setImageResource(R.drawable.ic_projects);
                projectLink.clearComposingText();
                projectTitle.clearComposingText();

            } else {
                Log.e("HERE", "Not uploaded");
                Toast.makeText(UploadProjectActivity.this, "Project upload the project, try again!", Toast.LENGTH_SHORT).show();

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