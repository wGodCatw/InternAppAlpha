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
    public static ArrayList<String> filters;
    private static ChipGroup chipGroup;
    private RecyclerView UniversitiesRecView;
    private RecyclerView FacultiesRecView;
    SpeedDialView speedDialView;
    private Button goToSearchBtn;

    public static void createChip(String text, View view) {
        Chip chip = (Chip) LayoutInflater.from(view.getContext()).inflate(R.layout.chip_layout, null);
        chip.setText(text);
        chip.setId(ViewCompat.generateViewId());
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(v);
                filters.remove(text);
//                filtersTxt.setText("Filters: " + filters.toString());
            }
        });
        chipGroup.addView(chip);
        filters.add(text);
//        filtersTxt.setText("Filters: " + filters.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        filtersTxt = findViewById(R.id.filtersTxt);
        FacultiesRecView = findViewById(R.id.FacultiesRecView);
        UniversitiesRecView = findViewById(R.id.UniversitiesRecView);


        ArrayList<University> universities = new ArrayList<>();
        ArrayList<University> faculties = new ArrayList<>();
        filters = new ArrayList<>();

        universities.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        universities.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));


        faculties.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        faculties.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));


        UniversitiesRecViewAdapter uniAdapter = new UniversitiesRecViewAdapter(this);
        uniAdapter.setUniversities(universities);
        UniversitiesRecView.setAdapter(uniAdapter);
        UniversitiesRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        UniversitiesRecViewAdapter facultyAdapter = new UniversitiesRecViewAdapter(this);
        facultyAdapter.setUniversities(faculties);
        FacultiesRecView.setAdapter(facultyAdapter);
        FacultiesRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        goToSearchBtn = findViewById(R.id.goToSearchBtn);
        goToSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchStudents.class);
                startActivity(intent);
            }
        });

        chipGroup = findViewById(R.id.chipGroup);

        speedDialView = findViewById(R.id.speedDialView);
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), SearchActivity.this);

    }
}