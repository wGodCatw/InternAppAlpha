package com.example.internapp.StudentDir;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.internapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Adapter class for the RecyclerView displaying the list of favorite students.
 */
public class ProjectsRecViewAdapter extends RecyclerView.Adapter<ProjectsRecViewAdapter.ViewHolder> {

    public static ArrayList<Project> projects = new ArrayList<>();
    private static FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private final Context context;

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
//        holder.txtDescription.setText(projects.get(position).getDescription());

        String description = projects.get(position).getDescription();

        holder.txtDescription.setOnClickListener(v -> onButtonShowPopupWindowClick(v, description));

        // Set an OnClickListener for the CardView
        holder.parent.setOnClickListener(v -> {
            String url = projects.get(position).getLink();
            Log.d("URL", "Project URL: " + url);
            if (!url.equals("")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No application found to open the link", Toast.LENGTH_SHORT).show();
                }

            } else {
                Log.e("URL Error", "URL is empty or invalid");
            }

        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered users/Students/" + firebaseUser.getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.parent.setOnLongClickListener(v -> {
                        new AlertDialog.Builder(context).setTitle("Delete Project").setMessage("Do you want to delete this project?").setPositiveButton("Yes", (dialog, which) -> {
                            Project projectToRemove = projects.get(holder.getAdapterPosition());
                            holder.removeProjectFromFirebase(projectToRemove);
                            projects.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                        }).setNegativeButton("No", null).show();
                        return true; // Indicates the callback consumed the long click
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Set long click listener on the parent CardView

        // Load the image using Picasso library
        if (!projects.get(position).getImageUrl().equals("none")) {
            RequestBuilder<Drawable> requestBuilder = Glide.with(holder.itemView.getContext()).asDrawable().sizeMultiplier(0.1f);
            Glide.with(context).load(projects.get(position).getImageUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(requestBuilder).fitCenter().centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(holder.image);
        }
    }

    public void onButtonShowPopupWindowClick(View view, String text) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);

        TextView descriptionView = popupView.findViewById(R.id.descriptionText);
        descriptionView.setText(text);

        // Create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // Find the close button inside the popup layout
        ImageView closeButton = popupView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> popupWindow.dismiss());

        // Show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
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
        projects.add(project);
        notifyDataSetChanged();
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

        private void removeProjectFromFirebase(Project projectToRemove) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered users/Students/" + firebaseUser.getUid());
            ref.orderByChild("username").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Projects/" + username);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                        Project project = snap.getValue(Project.class);
                                        if (project != null && project.equals(projectToRemove)) {
                                            snap.getRef().removeValue();
                                            break;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}
