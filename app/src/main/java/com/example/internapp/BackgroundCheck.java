package com.example.internapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class BackgroundCheck extends Service {
    private static boolean isRunning = false;
    private MainRepository mainRepository;
    private FirebaseUser firebaseUser;

    public static boolean isServiceRunning() {
        return isRunning;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("service", "service started");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent call = new Intent(this, VideoCallActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, call, PendingIntent.FLAG_IMMUTABLE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mainRepository = MainRepository.getInstance();



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/HRs");
        reference.orderByKey().equalTo(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String username = Objects.requireNonNull(snap.child("username").getValue()).toString();
                        Log.e("service", username);
                        mainRepository.login(username, getApplicationContext(), () -> {
                        });
                    }

                } else {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");
                    reference.orderByKey().equalTo(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snap : snapshot.getChildren()) {
                                String username = Objects.requireNonNull(snap.child("username").getValue()).toString();
                                Log.e("service", username);
                                mainRepository.login(username, getApplicationContext(), () -> {
                                });
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

        if (!isRunning) {
            isRunning = true;

            Handler handler = new Handler();
            Runnable hand = new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.e("service", "service running");
                        mainRepository.subscribeForLatestEvent(data -> {
                            if (data.getType() == DataModelType.StartCall) {
                                Log.e("service", data.getSender() + " is calling you");
                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                String text = data.getSender() + " is calling you";
                                Notification notification = new Notification.Builder(getApplicationContext(), "1")
                                        .setContentText(text)
                                        .setSmallIcon(R.drawable.ic_launcher)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true) // this will close the notification after it's clicked
                                        .build();

                                notificationManager.notify(1, notification);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    handler.postDelayed(this, 1000);
                }
            };
            handler.postDelayed(hand, 100);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        Log.e("service", "service stopped");
    }
}
