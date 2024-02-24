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
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), FavoritesActivity.this);

    }

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
                        getStudentsFromIDList(favoritesUIDs, myCallback);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getStudentsFromIDList(ArrayList<String> studentIDs, final StudentListCallback myCallback) {
        ArrayList<FavoriteStudent> students = new ArrayList<>();
        final AtomicInteger counter = new AtomicInteger(studentIDs.size());
        if(studentIDs.isEmpty() || studentIDs.get(0).equals("")){
            txtNoStudentsFound.setVisibility(TextView.VISIBLE);
        } else{
            for (String id : studentIDs) {
                getStudentFromId(id, value -> {
                    students.add(value);
                    if (counter.decrementAndGet() == 0) {
                        myCallback.onCallback(students);
                    }
                });
            }
        }


    }

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

                        FavoriteStudent student = new FavoriteStudent(name, faculty, userPic);
                        myCallback.onCallback(student);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface StudentCallback {
        void onCallback(FavoriteStudent value);
    }

    public interface StudentListCallback {
        void onCallback(ArrayList<FavoriteStudent> value);
    }

}