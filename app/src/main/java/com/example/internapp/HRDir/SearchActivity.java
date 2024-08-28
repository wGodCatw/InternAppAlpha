package com.example.internapp.HRDir;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.internapp.R;
import com.example.internapp.MainDir.SpeedDialinit;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    public final static ArrayList<String> filtersUniversities = new ArrayList<>();
    public final static ArrayList<String> filtersFaculties = new ArrayList<>();
    private static final ArrayList<University> universities = new ArrayList<>();
    private static final ArrayList<University> faculties = new ArrayList<>();
    static final UniversitiesRecViewAdapter uniAdapter = new UniversitiesRecViewAdapter(universities, faculties);
    static final UniversitiesRecViewAdapter facultyAdapter = new UniversitiesRecViewAdapter(universities, faculties);
    private static ChipGroup chipGroup;
    public TextView filtersTxt;

    /**
     * Creates a Chip (a small UI component that displays information) for the given text.
     * The Chip represents either a university or a faculty and is added to the ChipGroup.
     * If the Chip already exists, it is removed from the ChipGroup and the corresponding filter list.
     *
     * @param faculties      The list of faculties to filter.
     * @param universities   The list of universities to filter.
     * @param text           The text to display on the Chip.
     * @param view           The View object used to inflate the Chip layout.
     * @param uniAdapter     The adapter for the university RecyclerView.
     * @param facultyAdapter The adapter for the faculty RecyclerView.
     */
    public static void createChip(ArrayList<University> faculties, ArrayList<University> universities, String text, View view, UniversitiesRecViewAdapter uniAdapter, UniversitiesRecViewAdapter facultyAdapter) {
        Chip chip = (Chip) LayoutInflater.from(view.getContext()).inflate(R.layout.chip_layout, chipGroup, false);

        if (!filtersFaculties.contains(text) && !filtersUniversities.contains(text)) {
            chip.setText(text);
            chip.setId(ViewCompat.generateViewId());
            chipGroup.addView(chip);

            for (University uni : universities) {
                if (uni.getName().equals(text)) {
                    filtersUniversities.add(text);
                }
            }

            for (University uni : faculties) {
                if (uni.getName().equals(text)) {
                    filtersFaculties.add(text);
                }
            }

        } else {
            // Find the chip with the specified text and remove it
            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                View v = chipGroup.getChildAt(i);
                if (v instanceof Chip) {
                    Chip c = (Chip) v;
                    if (c.getText().equals(text)) {
                        chipGroup.removeView(c);
                        break;
                    }
                }
            }

            // Remove the text from the filters list
            if (filtersUniversities.contains(text)) {
                filtersUniversities.remove(text);
            } else {
                filtersFaculties.remove(text);
            }

        }


        chip.setOnClickListener(v -> {
            chipGroup.removeView(v);

            for (University faculty : faculties) {
                if (faculty.getName().equals(text)) {
                    filtersFaculties.remove(text);
                    // Update the RecyclerView item
                    facultyAdapter.notifyItemChanged(faculties.indexOf(faculty));
                }
            }


            for (University university : universities) {
                if (university.getName().equals(text)) {
                    filtersUniversities.remove(text);
                    // Update the RecyclerView item
                    uniAdapter.notifyItemChanged(universities.indexOf(university));
                }
            }
        });
    }

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0; // Default value if not found
    }

    /**
     * Called when the activity resumes.
     * Clears the ChipGroup, filter lists, and notifies the RecyclerView adapters of data changes.
     */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        chipGroup.removeAllViews();
        filtersUniversities.clear();
        filtersFaculties.clear();
        uniAdapter.notifyDataSetChanged();
        facultyAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        setContentView(R.layout.activity_search);

        ConstraintLayout constraintLayout = findViewById(R.id.parentConstraint); // Replace with your actual layout ID

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) constraintLayout.getLayoutParams();
        params.topMargin = getStatusBarHeight();

        chipGroup = findViewById(R.id.chipGroup);


        filtersTxt = findViewById(R.id.filtersTxt);
        final RecyclerView facultiesRecView = findViewById(R.id.FacultiesRecView);
        final RecyclerView universitiesRecView = findViewById(R.id.UniversitiesRecView);

        // Add sample universities and faculties to the respective lists
        universities.add(new University("Tel Aviv", "https://img.haarets.co.il/bs/00000184-9ebe-d710-a7c6-deff5e1e0000/2e/b4/0de8269e4f0a88c02a847a3bc529/49436148.JPG?precrop=4961,3307,x0,y0&height=1280&width=1920"));
        universities.add(new University("Bar-Ilan", "https://cdn.jns.org/uploads/2019/11/Bar-Ilan-University-Psychology-Department-scaled.jpg"));
        universities.add(new University("Ben Gurion", "https://www.abidat.in/images/Ben-Gurion-University-of-the-Negev.jpg"));
        universities.add(new University("Technion", "https://static.israel21c.org/www/uploads/2015/09/6846305363_881f7b4a47_b.jpg"));
        universities.add(new University("Haifa", "https://pr.haifa.ac.il/wp-content/uploads/2021/10/Aerial_Roy-0153.jpg"));
        universities.add(new University("Weizmann", "https://www.kimmel.co.il/sites/default/files/styles/full_width_xxlarge/public/2021-07/weizmann_cam-ext-02_05bcropped.jpg.jpeg?itok=HqvL41Yt"));
        universities.add(new University("Reichmann", "https://www.visionsoftravel.org/wp-content/uploads/2022/08/Reichman-University-IDC-Israel-2-scaled.jpg"));
        universities.add(new University("Hebrew", "https://www.tclf.org/sites/default/files/styles/full_width/public/thumbnails/image/HebrewUniversity_signature_TamiPorath_2016_03.jpg?itok=twwXVCYY"));
        universities.add(new University("Open University", "https://ounews.co/wp-content/uploads/2021/11/Berrill_new-branding_2018-scaled.jpg"));
        universities.add(new University("Ariel", "https://upload.wikimedia.org/wikipedia/commons/b/b4/Ariel_university.jpg"));


        faculties.add(new University("Business", "https://media.istockphoto.com/id/541114144/photo/bound-by-business.jpg?s=612x612&w=0&k=20&c=sRpFWa216olf8SZkAK3MTwyNAfHNt4G5eGZPHNfOvvQ="));
        faculties.add(new University("Health", "https://www.hhmglobal.com/wp-content/uploads/news/21610/medical-students.jpg"));
        faculties.add(new University("Social sciences", "https://www.topuniversities.com/sites/default/files/articles/lead-images/istock-959533530.jpg"));
        faculties.add(new University("Engineering", "https://res.cloudinary.com/highereducation/images/f_auto,q_auto/v1620062755/AffordableCollegesOnline.org/GettyImages-591168867/GettyImages-591168867.jpg?_i=AA"));
        faculties.add(new University("Psychology", "https://www.pearsonaccelerated.com/content/dam/websites/accelerated-pathways/blog-images/what-psychology-students-should-know.jpg"));
        faculties.add(new University("Computer science", "https://assets-global.website-files.com/6217f188c0aef4eed8d51758/6279bfac2693dc57cb6d9d3c_csphoto.jpeg"));
        faculties.add(new University("Education", "https://res.cloudinary.com/highereducation/images/f_auto,q_auto/v1659634197/BestColleges.com/BC_What-Is-Student-Teaching_247733fd61/BC_What-Is-Student-Teaching_247733fd61.jpg"));
        faculties.add(new University("Journalism", "https://www.rollingstone.com/wp-content/uploads/2022/06/JRN_Module2_in-article-image.jpg?w=1581&h=1054&crop=1"));
        faculties.add(new University("Biology", "https://marvel-b1-cdn.bc0a.com/f00000000290162/images.ctfassets.net/2htm8llflwdx/7sTZ1yfHmDZgSzW5eglpvc/cc0db8624510f74fcfc2a57d60f1518f/Classroom_StudentGroup_ScienceLab_Indoor_GettyImages-1188549344.jpg?fit=thumb"));
        faculties.add(new University("Visual arts", "https://res.cloudinary.com/highereducation/images/w_1024,h_683,c_scale/f_auto,q_auto/v1671826076/BestColleges.com/hubs/art-hub/art-hub-1024x683.jpg"));
        faculties.add(new University("Business", "https://media.istockphoto.com/id/541114144/photo/bound-by-business.jpg?s=612x612&w=0&k=20&c=sRpFWa216olf8SZkAK3MTwyNAfHNt4G5eGZPHNfOvvQ="));
        faculties.add(new University("Health", "https://www.hhmglobal.com/wp-content/uploads/news/21610/medical-students.jpg"));

        uniAdapter.setUniversities(universities);
        universitiesRecView.setAdapter(uniAdapter);
        universitiesRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        facultyAdapter.setUniversities(faculties);
        facultiesRecView.setAdapter(facultyAdapter);
        facultiesRecView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Button goToSearchBtn = findViewById(R.id.goToSearchBtn);
        goToSearchBtn.setOnClickListener(v -> {
            // Start the SearchStudents activity and pass the filter lists as extras
            Intent intent = new Intent(getApplicationContext(), SearchStudents.class);
            intent.putExtra("filtersFaculties", filtersFaculties);
            intent.putExtra("filtersUniversities", filtersUniversities);
            startActivity(intent);
        });


        SpeedDialView speedDialView = findViewById(R.id.speedDialView);
        // Initialize the SpeedDial view (a floating action button with expandable options)
        SpeedDialinit.fab_init(speedDialView, getApplicationContext(), SearchActivity.this);

    }
}