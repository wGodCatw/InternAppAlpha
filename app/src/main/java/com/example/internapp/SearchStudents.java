package com.example.internapp;


import android.os.Bundle;
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
import java.util.concurrent.atomic.AtomicInteger;

public class SearchStudents extends AppCompatActivity {


    SpeedDialView speedDialView;
    TextView txtNoStudentsFound;
    private ArrayList<String> filtersFaculties = new ArrayList<>();
    private ArrayList<String> filtersUniversities = new ArrayList<>();

//    private ArrayList<ViewPagerItem> viewPagerItemArrayList;
    private final ArrayList<String> allUniversities = new ArrayList<>();
    private final ArrayList<String> allFaculties = new ArrayList<>();
    private final ArrayList<String> studentsByUniversitiesID = new ArrayList<>();
    private final ArrayList<String> studentsByFacultiesID = new ArrayList<>();




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

        final ViewPager2 viewPager2 = findViewById(R.id.viewpager);
        final ArrayList<ViewPagerItem> viewPagerItemArrayList = new ArrayList<>();
        studentsByFacultiesID.clear();
        studentsByUniversitiesID.clear();

        speedDialView = findViewById(R.id.speedDialView);
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), SearchStudents.this);
        ArrayList<Student> students = new ArrayList<>();
        ArrayList<String> studentIDs = new ArrayList<>();

        if (filtersUniversities.isEmpty() && !filtersFaculties.isEmpty()) {
            filterAllFaculties(filtersFaculties, value -> {
                studentIDs.addAll(value);

                getStudentsFromIDList(studentIDs, value1 -> {
                    students.addAll(value1);
                    if (students.isEmpty()) {
                        txtNoStudentsFound.setVisibility(View.VISIBLE);
                    } else {
                        txtNoStudentsFound.setVisibility(View.GONE);
                        for (Student i : students) {
                            final ViewPagerItem viewPagerItem = new ViewPagerItem(i);
                            viewPagerItemArrayList.add(viewPagerItem);
                        }

                        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(SearchStudents.this, viewPagerItemArrayList, getApplicationContext(), projectsNames);
                        viewPager2.setAdapter(viewPagerAdapter);

                        viewPager2.setOffscreenPageLimit(2);
                        viewPager2.setClipChildren(false);
                        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
                    }
                });

            });
        } else if (filtersFaculties.isEmpty() && !filtersUniversities.isEmpty()) {

            filterAllUniversities(filtersUniversities, value -> {
                studentIDs.addAll(value);

                getStudentsFromIDList(studentIDs, value1 -> {
                    students.addAll(value1);
                    if (students.isEmpty()) {
                        txtNoStudentsFound.setVisibility(View.VISIBLE);
                    } else {
                        txtNoStudentsFound.setVisibility(View.GONE);
                        for (Student i : students) {
                            ViewPagerItem viewPagerItem = new ViewPagerItem(i);
                            viewPagerItemArrayList.add(viewPagerItem);
                        }

                        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(SearchStudents.this, viewPagerItemArrayList, getApplicationContext(), projectsNames);
                        viewPager2.setAdapter(viewPagerAdapter);

                        viewPager2.setOffscreenPageLimit(2);
                        viewPager2.setClipChildren(false);
                        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
                    }
                });

            });
        } else if (!filtersFaculties.isEmpty()) {
            filterAllUniversities(filtersUniversities, value -> {
                studentIDs.addAll(value);

                filterAllFaculties(filtersFaculties, value13 -> {
                    studentIDs.retainAll(value13);

                    getStudentsFromIDList(studentIDs, value12 -> {
                        students.addAll(value12);

                        if (students.isEmpty()) {
                            txtNoStudentsFound.setVisibility(View.VISIBLE);
                        } else {
                            txtNoStudentsFound.setVisibility(View.GONE);
                            for (Student i : students) {
                                ViewPagerItem viewPagerItem = new ViewPagerItem(i);
                                viewPagerItemArrayList.add(viewPagerItem);
                            }

                            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(SearchStudents.this, viewPagerItemArrayList, getApplicationContext(), projectsNames);
                            viewPager2.setAdapter(viewPagerAdapter);

                            viewPager2.setOffscreenPageLimit(2);
                            viewPager2.setClipChildren(false);
                            viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
                        }
                    });
                });
            });


        } else {
            txtNoStudentsFound.setVisibility(View.VISIBLE);
        }


        if (students.isEmpty()) {
            txtNoStudentsFound.setVisibility(View.VISIBLE);
        } else {
            txtNoStudentsFound.setVisibility(View.GONE);
            for (Student i : students) {
                ViewPagerItem viewPagerItem = new ViewPagerItem(i);
                viewPagerItemArrayList.add(viewPagerItem);
            }
        }

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(SearchStudents.this, viewPagerItemArrayList, getApplicationContext(), projectsNames);
        viewPager2.setAdapter(viewPagerAdapter);

        viewPager2.setOffscreenPageLimit(2);
        viewPager2.setClipChildren(false);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
    }


    public void filterByFaculty(String faculty, final UserCallback myCallback) {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students");
        referenceProfile.orderByChild("faculty").equalTo(faculty).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String nodId = snap.getKey();
                        studentsByFacultiesID.add(nodId);
                    }
                    myCallback.onCallback(studentsByFacultiesID);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void filterByUniversity(String university, final UserCallback myCallback) {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students");
        referenceProfile.orderByChild("university").equalTo(university).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String nodId = snap.getKey();
                        assert nodId != null;
                        studentsByUniversitiesID.add(nodId);
                    }
                    myCallback.onCallback(studentsByUniversitiesID);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void filterAllUniversities(ArrayList<String> universities, final UserCallback myCallback) {
        final AtomicInteger counter = new AtomicInteger(universities.size());
        for (String university :
                universities) {
            filterByUniversity(university, value -> {
                allUniversities.addAll(value);
                if (counter.decrementAndGet() == 0) {
                    myCallback.onCallback(allUniversities);
                }
            });
        }
    }

    public void filterAllFaculties(ArrayList<String> faculties, final UserCallback myCallback) {
        final AtomicInteger counter = new AtomicInteger(faculties.size());
        for (String faculty :
                faculties) {
            filterByFaculty(faculty, value -> {
                allFaculties.addAll(value);
                if (counter.decrementAndGet() == 0) {
                    myCallback.onCallback(allFaculties);
                }
            });
        }
    }

    public void getStudentFromId(String studentId, final UserFinalCallback myCallback) {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students");
        referenceProfile.orderByKey().equalTo(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot snap :
                            snapshot.getChildren()) {
                        String name = (String) snap.child("name").getValue();
                        String mobile = (String) snap.child("mobile").getValue();
                        String userPic = (String) snap.child("userPic").getValue();
                        String faculty = (String) snap.child("faculty").getValue();
                        Student student = new Student(userPic, name, faculty, mobile);
                        myCallback.onCallback(student);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //10 2
                //20 6 (3+3)
                //30 12 (6+6)
                //40 20 (10+10)
                //50 30 (15+15)
            }
        });
    }

    public void getStudentsFromIDList(ArrayList<String> studentIDs, final UserListCallback myCallback) {
        ArrayList<Student> students = new ArrayList<>();
        final AtomicInteger counter = new AtomicInteger(studentIDs.size());

        for (String id : studentIDs) {
            getStudentFromId(id, value -> {
                students.add(value);
                if (counter.decrementAndGet() == 0) {
                    myCallback.onCallback(students);
                }
            });
        }
    }

    public interface UserCallback {
        void onCallback(ArrayList<String> value);
    }

    public interface UserFinalCallback {
        void onCallback(Student value);
    }

    public interface UserListCallback {
        void onCallback(ArrayList<Student> value);
    }

}