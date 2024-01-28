package com.example.internapp;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

public class SpeedDialinit {
    public static void fab_init(SpeedDialView fab, Context context, Activity activity) {

        fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_settings, R.drawable.ic_settings)
                .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.lightblue, context.getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(context.getResources(), R.color.white, context.getTheme())).create());

        fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_home, R.drawable.ic_home)
                .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.lightblue, context.getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(context.getResources(), R.color.white, context.getTheme())).create());

        fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_favorites, R.drawable.ic_favorites)
                .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.lightblue, context.getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(context.getResources(), R.color.white, context.getTheme())).create());

        fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_schedule, R.drawable.ic_schedule)
                .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.lightblue, context.getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(context.getResources(), R.color.white, context.getTheme())).create());

        fab.addActionItem(new SpeedDialActionItem.Builder(R.id.action_search, R.drawable.ic_search)
                .setFabBackgroundColor(ResourcesCompat.getColor(context.getResources(), R.color.lightblue, context.getTheme()))
                .setFabImageTintColor(ResourcesCompat.getColor(context.getResources(), R.color.white, context.getTheme())).create());

        fab.inflate(R.menu.main_menu);
        fab.setExpansionMode(SpeedDialView.ExpansionMode.LEFT);
        fab.setUseReverseAnimationOnClose(true);
        fab.getMainFab().setCustomSize(200);
        fab.setMainFabAnimationRotateAngle(270f);
        fab.setOrientation(LinearLayout.VERTICAL);
        fab.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                if (actionItem.getId() == R.id.action_settings) {
                    fab.close(true);
                    Intent intent = new Intent(context, SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(context, intent, null);
                    activity.finish();
                } else if (actionItem.getId() == R.id.action_home) {
                    if (activity.getClass() == HomepageActivity.class)
                        Toast.makeText(context, "You're here already!", Toast.LENGTH_SHORT).show();
                    else {
                        fab.close(true);
                        Intent intent = new Intent(context, HomepageActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(context, intent, null);
                        activity.finish();
                    }
                } else if (actionItem.getId() == R.id.action_favorites) {
                    if (activity.getClass() == FavoritesActivity.class)
                        Toast.makeText(context, "You're here already!", Toast.LENGTH_SHORT).show();
                    else {
                        fab.close(true);
                        Intent intent = new Intent(context, FavoritesActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(context, intent, null);
                    }
                } else if (actionItem.getId() == R.id.action_schedule) {
                    Toast.makeText(context, "You clicked: " + actionItem.getLabel(context), Toast.LENGTH_SHORT).show();
                    fab.close(true);
                } else if (actionItem.getId() == R.id.action_search) {
                    if (activity.getClass() == SearchActivity.class)
                        Toast.makeText(context, "You're here already!", Toast.LENGTH_SHORT).show();
                    else {
                        fab.close(true);
                        Intent intent = new Intent(context, SearchActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(context, intent, null);
                    }
                }
                return true;
            }
        });
    }
}
