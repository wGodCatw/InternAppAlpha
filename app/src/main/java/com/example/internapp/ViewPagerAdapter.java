package com.example.internapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    private ArrayList<ViewPagerItem> viewPagerItemArrayList;
    private ArrayList<University> projectsNames;
    private final Context context;
    private Activity a;


    public ViewPagerAdapter(Activity a, ArrayList<ViewPagerItem> viewPagerItemArrayList, Context context, ArrayList<University> projectsNames) {
        this.viewPagerItemArrayList = viewPagerItemArrayList;
        this.a = a;
        this.projectsNames = projectsNames;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewpager_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        ViewPagerItem viewPagerItem = viewPagerItemArrayList.get(position);

        holder.txtName.setText(viewPagerItem.name);
        holder.txtFaculty.setText(viewPagerItem.faculty);
        holder.whatsappLink.setOnClickListener(v -> {
            String url = "https://api.whatsapp.com/send?phone=" + viewPagerItem.getWhatsappNumber();
            try {
                PackageManager pm = context.getApplicationContext().getPackageManager();
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                a.startActivity(i);
            } catch (PackageManager.NameNotFoundException e){
                a.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });


        Glide.with(context).asBitmap().load(viewPagerItemArrayList.get(position).imageUrl).into(holder.imgStudentSearch);
    }

    @Override
    public int getItemCount() {
        return viewPagerItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imgStudentSearch, whatsappLink;
        private RecyclerView projects;
        private TextView txtName, txtFaculty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            projects = itemView.findViewById(R.id.ProjectsRecView);
            whatsappLink = itemView.findViewById(R.id.whatsappLink);
            imgStudentSearch = itemView.findViewById(R.id.studentPicSearch);
            txtName = itemView.findViewById(R.id.studentNameSearch);
            txtFaculty = itemView.findViewById(R.id.studentFacultySearch);


            UniversitiesRecViewAdapter projectsAdapter = new UniversitiesRecViewAdapter(a.getApplicationContext());
            projectsAdapter.setUniversities(projectsNames);
            projects.setAdapter(projectsAdapter);
            projects.setLayoutManager(new LinearLayoutManager(a.getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        }
    }
}
