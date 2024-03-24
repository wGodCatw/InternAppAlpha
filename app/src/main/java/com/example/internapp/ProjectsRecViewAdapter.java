package com.example.internapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter class for the RecyclerView displaying the list of favorite students.
 */
public class ProjectsRecViewAdapter extends RecyclerView.Adapter<ProjectsRecViewAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Project> projects = new ArrayList<>();

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // Bind the data from the Project object to the ViewHolder
        holder.txtTitle.setText(projects.get(position).getTitle());
        holder.txtDescription.setText(projects.get(position).getDescription());

        // Set an OnClickListener for the CardView
        holder.parent.setOnClickListener(v -> {
            //open link
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(projects.get(position).getLink()));
            context.startActivity(intent);
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
     * Set the list of projects to be displayed in the RecyclerView.
     *
     * @param project The Project object to be displayed.
     */
    public void addProject(Project project) {
        this.projects.add(project);
        notifyItemChanged(0);
    }

    /**
     * ViewHolder class for the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtDescription;
        private final TextView txtTitle;
        private final CardView parent;
        private final ImageView image;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The View representing the individual item in the RecyclerView.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            image = itemView.findViewById(R.id.projectImg);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}