package com.example.internapp;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class SearchStudents extends AppCompatActivity {

    ViewPager2 viewPager2;
    ArrayList<ViewPagerItem> viewPagerItemArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_students);

        viewPager2 = findViewById(R.id.viewpager);
        viewPagerItemArrayList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ViewPagerItem viewPagerItem = new ViewPagerItem("https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large",
                    "Kriko", "Computer Faculty", "Beer Sheva");
            viewPagerItemArrayList.add(viewPagerItem);
        }
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(viewPagerItemArrayList, this);
        viewPager2.setAdapter(viewPagerAdapter);

        viewPager2.setOffscreenPageLimit(2);
        viewPager2.setClipChildren(false);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
    }
}