package com.example.internapp;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;

public class SearchStudents extends AppCompatActivity {

    ViewPager2 viewPager2;

    SpeedDialView speedDialView;
    TextView txtNoStudentsFound;
    ArrayList<String> filtersFaculties = new ArrayList<>();
    ArrayList<String> filtersUniversities = new ArrayList<>();

    ArrayList<ViewPagerItem> viewPagerItemArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_students);
        txtNoStudentsFound = findViewById(R.id.txtNoStudentsFound);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            filtersUniversities = extras.getStringArrayList("filtersUniversities");
            filtersFaculties = extras.getStringArrayList("filtersFaculties");
        }

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

        filterStudents(filtersUniversities, filtersFaculties, projectsNames);

        speedDialView = findViewById(R.id.speedDialView);
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), SearchStudents.this);
    }

    private void filterStudents(ArrayList<String> filtersUniversities, ArrayList<String> filterFaculties, ArrayList<University> projectsNames) {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students");
        ArrayList<Student> students = new ArrayList<>();

        if (!filtersUniversities.isEmpty() || !filterFaculties.isEmpty()) {

            ArrayList<String> studentsByFaculties = new ArrayList<>();
            ArrayList<String> studentsByUniversities = new ArrayList<>();

            if (filtersUniversities.isEmpty()) {
                Log.e("No universities", "NOOO");
                filterByFaculty(filterFaculties, new OnDataRetrieved() {
                    @Override
                    public void onData(ArrayList<String> data) {
                        ArrayList<String> chosenStudentsfinal = new ArrayList<>(data);
                        if (!chosenStudentsfinal.isEmpty()) {
                            for (String id : chosenStudentsfinal) {
                                referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChildren()) {
                                            for (DataSnapshot snap : snapshot.getChildren()) {
                                                String nodId = snap.getKey();

                                                if (nodId.equals(id)) {
                                                    String name = (String) snap.child("name").getValue();
                                                    String mobile = (String) snap.child("mobile").getValue();
                                                    String userPic = (String) snap.child("userPic").getValue();
                                                    String faculty = (String) snap.child("faculty").getValue();
                                                    Student student = new Student(userPic, name, faculty, mobile);
                                                    students.add(student);
                                                }
                                            }
                                            for (Student i : students) {
                                                ViewPagerItem viewPagerItem = new ViewPagerItem(i);
                                                viewPagerItemArrayList.add(viewPagerItem);
                                            }

                                            txtNoStudentsFound.setVisibility(View.GONE);

                                            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(SearchStudents.this, viewPagerItemArrayList, getApplicationContext(), projectsNames);
                                            viewPager2.setAdapter(viewPagerAdapter);

                                            viewPager2.setOffscreenPageLimit(2);
                                            viewPager2.setClipChildren(false);
                                            viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
                                        } else {
                                            txtNoStudentsFound.setVisibility(View.VISIBLE);
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        } else {
                            txtNoStudentsFound.setVisibility(View.VISIBLE);
                        }
                    }
                });
            } else if (filterFaculties.isEmpty()) {
                Log.e("No faculties", "NOOOOO");
                filterByUniversity(filtersUniversities, data -> {
                    ArrayList<String> chosenStudentsfinal = new ArrayList<>(data);
                    if (!chosenStudentsfinal.isEmpty()) {
                        Log.e("WTF", chosenStudentsfinal.toString());
                        for (String id : chosenStudentsfinal) {
                            referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChildren()) {
                                        for (DataSnapshot snap : snapshot.getChildren()) {
                                            String nodId = snap.getKey();

                                            if (nodId.equals(id)) {
                                                String name = (String) snap.child("name").getValue();
                                                String mobile = (String) snap.child("mobile").getValue();
                                                String userPic = (String) snap.child("userPic").getValue();
                                                String faculty = (String) snap.child("faculty").getValue();
                                                Student student = new Student(userPic, name, faculty, mobile);
                                                students.add(student);
                                            }
                                        }

                                        Log.e("University student", students.toString());
                                        for (Student i : students) {
                                            ViewPagerItem viewPagerItem = new ViewPagerItem(i);
                                            viewPagerItemArrayList.add(viewPagerItem);
                                        }

                                        txtNoStudentsFound.setVisibility(View.GONE);

                                        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(SearchStudents.this, viewPagerItemArrayList, getApplicationContext(), projectsNames);
                                        viewPager2.setAdapter(viewPagerAdapter);

                                        viewPager2.setOffscreenPageLimit(2);
                                        viewPager2.setClipChildren(false);
                                        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
                                    } else {
                                        txtNoStudentsFound.setVisibility(View.VISIBLE);
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    } else {
                        txtNoStudentsFound.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                Log.e("Both is", "NOOOOO");
                filterByBoth(filtersUniversities, filterFaculties, data -> {

                    studentsByUniversities.addAll(data.get(0));
                    studentsByFaculties.addAll(data.get(1));

                    studentsByFaculties.retainAll(studentsByUniversities);
                    ArrayList<String> chosenStudentsfinal = new ArrayList<>(studentsByFaculties);
                    Log.e("Both students", chosenStudentsfinal.toString());

                    if (!chosenStudentsfinal.isEmpty()) {
                        for (String id : chosenStudentsfinal) {
                            referenceProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChildren()) {
                                        for (DataSnapshot snap : snapshot.getChildren()) {
                                            String nodId = snap.getKey();

                                            if (nodId.equals(id)) {
                                                String name = (String) snap.child("name").getValue();
                                                String mobile = (String) snap.child("mobile").getValue();
                                                String userPic = (String) snap.child("userPic").getValue();
                                                String faculty = (String) snap.child("faculty").getValue();
                                                Student student = new Student(userPic, name, faculty, mobile);
                                                students.add(student);
                                            }
                                        }
                                        for (Student i : students) {
                                            ViewPagerItem viewPagerItem = new ViewPagerItem(i);
                                            viewPagerItemArrayList.add(viewPagerItem);
                                        }

                                        txtNoStudentsFound.setVisibility(View.GONE);

                                        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(SearchStudents.this, viewPagerItemArrayList, getApplicationContext(), projectsNames);
                                        viewPager2.setAdapter(viewPagerAdapter);

                                        viewPager2.setOffscreenPageLimit(2);
                                        viewPager2.setClipChildren(false);
                                        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
                                    } else {
                                        txtNoStudentsFound.setVisibility(View.VISIBLE);
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    } else {
                        txtNoStudentsFound.setVisibility(View.VISIBLE);
                    }
                });
            }


        } else {
            txtNoStudentsFound.setVisibility(View.VISIBLE);
        }

    }

    private ArrayList<String> filterByFaculty(ArrayList<String> filterFaculties, OnDataRetrieved callback) {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students");
        ArrayList<String> studentsByFaculties = new ArrayList<>();
        for (String faculty : filterFaculties) {
            referenceProfile.orderByChild("faculty").equalTo(faculty).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String nodId = snap.getKey();
                            Log.e("By faculty id", nodId);
                            studentsByFaculties.add(nodId);
                        }
                    } else {
                        callback.onData(null);
                    }
                    callback.onData(studentsByFaculties);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return studentsByFaculties;
    }

    private ArrayList<String> filterByUniversity(ArrayList<String> filtersUniversities, OnDataRetrieved callback) {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students");
        ArrayList<String> studentsByUniversities = new ArrayList<>();
        for (String university : filtersUniversities) {
            referenceProfile.orderByChild("university").equalTo(university).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String nodId = snap.getKey();
                            Log.e("By university id", nodId);
                            studentsByUniversities.add(nodId);
                        }
                    } else {
                        callback.onData(null);
                    }
                    callback.onData(studentsByUniversities);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return studentsByUniversities;
    }

    public ArrayList<ArrayList<String>> filterByBoth(ArrayList<String> filtersUniversities, ArrayList<String> filterFaculties, OnDataRetrievedAll callback) {
        ArrayList<ArrayList<String>> arr = new ArrayList<>();
        ArrayList<String> studentsByUniversities = new ArrayList<>();
        ArrayList<String> studentsByFaculties = new ArrayList<>();
        filterByUniversity(filtersUniversities, data -> {
            studentsByUniversities.addAll(data);

            filterByFaculty(filterFaculties, data1 -> {
                studentsByFaculties.addAll(data1);

                arr.add(studentsByUniversities);
                arr.add(studentsByFaculties);

                callback.onData(arr);
            });
        });

        return arr;
    }

    interface OnDataRetrieved {
        void onData(ArrayList<String> data);
    }

    interface OnDataRetrievedAll {
        void onData(ArrayList<ArrayList<String>> data);
    }
}