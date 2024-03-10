package com.example.internapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    //TODO if HR put you as favorite send notification to the student
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        txtNoStudentsFound = findViewById(R.id.txtNoStudentsFound);

        RecyclerView favoriteStudentsRecView = findViewById(R.id.FavoriteStudentsRecView);

        /*
          Retrieves the list of favorite students for the current HR user from the Firebase database.

          @param myCallback Callback to handle the retrieved list of favorite students.
         */
        getFavoriteStudents(value -> {
            if (value.isEmpty()) {
                Log.e("VALUE", value.toString());
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
                    assert favoritesStr != null;
                    ArrayList<String> favoritesUIDs = new ArrayList<>(Arrays.asList(favoritesStr.split(",")));
                    //if favoritesUIDs is empty txtNoStudentsFound will be visible
                    //else txtNoStudentsFound will be gone
                    if (favoritesUIDs.isEmpty()) {
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
        if(studentIDs.isEmpty() || studentIDs.get(0).equals("")){
            txtNoStudentsFound.setVisibility(TextView.VISIBLE);
        } else{
            for (String id : studentIDs) {
                // Retrieve the FavoriteStudent object for each student ID
                getStudentFromId(id, value -> {
                    students.add(value);
                    if (counter.decrementAndGet() == 0) {
                        myCallback.onCallback(students);
                    }
                });
            }
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
        referenceProfile.orderByKey().equalTo(studentId).addValueEventListener(new ValueEventListener() {
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