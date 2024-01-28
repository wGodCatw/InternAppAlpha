package com.example.internapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {


    private RecyclerView FavoriteStudentsRecView;

    SpeedDialView speedDialView;

    //TODO if HR put you as favorite send notification to the student
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        FavoriteStudentsRecView = findViewById(R.id.FavoriteStudentsRecView);

        ArrayList<FavoriteStudent> contacts = new ArrayList<>();
        contacts.add(new FavoriteStudent("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        contacts.add(new FavoriteStudent("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        contacts.add(new FavoriteStudent("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        contacts.add(new FavoriteStudent("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        contacts.add(new FavoriteStudent("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        contacts.add(new FavoriteStudent("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        contacts.add(new FavoriteStudent("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        contacts.add(new FavoriteStudent("Ryan GOD Gosling", "RyanTheBest@gmail.com", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));


        FavoritesRecViewAdapter adapter = new FavoritesRecViewAdapter(this);
        adapter.setFavoriteStudents(contacts);
        FavoriteStudentsRecView.setAdapter(adapter);
        FavoriteStudentsRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        speedDialView = findViewById(R.id.speedDialView);
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), FavoritesActivity.this);

    }
}