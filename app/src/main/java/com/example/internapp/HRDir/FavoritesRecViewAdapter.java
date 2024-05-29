package com.example.internapp.HRDir;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
import com.example.internapp.StudentDir.StudentProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Adapter class for the RecyclerView displaying the list of favorite students.
 */
public class FavoritesRecViewAdapter extends RecyclerView.Adapter<FavoritesRecViewAdapter.ViewHolder> {

    private final Context context;
    private ArrayList<FavoriteStudent> favoriteStudents = new ArrayList<>();

    /**
     * Constructor for the FavoritesRecViewAdapter.
     *
     * @param context The context in which the adapter is being used.
     */
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
        // Bind the data from the FavoriteStudent object to the ViewHolder
        holder.txtName.setText(favoriteStudents.get(holder.getAdapterPosition()).getName());
        holder.txtEmail.setText(favoriteStudents.get(holder.getAdapterPosition()).getEmail());
        Log.e("HERE", favoriteStudents.get(holder.getAdapterPosition()).getUsername());
        holder.txtUsername.setText("@" + favoriteStudents.get(position).getUsername());

        // Set an OnClickListener for the CardView
        holder.parent.setOnClickListener(v -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
            reference.orderByChild("username").equalTo(favoriteStudents.get(position).getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
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

        holder.isFavoriteButton.setOnClickListener(v -> {

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/HRs/" + firebaseUser.getUid());
            reference.child("favoriteStudents").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String favoriteStr = (String) snapshot.getValue();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered users/Students");
                        ref.orderByChild("username").equalTo(favoriteStudents.get(holder.getAdapterPosition()).getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String UID = "";
                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                        UID = snap.getKey();
                                    }
                                    ArrayList<String> favoritesList = new ArrayList<>(Arrays.asList(favoriteStr.split(",")));


                                    favoriteStudents.remove(holder.getAdapterPosition());
                                    notifyItemRemoved(holder.getAdapterPosition());

                                    favoritesList.remove(UID);


                                    String newFavoriteStr = String.join(",", favoritesList);
                                    reference.child("favoriteStudents").setValue(newFavoriteStr);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    } else {
                        //get UID of the student from username of the viewpager item
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
                        reference.orderByChild("username").equalTo(favoriteStudents.get(holder.getAdapterPosition()).getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String UID = "";
                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                        UID = snap.getKey();
                                    }
                                    DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Registered users/HRs/" + firebaseUser.getUid());
                                    referenceUser.child("favoriteStudents").setValue(UID);
                                    holder.isFavoriteButton.setImageResource(R.drawable.ic_favorites_added);
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
        });


        // Load the image using Glide library
        if (!favoriteStudents.get(position).getImageUrl().equals("none")) {
            RequestBuilder<Drawable> requestBuilder = Glide.with(holder.itemView.getContext()).asDrawable().sizeMultiplier(0.1f);
            Glide.with(context).load(favoriteStudents.get(holder.getAdapterPosition()).getImageUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(requestBuilder).fitCenter().centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return favoriteStudents.size();
    }

    /**
     * Set the list of favorite students to be displayed in the RecyclerView.
     *
     * @param favoriteStudents The list of FavoriteStudent objects to be displayed.
     */
    public void setFavoriteStudents(ArrayList<FavoriteStudent> favoriteStudents) {
        this.favoriteStudents = favoriteStudents;
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

        private final ImageView isFavoriteButton;

        /**
         * Constructor for the ViewHolder.
         *
         * @param itemView The View representing the individual item in the RecyclerView.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            isFavoriteButton = itemView.findViewById(R.id.isFavoriteButton);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtName = itemView.findViewById(R.id.txtName);
            image = itemView.findViewById(R.id.universityImg);
            parent = itemView.findViewById(R.id.parent);
            txtEmail = itemView.findViewById(R.id.txtEmail);
        }
    }
}