package com.example.internapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter class for the RecyclerView displaying the list of favorite students.
 */
public class ProjectsRecViewAdapter extends RecyclerView.Adapter<ProjectsRecViewAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<FavoriteStudent> projects = new ArrayList<>();

    /**
     * Constructor for the FavoritesRecViewAdapter.
     *
     * @param context The context in which the adapter is being used.
     */
    public ProjectsRecViewAdapter(Context context) {
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
        // Bind the data from the FavoriteStudent object to the ViewHolder
        holder.txtName.setText(projects.get(position).getName());
        holder.txtEmail.setText(projects.get(position).getEmail());
        Log.e("HERE", projects.get(position).getUsername());
        holder.txtUsername.setText("@" + projects.get(position).getUsername());

        // Set an OnClickListener for the CardView
        holder.parent.setOnClickListener(v -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
            reference.orderByChild("username").equalTo(projects.get(position).getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String UID = "";
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            UID = snap.getKey();
                        }
                        Intent intent = new Intent(context, StudentProfileActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("UID", UID);
                        context.startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        // Load the image using Picasso library
        if (!projects.get(position).getImageUrl().equals("none")) {
            Picasso.get().load(projects.get(position).getImageUrl()).into(holder.image);

        }
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    /**
     * Set the list of favorite students to be displayed in the RecyclerView.
     *
     * @param favoriteStudents The list of FavoriteStudent objects to be displayed.
     */
    public void setFavoriteStudents(ArrayList<FavoriteStudent> favoriteStudents) {
        this.projects = favoriteStudents;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtName;
        private final TextView txtUsername;
        private final TextView txtEmail;
        private final CardView parent;
        private final ImageView image;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The View representing the individual item in the RecyclerView.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtName = itemView.findViewById(R.id.txtName);
            image = itemView.findViewById(R.id.universityImg);
            parent = itemView.findViewById(R.id.parent);
            txtEmail = itemView.findViewById(R.id.txtEmail);
        }
    }
}