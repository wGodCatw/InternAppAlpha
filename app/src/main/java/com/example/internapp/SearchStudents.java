package com.example.internapp;


import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.internal.zzaa;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchStudents extends AppCompatActivity {

    ViewPager2 viewPager2;

    SpeedDialView speedDialView;

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

        String university = "Technion";

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students");
        referenceProfile.orderByChild("university").equalTo(university).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String nodId = snap.getKey();
                        String mobile = (String) snap.child("mobile").getValue();
                        String userPic = (String) snap.child("userPic").getValue();
                        if(!TextUtils.isEmpty(userPic)){
                            Toast.makeText(SearchStudents.this, userPic, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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

        ArrayList<University> projectsNames = new ArrayList<>();
        projectsNames.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        projectsNames.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        projectsNames.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        projectsNames.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        projectsNames.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        projectsNames.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        projectsNames.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));
        projectsNames.add(new University("Ryan GOD Gosling", "https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large"));

        viewPager2 = findViewById(R.id.viewpager);
        viewPagerItemArrayList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ViewPagerItem viewPagerItem = new ViewPagerItem("https://pbs.twimg.com/media/F0mt2ApXwAE7Lmt?format=jpg&name=large", phoneNumbers.get(i),
                    "Kriko", "Computer Faculty", "Beer Sheva");
            viewPagerItemArrayList.add(viewPagerItem);
        }
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(SearchStudents.this, viewPagerItemArrayList, this, projectsNames);
        viewPager2.setAdapter(viewPagerAdapter);

        viewPager2.setOffscreenPageLimit(2);
        viewPager2.setClipChildren(false);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        speedDialView = findViewById(R.id.speedDialView);
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), SearchStudents.this);
    }
}