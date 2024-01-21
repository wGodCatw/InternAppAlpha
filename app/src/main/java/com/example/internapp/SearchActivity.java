package com.example.internapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ChipGroup chipGroup = findViewById(R.id.chipGroup);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Technion");
        arrayList.add("Technion");
        arrayList.add("Technion");
        arrayList.add("Technion");
        arrayList.add("Technion");
        arrayList.add("Technion");

        for (String s: arrayList){
            Chip chip = (Chip) LayoutInflater.from(SearchActivity.this).inflate(R.layout.chip_layout, null);
            chip.setText(s);
            chip.setId(ViewCompat.generateViewId());
            chipGroup.addView(chip);
        }

        chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

            }
        });
    }
}