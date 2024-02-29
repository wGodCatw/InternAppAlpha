package com.example.internapp;

import static com.example.internapp.SearchActivity.filtersFaculties;
import static com.example.internapp.SearchActivity.filtersUniversities;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UniversitiesRecViewAdapter extends RecyclerView.Adapter<UniversitiesRecViewAdapter.ViewHolder>{

    private final ArrayList<University> universitiesList;
    private final ArrayList<University> facultiesList;
    private ArrayList<University> universities = new ArrayList<>();

    public UniversitiesRecViewAdapter(final ArrayList<University> universitiesList, final ArrayList<University> facultiesList) {
        this.universitiesList = universitiesList;
        this.facultiesList = facultiesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.university_list_item, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.txtName.setText(universities.get(position).getName());

        if (filtersUniversities.contains(universities.get(position).getName()) || filtersFaculties.contains(universities.get(position).getName())) {
            holder.txtName.setTextColor(holder.itemView.getResources().getColor(R.color.white, null));
            holder.parent.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.blue, null));
        } else {
            holder.txtName.setTextColor(holder.itemView.getResources().getColor(R.color.black, null));
            holder.parent.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.lightblue, null));
        }


        holder.parent.setOnClickListener(v -> {
            if(holder.txtName.getCurrentTextColor() == holder.itemView.getResources().getColor(R.color.white, null)){
                String text = universities.get(holder.getAdapterPosition()).getName();
                SearchActivity.createChip(facultiesList, universitiesList, text, v, SearchActivity.uniAdapter, SearchActivity.facultyAdapter);
                holder.txtName.setTextColor(holder.itemView.getResources().getColor(R.color.black, null));
                holder.parent.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.lightblue, null));
            } else{
                holder.txtName.setTextColor(holder.itemView.getResources().getColor(R.color.white, null));
                holder.parent.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.blue, null));
                String text = universities.get(holder.getAdapterPosition()).getName();
                SearchActivity.createChip(facultiesList, universitiesList, text, v, SearchActivity.uniAdapter, SearchActivity.facultyAdapter);
            }

        });
        Glide.with(holder.itemView.getContext()).asBitmap().load(universities.get(position).getImageUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return universities.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUniversities(final ArrayList<University> universities) {
        this.universities = universities;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView txtName;
        private final CardView parent;
        private final ImageView image;


        public ViewHolder(View itemView){
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            image = itemView.findViewById(R.id.universityImg);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
