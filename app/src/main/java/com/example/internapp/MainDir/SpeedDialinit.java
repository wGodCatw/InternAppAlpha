package com.example.internapp.MainDir;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.widget.ActionMenuView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.internapp.HRDir.FavoritesActivity;
import com.example.internapp.HRDir.SearchActivity;
import com.example.internapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

/**
 * Utility class for initializing a speed dial menu.
 */
public class SpeedDialinit {

    /**
     * Initializes the speed dial menu.
     *
     * @param fab      The SpeedDialView object to initialize.
     * @param context  The context of the calling activity.
     * @param activity The activity calling this method.
     */
    public static void fab_init(SpeedDialView fab, Context context, Activity activity) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Registered users/Students/" + firebaseUser.getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_settings, R.drawable.ic_settings).setLabel("Settings")
                            .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.white, context.getTheme()))
                            .setFabImageTintColor(ResourcesCompat.getColor(context.getResources(), R.color.orange, context.getTheme())).create());

                    fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_home, R.drawable.ic_home).setLabel("Home")
                            .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.white, context.getTheme()))
                            .setFabImageTintColor(ResourcesCompat.getColor(context.getResources(), R.color.orange, context.getTheme())).create());


                    fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_projects, R.drawable.ic_project).setLabel("Projects")
                            .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.white, context.getTheme()))
                            .setFabImageTintColor(ResourcesCompat.getColor(context.getResources(), R.color.orange, context.getTheme())).create());


                } else {

                    fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_settings, R.drawable.ic_settings).setLabel("Settings")
                            .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.white, context.getTheme()))
                            .setFabImageTintColor(ResourcesCompat.getColor(context.getResources(), R.color.orange, context.getTheme())).create());

                    fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_home, R.drawable.ic_home).setLabel("Home")
                            .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.white, context.getTheme()))
                            .setFabImageTintColor(ResourcesCompat.getColor(context.getResources(), R.color.orange, context.getTheme())).create());

                    fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_favorites, R.drawable.ic_favorites).setLabel("Favorite students")
                            .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.white, context.getTheme()))
                            .setFabImageTintColor(ResourcesCompat.getColor(context.getResources(), R.color.orange, context.getTheme())).create());

                    fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_search, R.drawable.ic_search).setLabel("Search")
                            .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.white, context.getTheme()))
                            .setFabImageTintColor(ResourcesCompat.getColor(context.getResources(), R.color.orange, context.getTheme())).create());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Adding action items to the speed dial menu


        // Retrieving action menu view and inflating the menu
        ActionMenuView actionMenuView = activity.findViewById(R.id.amv);
        Menu amv_menu = actionMenuView.getMenu();
        activity.getMenuInflater().inflate(R.menu.main_menu, amv_menu);

        // Setting properties for the speed dial menu
        fab.setExpansionMode(SpeedDialView.ExpansionMode.LEFT);
        fab.setUseReverseAnimationOnClose(true);
        fab.getMainFab().setCustomSize(200);
        fab.setMainFabAnimationRotateAngle(270f);
        fab.setOrientation(LinearLayout.VERTICAL);

        // Handling action selection
        fab.setOnActionSelectedListener(actionItem -> {
            // Check which action item is selected
            if (actionItem.getId() == R.id.action_settings) {
                // Check if already on SettingsActivity
                if (activity.getClass() == SettingsActivity.class)
                    Toast.makeText(context, "You're here already!", Toast.LENGTH_SHORT).show();
                else {
                    fab.close(true);
                    // Start SettingsActivity
                    Intent intent = new Intent(context, SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ContextCompat.startActivity(context, intent, null);
                }
            } else if (actionItem.getId() == R.id.action_home) {
                // Check if already on UserProfileActivity
                if (activity.getClass() == UserProfileActivity.class)
                    Toast.makeText(context, "You're here already!", Toast.LENGTH_SHORT).show();
                else {
                    fab.close(true);
                    // Start UserProfileActivity
                    Intent intent = new Intent(context, UserProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    ContextCompat.startActivity(context, intent, null);
                    activity.finish();
                }
            } else if (actionItem.getId() == R.id.action_favorites) {
                // Check if already on FavoritesActivity
                if (activity.getClass() == FavoritesActivity.class)
                    Toast.makeText(context, "You're here already!", Toast.LENGTH_SHORT).show();
                else {
                    fab.close(true);
                    // Start FavoritesActivity
                    Intent intent = new Intent(context, FavoritesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ContextCompat.startActivity(context, intent, null);
                }
            } else if (actionItem.getId() == R.id.action_projects) {
                // Check if already on UploadProjectActivity
                if (activity.getClass() == UploadProjectActivity.class)
                    Toast.makeText(context, "You're here already!", Toast.LENGTH_SHORT).show();
                else {
                    fab.close(true);
                    // Start UploadProjectActivity
                    Intent intent = new Intent(context, UploadProjectActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ContextCompat.startActivity(context, intent, null);
                }
            } else if (actionItem.getId() == R.id.action_search) {
                // Check if already on SearchActivity
                if (activity.getClass() == SearchActivity.class)
                    Toast.makeText(context, "You're here already!", Toast.LENGTH_SHORT).show();
                else {
                    fab.close(true);
                    // Start SearchActivity
                    Intent intent = new Intent(context, SearchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ContextCompat.startActivity(context, intent, null);
                }
            }
            return true;
        });
    }
}
