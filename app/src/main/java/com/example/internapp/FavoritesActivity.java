package com.example.internapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {


    private RecyclerView FavoriteStudentsRecView;

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


    }
}