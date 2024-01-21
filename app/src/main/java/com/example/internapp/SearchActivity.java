package com.example.internapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView UniversitiesRecView;
    private RecyclerView FacultiesRecView;
    private static ChipGroup chipGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        FacultiesRecView = findViewById(R.id.FacultiesRecView);
        UniversitiesRecView = findViewById(R.id.UniversitiesRecView);


        ArrayList<University> universities = new ArrayList<>();
        ArrayList<University> faculties = new ArrayList<>();

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

        chipGroup = findViewById(R.id.chipGroup);

//        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.add("Technion");
//        arrayList.add("Technion");
//        arrayList.add("Technion");
//        arrayList.add("Technion");
//        arrayList.add("Technion");
//        arrayList.add("Technion");
//
//        for (String s: arrayList){
//            Chip chip = (Chip) LayoutInflater.from(SearchActivity.this).inflate(R.layout.chip_layout, null);
//            chip.setText(s);
//            chip.setId(ViewCompat.generateViewId());
//            chipGroup.addView(chip);
//        }


        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

            }
        });
    }

    public static void createChip(String text, View view) {
        Chip chip = (Chip) LayoutInflater.from(view.getContext()).inflate(R.layout.chip_layout, null);
        chip.setText(text);
        chip.setId(ViewCompat.generateViewId());
        chipGroup.addView(chip);
    }
}