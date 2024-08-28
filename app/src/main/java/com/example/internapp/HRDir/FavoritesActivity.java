package com.example.internapp.HRDir;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Activity that displays a list of favorite students for the current HR user.
 */
public class FavoritesActivity extends AppCompatActivity {


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

        setContentView(R.layout.activity_favorites);

        ConstraintLayout constraintLayout = findViewById(R.id.parentConstraint); // Replace with your actual layout ID

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) constraintLayout.getLayoutParams();
        params.topMargin = getStatusBarHeight();


        txtNoStudentsFound = findViewById(R.id.txtNoStudentsFound);

        RecyclerView favoriteStudentsRecView = findViewById(R.id.FavoriteStudentsRecView);

        /*
          Retrieves the list of favorite students for the current HR user from the Firebase database.

          @param myCallback Callback to handle the retrieved list of favorite students.
         */


        getFavoriteStudents(value -> {

            if (value.isEmpty()) {
                txtNoStudentsFound.setVisibility(TextView.VISIBLE);
                favoriteStudentsRecView.setVisibility(RecyclerView.GONE);
            } else {
                txtNoStudentsFound.setVisibility(TextView.GONE);
                favoriteStudentsRecView.setVisibility(RecyclerView.VISIBLE);
                FavoritesRecViewAdapter adapter = new FavoritesRecViewAdapter(FavoritesActivity.this);
                adapter.setFavoriteStudents(value);
                favoriteStudentsRecView.setAdapter(adapter);
                favoriteStudentsRecView.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this, LinearLayoutManager.VERTICAL, false));
            }

        });

        speedDialView = findViewById(R.id.speedDialView);
        // Initialize the SpeedDial view
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), FavoritesActivity.this);

    }

    /**
     * Retrieves the list of favorite students for the current HR user from the Firebase database.
     *
     * @param myCallback Callback to handle the retrieved list of favorite students.
     */
    private void getFavoriteStudents(final StudentListCallback myCallback) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/HRs/" + firebaseUser.getUid());
        reference.child("favoriteStudents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    String favoritesStr = (String) snapshot.getValue();
                    // Split the comma-separated list of student IDs into a list

                    assert favoritesStr != null;
                    ArrayList<String> favoritesUIDs = new ArrayList<>(Arrays.asList(favoritesStr.split(",")));
                    //if favoritesUIDs is empty txtNoStudentsFound will be visible
                    //else txtNoStudentsFound will be gone
                    if (favoritesUIDs.isEmpty() || favoritesStr.equals("")) {
                        txtNoStudentsFound.setVisibility(TextView.VISIBLE);

                    } else {
                        txtNoStudentsFound.setVisibility(TextView.GONE);
                        // Retrieve the list of FavoriteStudent objects from the list of student IDs
                        getStudentsFromIDList(favoritesUIDs, myCallback);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Retrieves the list of FavoriteStudent objects from the provided list of student IDs.
     *
     * @param studentIDs List of student IDs to retrieve the FavoriteStudent objects for.
     * @param myCallback Callback to handle the retrieved list of FavoriteStudent objects.
     */
    private void getStudentsFromIDList(ArrayList<String> studentIDs, final StudentListCallback myCallback) {
        ArrayList<FavoriteStudent> students = new ArrayList<>();
        final AtomicInteger counter = new AtomicInteger(studentIDs.size());

        for (String id : studentIDs) {
            getStudentFromId(id, value -> {
                if (value != null) {
                    students.add(value);
                }
                if (counter.decrementAndGet() == 0) {
                    myCallback.onCallback(students);
                }
            });
        }
    }


    /**
     * Retrieves the FavoriteStudent object for the provided student ID from the Firebase database.
     *
     * @param studentId ID of the student to retrieve the FavoriteStudent object for.
     * @param myCallback Callback to handle the retrieved FavoriteStudent object.
     */
    private void getStudentFromId(String studentId, final StudentCallback myCallback) {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students");
        referenceProfile.orderByKey().equalTo(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String name = (String) snap.child("name").getValue();
                        String userPic = (String) snap.child("userPic").getValue();
                        String faculty = (String) snap.child("faculty").getValue();
                        String username = (String) snap.child("username").getValue();

                        FavoriteStudent student = new FavoriteStudent(name, faculty, userPic, username);
                        myCallback.onCallback(student);
                    }
                } else {
                    // If the student ID is not found, call the callback with null
                    myCallback.onCallback(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    /**
     * Callback interface to handle a single FavoriteStudent object.
     */
    public interface StudentCallback {
        void onCallback(FavoriteStudent value);
    }

    /**
     * Callback interface to handle a list of FavoriteStudent objects.
     */
    public interface StudentListCallback {
        void onCallback(ArrayList<FavoriteStudent> value);
    }

}