package com.example.internapp.HRDir;

import static com.example.internapp.HRDir.SearchActivity.filtersFaculties;
import static com.example.internapp.HRDir.SearchActivity.filtersUniversities;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.internapp.R;

import java.util.ArrayList;

/**
 * Adapter for displaying a list of universities in a RecyclerView.
 */
public class UniversitiesRecViewAdapter extends RecyclerView.Adapter<UniversitiesRecViewAdapter.ViewHolder> {

    private final ArrayList<University> universitiesList;
    private final ArrayList<University> facultiesList;
    private ArrayList<University> universities = new ArrayList<>();

    /**
     * Constructs a new UniversitiesRecViewAdapter with the provided lists of universities and faculties.
     *
     * @param universitiesList The list of universities.
     * @param facultiesList    The list of faculties.
     */
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

        // Highlight universities based on applied filters
        if (filtersUniversities.contains(universities.get(position).getName()) || filtersFaculties.contains(universities.get(position).getName())) {
            holder.txtName.setTextColor(holder.itemView.getResources().getColor(R.color.white, null));
            holder.parent.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.blue, null));
        } else {
            holder.txtName.setTextColor(holder.itemView.getResources().getColor(R.color.black, null));
            holder.parent.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.lightblue, null));
        }

        // Click listener to handle chip creation and highlighting
        holder.parent.setOnClickListener(v -> {
            String text = universities.get(holder.getAdapterPosition()).getName();
            if (filtersUniversities.contains(text) || filtersFaculties.contains(text)) {
                // Remove chip and update UI
                SearchActivity.createChip(facultiesList, universitiesList, text, v, SearchActivity.uniAdapter, SearchActivity.facultyAdapter);
                holder.txtName.setTextColor(holder.itemView.getResources().getColor(R.color.black, null));
                holder.parent.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.lightblue, null));
            } else {
                // Add chip and update UI
                SearchActivity.createChip(facultiesList, universitiesList, text, v, SearchActivity.uniAdapter, SearchActivity.facultyAdapter);
                holder.txtName.setTextColor(holder.itemView.getResources().getColor(R.color.white, null));
                holder.parent.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.blue, null));
            }
        });

        RequestBuilder<Drawable> requestBuilder = Glide.with(holder.itemView.getContext()).asDrawable().sizeMultiplier(0.1f);

        Glide.with(holder.image.getContext()).load(universities.get(position).getImageUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(requestBuilder).fitCenter().centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return universities.size();
    }

    /**
     * Sets the list of universities to be displayed.
     *
     * @param universities The list of universities to be set.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setUniversities(final ArrayList<University> universities) {
        this.universities = universities;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class to hold the views for each item in the RecyclerView.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtName;
        private final CardView parent;
        private final ImageView image;

        /**
         * Constructs a new ViewHolder.
         *
         * @param itemView The view corresponding to the item layout.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            image = itemView.findViewById(R.id.universityImg);
            parent = itemView.findViewById(R.id.parent);
        }
    }
}
