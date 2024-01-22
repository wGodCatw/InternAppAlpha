package com.example.internapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    ArrayList<ViewPagerItem> viewPagerItemArrayList;
    private final Context context;


    public ViewPagerAdapter(ArrayList<ViewPagerItem> viewPagerItemArrayList, Context context) {
        this.viewPagerItemArrayList = viewPagerItemArrayList;
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
        holder.txtLocation.setText(viewPagerItem.location);


        Glide.with(context).asBitmap().load(viewPagerItemArrayList.get(position).getImageUrl()).into(holder.imgStudentSearch);
    }

    @Override
    public int getItemCount() {
        return viewPagerItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgStudentSearch;
        TextView txtName, txtFaculty, txtLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgStudentSearch = itemView.findViewById(R.id.studentPicSearch);
            txtName = itemView.findViewById(R.id.studentNameSearch);
            txtFaculty = itemView.findViewById(R.id.studentFacultySearch);
            txtLocation = itemView.findViewById(R.id.studentLocationSearch);
        }
    }
}
