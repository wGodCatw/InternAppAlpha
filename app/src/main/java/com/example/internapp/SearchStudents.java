package com.example.internapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class SearchStudents extends AppCompatActivity {

    ViewPager2 viewPager2;

    String whatsappPhone = "+972504828406";
    String whatsappPhone2 = "+972504828405";
    String whatsappPhone3 = "+972503888193";
    String whatsappPhone4 = "+972546132757";
    String whatsappPhone5 = "+972539546832";
    String whatsappPhone6 = "+380506755047";
    String whatsappPhone7 = "+972533867971";
    String whatsappPhone8 = "+972585290306";
    String whatsappPhone9 = "+972585606865";
    String whatsappPhone10 = "+972533753237";
    ArrayList<ViewPagerItem> viewPagerItemArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_students);

        ArrayList<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(whatsappPhone);
        phoneNumbers.add(whatsappPhone2);
        phoneNumbers.add(whatsappPhone3);
        phoneNumbers.add(whatsappPhone4);
        phoneNumbers.add(whatsappPhone5);
        phoneNumbers.add(whatsappPhone6);
        phoneNumbers.add(whatsappPhone7);
        phoneNumbers.add(whatsappPhone8);
        phoneNumbers.add(whatsappPhone9);
        phoneNumbers.add(whatsappPhone10);

        viewPager2 = findViewById(R.id.viewpager);
        viewPagerItemArrayList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ViewPagerItem viewPagerItem = new ViewPagerItem("https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large", phoneNumbers.get(i),
                    "Kriko", "Computer Faculty", "Beer Sheva");
            viewPagerItemArrayList.add(viewPagerItem);
        }
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(SearchStudents.this, viewPagerItemArrayList, this);
        viewPager2.setAdapter(viewPagerAdapter);

        viewPager2.setOffscreenPageLimit(2);
        viewPager2.setClipChildren(false);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
    }
}