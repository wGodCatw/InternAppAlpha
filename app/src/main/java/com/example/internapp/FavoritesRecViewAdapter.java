package com.example.internapp;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FavoritesRecViewAdapter extends RecyclerView.Adapter<FavoritesRecViewAdapter.ViewHolder>{

    private ArrayList<FavoriteStudent> favoriteStudents = new ArrayList<>();
    private final Context context;
    public FavoritesRecViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.txtName.setText(favoriteStudents.get(position).getName());
        holder.txtEmail.setText(favoriteStudents.get(position).getEmail());
        holder.parent.setOnClickListener(v -> Toast.makeText(context, favoriteStudents.get(holder.getAdapterPosition()).getName() + " selected", Toast.LENGTH_SHORT).show());

        Glide.with(context).asBitmap().load(favoriteStudents.get(position).getImageurl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return favoriteStudents.size();
    }

    public void setFavoriteStudents(ArrayList<FavoriteStudent> favoriteStudents) {
        this.favoriteStudents = favoriteStudents;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView txtName;
        private final TextView txtEmail;
        private final CardView parent;
        private final ImageView image;


        public ViewHolder(View itemView){
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            image = itemView.findViewById(R.id.imageView);
            parent = itemView.findViewById(R.id.parent);
            txtEmail = itemView.findViewById(R.id.txtEmail);
        }
    }
}
