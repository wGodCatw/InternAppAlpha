package com.example.internapp.StudentDir;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.internapp.R;
import com.example.internapp.MainDir.SpeedDialinit;
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
public class StudentProjectsActivity extends AppCompatActivity {


    private final ProjectsRecViewAdapter adapter = new ProjectsRecViewAdapter(StudentProjectsActivity.this);
    SpeedDialView speedDialView;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    TextView txtNoStudentsFound;


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


        setContentView(R.layout.activity_student_projects);

        ConstraintLayout constraintLayout = findViewById(R.id.parentConstraint); // Replace with your actual layout ID

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) constraintLayout.getLayoutParams();
        params.topMargin = getStatusBarHeight();

        Bundle extras = getIntent().getExtras();
        String username = null;
        if (extras != null) {
            username = extras.getString("username");
        }

        txtNoStudentsFound = findViewById(R.id.txtNoProjectsFound);

        RecyclerView projectsRecView = findViewById(R.id.StudentProjectsRecView);


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
                } else{
                    txtNoStudentsFound.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        projectsRecView.setVisibility(RecyclerView.VISIBLE);
        projectsRecView.setAdapter(adapter);
        projectsRecView.setLayoutManager(new LinearLayoutManager(StudentProjectsActivity.this, LinearLayoutManager.VERTICAL, false));

        SpeedDialView speedDialView = findViewById(R.id.speedDialView);
        // Initialize the SpeedDial view
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), StudentProjectsActivity.this);


        speedDialView = findViewById(R.id.speedDialView);
        // Initialize the SpeedDial view
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), StudentProjectsActivity.this);


    }
}