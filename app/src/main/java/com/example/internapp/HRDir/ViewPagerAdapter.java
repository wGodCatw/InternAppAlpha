package com.example.internapp.HRDir;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.internapp.R;
import com.example.internapp.StudentDir.StudentProfileActivity;
import com.example.internapp.StudentDir.StudentProjectsActivity;
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
 * Adapter for ViewPager in StudentProfileActivity.
 */
public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    private final ArrayList<ViewPagerItem> viewPagerItemArrayList;
    private final Context context;
    private final Activity activity;

    /**
     * Constructor for ViewPagerAdapter.
     *
     * @param activity               The activity context.
     * @param viewPagerItemArrayList The list of ViewPagerItems.
     * @param context                The context.
     */
    public ViewPagerAdapter(Activity activity, final ArrayList<ViewPagerItem> viewPagerItemArrayList, Context context) {
        this.viewPagerItemArrayList = viewPagerItemArrayList;
        this.activity = activity;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewpager_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final ViewPagerItem viewPagerItem = viewPagerItemArrayList.get(position);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Check if student is already in favorites
        assert firebaseUser != null;
        DatabaseReference referenceImage = FirebaseDatabase.getInstance().getReference("Registered users/HRs/" + firebaseUser.getUid());
        referenceImage.child("favoriteStudents").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String favoriteStr = (String) snapshot.getValue();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered users/Students");
                    ref.orderByChild("username").equalTo(viewPagerItem.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String UID = "";
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    UID = snap.getKey();
                                }
                                assert favoriteStr != null;
                                ArrayList<String> favoritesList = new ArrayList<>(Arrays.asList(favoriteStr.split(",")));
                                if (favoritesList.contains(UID)) {
                                    holder.addToFavorites.setImageResource(R.drawable.ic_favorites_added);
                                } else {
                                    holder.addToFavorites.setImageResource(R.drawable.ic_favorites);
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

        // Set text data for ViewPagerItem
        holder.txtUsername.setText("@" + viewPagerItem.getUsername());
        holder.txtName.setText(viewPagerItem.getName());
        holder.txtFaculty.setText(viewPagerItem.getFaculty());
        holder.txtUniversity.setText(viewPagerItem.getUniversity());
        holder.btnProjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, StudentProjectsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("username", viewPagerItem.getUsername());
                context.startActivity(intent);
            }
        });


        // Handle profile button click
        holder.btnProfile.setOnClickListener(v -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
            reference.orderByChild("username").equalTo(viewPagerItem.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
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

        // Handle WhatsApp link click
        holder.whatsappLink.setOnClickListener(v -> {
            // Open WhatsApp chat with student
            String url = "https://api.whatsapp.com/send?phone=" + viewPagerItem.getWhatsappNumber();
            try {
                PackageManager pm = context.getPackageManager();
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                activity.startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

        // Handle favorites button click
        holder.addToFavorites.setOnClickListener(v -> {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/HRs/" + firebaseUser.getUid());
            reference.child("favoriteStudents").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String favoriteStr = (String) snapshot.getValue();
                        if (favoriteStr.equals("")) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
                            reference.orderByChild("username").equalTo(viewPagerItem.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String UID = "";
                                        for (DataSnapshot snap : snapshot.getChildren()) {
                                            UID = snap.getKey();
                                        }
                                        DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Registered users/HRs/" + firebaseUser.getUid());
                                        referenceUser.child("favoriteStudents").setValue(UID);
                                        holder.addToFavorites.setImageResource(R.drawable.ic_favorites_added);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered users/Students");
                            ref.orderByChild("username").equalTo(viewPagerItem.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String UID = "";
                                        for (DataSnapshot snap : snapshot.getChildren()) {
                                            UID = snap.getKey();
                                        }
                                        ArrayList<String> favoritesList = new ArrayList<>(Arrays.asList(favoriteStr.split(",")));
                                        if (favoritesList.contains(UID)) {
                                            holder.addToFavorites.setImageResource(R.drawable.ic_favorites);
                                            favoritesList.remove(UID);
                                        } else {
                                            holder.addToFavorites.setImageResource(R.drawable.ic_favorites_added);
                                            favoritesList.add(UID);

                                        }

                                        String newFavoriteStr = String.join(",", favoritesList);
                                        reference.child("favoriteStudents").setValue(newFavoriteStr);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    } else {
                        //get UID of the student from username of the viewpager item
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
                        reference.orderByChild("username").equalTo(viewPagerItem.getUsername()).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String UID = "";
                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                        UID = snap.getKey();
                                    }
                                    DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference("Registered users/HRs/" + firebaseUser.getUid());
                                    referenceUser.child("favoriteStudents").setValue(UID);
                                    holder.addToFavorites.setImageResource(R.drawable.ic_favorites_added);
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

        // Load student image if available
        if (!viewPagerItemArrayList.get(position).getImageUrl().equals("none")) {
            RequestBuilder<Drawable> requestBuilder = Glide.with(holder.itemView.getContext()).asDrawable().sizeMultiplier(0.1f);
            Glide.with(context).load(viewPagerItemArrayList.get(position).getImageUrl()).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(requestBuilder).fitCenter().centerCrop().transition(DrawableTransitionOptions.withCrossFade()).into(holder.imgStudentSearch);

        }
    }

    @Override
    public int getItemCount() {
        return viewPagerItemArrayList.size();
    }

    /**
     * ViewHolder class for ViewPagerAdapter.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgStudentSearch, whatsappLink, addToFavorites, btnProfile, btnProjects;
        private final TextView txtName, txtFaculty, txtUsername, txtUniversity;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            btnProfile = itemView.findViewById(R.id.btnProfile);
            btnProjects = itemView.findViewById(R.id.btnProjects);
            addToFavorites = itemView.findViewById(R.id.addToFavorites);
            txtUniversity = itemView.findViewById(R.id.studentUniversitySearch);
            whatsappLink = itemView.findViewById(R.id.whatsappLink);
            imgStudentSearch = itemView.findViewById(R.id.studentPicSearch);
            txtName = itemView.findViewById(R.id.studentNameSearch);
            txtFaculty = itemView.findViewById(R.id.studentFacultySearch);
            txtUsername = itemView.findViewById(R.id.username);
        }
    }
}
