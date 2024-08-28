package com.example.internapp.HRDir;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.internapp.R;
import com.example.internapp.MainDir.SpeedDialinit;
import com.example.internapp.MainDir.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Activity to search for students based on selected universities and faculties.
 */
public class SearchStudents extends AppCompatActivity {

    private final ArrayList<String> allUniversities = new ArrayList<>();
    private final ArrayList<String> allFaculties = new ArrayList<>();
    private final ArrayList<String> studentsByUniversitiesID = new ArrayList<>();
    private final ArrayList<String> studentsByFacultiesID = new ArrayList<>();
    SpeedDialView speedDialView;
    TextView txtNoStudentsFound;
    private ArrayList<String> filtersFaculties = new ArrayList<>();
    private ArrayList<String> filtersUniversities = new ArrayList<>();

    /**
     * Clears filters for universities and faculties when back button is pressed.
     *
     * @return Returns OnBackInvokedDispatcher for back button handling.
     */
    @NonNull
    @Override
    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        SearchActivity.filtersFaculties.clear();
        SearchActivity.filtersUniversities.clear();
        // Make all items in faculty and university recyclerview have grey background and black text
        return super.getOnBackInvokedDispatcher();
    }

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0; // Default value if not found
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_search_students);

        ConstraintLayout constraintLayout = findViewById(R.id.parentConstraint); // Replace with your actual layout ID

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) constraintLayout.getLayoutParams();
        params.topMargin = getStatusBarHeight();


        txtNoStudentsFound = findViewById(R.id.txtNoStudentsFound);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            filtersUniversities = extras.getStringArrayList("filtersUniversities");
            filtersFaculties = extras.getStringArrayList("filtersFaculties");
        }

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
                        // Remove all duplicate values from the array list students
                        for (int i = 0; i < students.size(); i++) {
                            for (int j = i + 1; j < students.size(); j++) {
                                if (students.get(i).username.equals(students.get(j).username)) {
                                    students.remove(j);
                                    j--;
                                }
                            }
                        }
                        for (Student i : students) {
                            final ViewPagerItem viewPagerItem = new ViewPagerItem(i);
                            viewPagerItemArrayList.add(viewPagerItem);
                        }

                        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(SearchStudents.this, viewPagerItemArrayList, getApplicationContext());
                        viewPager2.setAdapter(viewPagerAdapter);

                        viewPager2.setOffscreenPageLimit(2);
                        viewPager2.setClipChildren(false);
                        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
                    }
                });

            });
        } else if (filtersFaculties.isEmpty() && !filtersUniversities.isEmpty()) {
            // Implementation when faculties are empty and universities are selected
            filterAllUniversities(filtersUniversities, value -> {
                studentIDs.addAll(value);

                getStudentsFromIDList(studentIDs, value1 -> {
                    students.addAll(value1);
                    if (students.isEmpty()) {
                        txtNoStudentsFound.setVisibility(View.VISIBLE);
                        viewPager2.setVisibility(View.GONE);
                    } else {
                        viewPager2.setVisibility(View.VISIBLE);
                        txtNoStudentsFound.setVisibility(View.GONE);
                        for (int i = 0; i < students.size(); i++) {
                            for (int j = i + 1; j < students.size(); j++) {
                                if (students.get(i).username.equals(students.get(j).username)) {
                                    students.remove(j);
                                    j--;
                                }
                            }
                        }
                        for (Student i : students) {
                            ViewPagerItem viewPagerItem = new ViewPagerItem(i);
                            viewPagerItemArrayList.add(viewPagerItem);
                        }

                        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(SearchStudents.this, viewPagerItemArrayList, getApplicationContext());
                        viewPager2.setAdapter(viewPagerAdapter);

                        viewPager2.setOffscreenPageLimit(2);
                        viewPager2.setClipChildren(false);
                        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
                    }
                });

            });
        } else if (!filtersFaculties.isEmpty()) {
            // Implementation when both faculties and universities are selected
            filterAllUniversities(filtersUniversities, value -> {
                studentIDs.addAll(value);

                filterAllFaculties(filtersFaculties, value13 -> {
                    studentIDs.retainAll(value13);

                    getStudentsFromIDList(studentIDs, value12 -> {
                        students.addAll(value12);

                        if (students.isEmpty()) {
                            viewPager2.setVisibility(View.GONE);
                            txtNoStudentsFound.setVisibility(View.VISIBLE);
                        } else {
                            viewPager2.setVisibility(View.VISIBLE);
                            txtNoStudentsFound.setVisibility(View.GONE);
                            for (int i = 0; i < students.size(); i++) {
                                for (int j = i + 1; j < students.size(); j++) {
                                    if (students.get(i).username.equals(students.get(j).username)) {
                                        students.remove(j);
                                        j--;
                                    }
                                }
                            }
                            for (Student i : students) {
                                ViewPagerItem viewPagerItem = new ViewPagerItem(i);
                                viewPagerItemArrayList.add(viewPagerItem);
                            }

                            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(SearchStudents.this, viewPagerItemArrayList, getApplicationContext());
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
    }

    /**
     * Filters students by faculty from Firebase database.
     *
     * @param faculty    The faculty to filter by.
     * @param myCallback Callback to return filtered student IDs.
     */
    private void filterByFaculty(String faculty, final UserCallback myCallback) {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students");
        referenceProfile.orderByChild("faculty").equalTo(faculty).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String nodId = snap.getKey();
                        studentsByFacultiesID.add(nodId);
                    }
                }
                myCallback.onCallback(studentsByFacultiesID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Filters students by university from Firebase database.
     *
     * @param university The university to filter by.
     * @param myCallback Callback to return filtered student IDs.
     */
    private void filterByUniversity(String university, final UserCallback myCallback) {
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
                }
                myCallback.onCallback(studentsByUniversitiesID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Filters students by a list of universities.
     *
     * @param universities The list of universities to filter by.
     * @param myCallback   Callback to return filtered student IDs.
     */
    private void filterAllUniversities(ArrayList<String> universities, final UserCallback myCallback) {
        final AtomicInteger counter = new AtomicInteger(universities.size());
        for (String university : universities) {
            filterByUniversity(university, value -> {
                if (!value.isEmpty()) {
                    allUniversities.addAll(value);
                    if (counter.decrementAndGet() == 0) {
                        myCallback.onCallback(allUniversities);
                    }
                } else {
                    txtNoStudentsFound.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /**
     * Filters students by a list of faculties.
     *
     * @param faculties  The list of faculties to filter by.
     * @param myCallback Callback to return filtered student IDs.
     */
    private void filterAllFaculties(ArrayList<String> faculties, final UserCallback myCallback) {
        final AtomicInteger counter = new AtomicInteger(faculties.size());
        for (String faculty : faculties) {
            filterByFaculty(faculty, value -> {
                if (!value.isEmpty()) {
                    allFaculties.addAll(value);
                    if (counter.decrementAndGet() == 0) {
                        myCallback.onCallback(allFaculties);
                    }
                } else {
                    txtNoStudentsFound.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    /**
     * Retrieves student details from Firebase based on student ID.
     *
     * @param studentId  The ID of the student to retrieve.
     * @param myCallback Callback to return student details.
     */
    private void getStudentFromId(String studentId, final UserFinalCallback myCallback) {
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered users/Students");
        referenceProfile.orderByKey().equalTo(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String username = (String) snap.child("username").getValue();
                        String name = (String) snap.child("name").getValue();
                        String mobile = (String) snap.child("mobile").getValue();
                        String userPic = (String) snap.child("userPic").getValue();
                        String faculty = (String) snap.child("faculty").getValue();
                        String university = (String) snap.child("university").getValue();
                        Student student = new Student(username, userPic, name, university, faculty, mobile);
                        myCallback.onCallback(student);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Retrieves list of students from Firebase based on list of student IDs.
     *
     * @param studentIDs The list of student IDs to retrieve.
     * @param myCallback Callback to return list of students.
     */
    private void getStudentsFromIDList(ArrayList<String> studentIDs, final UserListCallback myCallback) {
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

    /**
     * Callback interface for returning filtered student IDs.
     */
    public interface UserCallback {
        void onCallback(ArrayList<String> value);
    }

    /**
     * Callback interface for returning individual student details.
     */
    public interface UserFinalCallback {
        void onCallback(Student value);
    }

    /**
     * Callback interface for returning list of students.
     */
    public interface UserListCallback {
        void onCallback(ArrayList<Student> value);
    }
}
