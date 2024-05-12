package com.example.internapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Adapter class for the RecyclerView displaying the list of favorite students.
 */
public class ProjectsRecViewAdapter extends RecyclerView.Adapter<ProjectsRecViewAdapter.ViewHolder> {

    private final Context context;
    static ArrayList<Project> projects = new ArrayList<>();

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

        // Load the image using Picasso library
        if (!projects.get(position).getImageUrl().equals("none")) {
            Picasso.get().load(projects.get(position).getImageUrl()).into(holder.image);

        }
    }

    public void onButtonShowPopupWindowClick(View view, String text) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View popupView = inflater.inflate(R.layout.popup_window, null);

        TextView descriptionView = popupView.findViewById(R.id.descriptionText);
        descriptionView.setText(text);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
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
        notifyDataSetChanged();
    }

    public boolean containsProject(Project project) {
        return projects.contains(project);
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

