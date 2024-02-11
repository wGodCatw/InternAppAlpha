package com.example.internapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    public static TextView filtersTxt;
    private final static ArrayList<String> filtersUniversities = new ArrayList<>();
    private final static ArrayList<String> filtersFaculties = new ArrayList<>();
    private static ChipGroup chipGroup;
    private final ArrayList<University> universities = new ArrayList<>();
    private final ArrayList<University> faculties = new ArrayList<>();

    public static void createChip(ArrayList<University> faculties, ArrayList<University> universities, String text, View view) {
        Chip chip = (Chip) LayoutInflater.from(view.getContext()).inflate(R.layout.chip_layout, null);


        if (!filtersFaculties.contains(text) && !filtersUniversities.contains(text)) {
            chip.setText(text);
            chip.setId(ViewCompat.generateViewId());
            chipGroup.addView(chip);

            for (University uni:
                 universities) {
                if(uni.getName().equals(text)){
                    filtersUniversities.add(text);
                }
            }

            for (University uni:
                    faculties) {
                if(uni.getName().equals(text)){
                    filtersFaculties.add(text);
                }
            }

        }
        chip.setOnClickListener(v -> {
            chipGroup.removeView(v);
            for (University faculty:
                 faculties) {
                if(faculty.getName().equals(text)){
                    filtersFaculties.remove(text);
                }
            }
            for (University university:
                    universities) {
                if(university.getName().equals(text)){
                    filtersUniversities.remove(text);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        chipGroup.removeAllViews();
        filtersUniversities.clear();
        filtersFaculties.clear();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        chipGroup = findViewById(R.id.chipGroup);


        filtersTxt = findViewById(R.id.filtersTxt);
        final RecyclerView facultiesRecView = findViewById(R.id.FacultiesRecView);
        final RecyclerView universitiesRecView = findViewById(R.id.UniversitiesRecView);

        universities.add(new University("Tel Aviv", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Bar-Ilan", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Ben Gurion", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Technion", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Haifa", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Weizmann", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Reichmann", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Hebrew", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Open University", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Ariel", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));


        faculties.add(new University("Business", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Health", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Social sciences", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Engineering", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Psychology", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Computer science", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Education", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Journalism", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Biology", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Visual arts", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));


        final UniversitiesRecViewAdapter uniAdapter = new UniversitiesRecViewAdapter(universities, faculties);
        uniAdapter.setUniversities(universities);
        universitiesRecView.setAdapter(uniAdapter);
        universitiesRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        final UniversitiesRecViewAdapter facultyAdapter = new UniversitiesRecViewAdapter(universities, faculties);
        facultyAdapter.setUniversities(faculties);
        facultiesRecView.setAdapter(facultyAdapter);
        facultiesRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Button goToSearchBtn = findViewById(R.id.goToSearchBtn);
        goToSearchBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SearchStudents.class);
            intent.putExtra("filtersFaculties", filtersFaculties);
            intent.putExtra("filtersUniversities", filtersUniversities);
            startActivity(intent);
        });


        SpeedDialView speedDialView = findViewById(R.id.speedDialView);
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), SearchActivity.this);

    }
}