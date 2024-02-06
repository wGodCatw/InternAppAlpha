package com.example.internapp;

import android.content.Context;
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

    private ArrayList<University> universities = new ArrayList<>();
    private final Context context;
    public UniversitiesRecViewAdapter(Context context) {
        this.context = context;
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
//        holder.parent.setOnClickListener(v -> Toast.makeText(context, universities.get(holder.getAdapterPosition()).getName() + " selected", Toast.LENGTH_SHORT).show());
        holder.parent.setOnClickListener(v -> {
            String text = universities.get(holder.getAdapterPosition()).getName();
            SearchActivity.createChip(text, v);
        });
        Glide.with(context).asBitmap().load(universities.get(position).getImageUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return universities.size();
    }

    public void setUniversities(ArrayList<University> universities) {
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
