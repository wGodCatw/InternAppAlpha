package com.example.internapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.leinardi.android.speeddial.SpeedDialView;

/**
 * Activity that displays a list of favorite students for the current HR user.
 */
public class UploadProjectActivity extends AppCompatActivity {


    private Button btnUploadProject, btnChoosePicture;
    private TextInputEditText projectLink, projectDescription, projectTitle;
    private ImageView projectImage;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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


        btnChoosePicture.setOnClickListener(v -> openFileChooser());


        btnUploadProject.setOnClickListener(v -> {
            String title = projectTitle.getText().toString();
        });



        projectsRecView.setVisibility(RecyclerView.VISIBLE);
        ProjectsRecViewAdapter adapter = new ProjectsRecViewAdapter(UploadProjectActivity.this);
//        adapter.addProject();
        projectsRecView.setAdapter(adapter);
        projectsRecView.setLayoutManager(new LinearLayoutManager(UploadProjectActivity.this, LinearLayoutManager.VERTICAL, false));


        SpeedDialView speedDialView = findViewById(R.id.speedDialView);
        // Initialize the SpeedDial view
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), UploadProjectActivity.this);

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mGetContent.launch(intent);
    }
}