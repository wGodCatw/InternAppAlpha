package com.example.internapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    private final ArrayList<ViewPagerItem> viewPagerItemArrayList;
    private final Context context;
    private final Activity a;


    public ViewPagerAdapter(Activity a, final ArrayList<ViewPagerItem> viewPagerItemArrayList, Context context) {
        this.viewPagerItemArrayList = viewPagerItemArrayList;
        this.a = a;
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

        //check if student is already in favorites
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

        holder.txtUsername.setText("@" + viewPagerItem.getUsername());
        holder.txtName.setText(viewPagerItem.getName());
        holder.txtFaculty.setText(viewPagerItem.getFaculty());
        holder.txtUniversity.setText(viewPagerItem.getUniversity());
        holder.whatsappLink.setOnClickListener(v -> {
            String url = "https://api.whatsapp.com/send?phone=" + viewPagerItem.getWhatsappNumber();
            try {
                PackageManager pm = context.getApplicationContext().getPackageManager();
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                a.startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                a.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

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

        Glide.with(context).asBitmap().load(viewPagerItemArrayList.get(position).getImageUrl()).into(holder.imgStudentSearch);
    }

    @Override
    public int getItemCount() {
        return viewPagerItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgStudentSearch, whatsappLink, addToFavorites;
        private final TextView txtName, txtFaculty, txtUsername, txtUniversity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

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
