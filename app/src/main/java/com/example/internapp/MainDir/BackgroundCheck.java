package com.example.internapp.MainDir;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.internapp.R;
import com.example.internapp.VideoCall.DataModelType;
import com.example.internapp.VideoCall.MainRepository;
import com.example.internapp.VideoCall.VideoCallActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * The BackgroundCheck class is a Service that runs in the background and checks for incoming video call events.
 * It listens for data changes in the Firebase Realtime Database and displays a notification when a call is received.
 */
public class BackgroundCheck extends Service {
    private static boolean isRunning = false;
    private static boolean madeNotification = false;
    private MainRepository mainRepository;
    private FirebaseUser firebaseUser;

    /**
     * Returns whether the BackgroundCheck service is currently running.
     *
     * @return true if the service is running, false otherwise.
     */
    public static boolean isServiceRunning() {
        return !isRunning;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        madeNotification = false;
        Log.e("service", "service started");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Create a PendingIntent to launch the VideoCallActivity when the notification is clicked
        Intent call = new Intent(this, VideoCallActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, call, PendingIntent.FLAG_IMMUTABLE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mainRepository = MainRepository.getInstance();

        // Retrieve the user's username from the Firebase Realtime Database
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/HRs");
        reference.orderByKey().equalTo(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String username = (snap.child("username").getValue()).toString();
                        Log.e("service", username);
                        // Log in with the retrieved username
                        mainRepository.login(username, getApplicationContext(), () -> {
                        });
                    }

                } else {
                    // If the user is not found in the "HRs" node, check the "Students" node
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

            Handler handler = new Handler(Looper.getMainLooper());
            Runnable hand = new Runnable() {
                @Override
                public void run() {
                    if(!madeNotification){
                        try {
                            Log.e("service", "service running");
                            // Subscribe to the latest event from the MainRepository
                            mainRepository.subscribeForLatestEvent(data -> {
                                // Check if the event type is StartCall
                                if (data.getType() == DataModelType.StartCall) {
                                    Log.e("service", data.getSender() + " is calling you");
                                    // Display a notification when a call is received
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    String text = data.getSender() + " is calling you";
                                    Notification notification = new Notification.Builder(getApplicationContext(), "1")
                                            .setContentText(text)
                                            .setSmallIcon(R.drawable.ic_launcher)
                                            .setContentIntent(pendingIntent)
                                            .setAutoCancel(true) // this will close the notification after it's clicked
                                            .build();

                                    notificationManager.notify(1, notification);
                                    madeNotification = true;
                                }
                            });
                        } catch (Exception e) {
                            Log.e("service", Objects.requireNonNull(e.getMessage()));
                        }
                    }

                    handler.postDelayed(this, 1000);
                }
            };
            handler.postDelayed(hand, 100);
        }
        return START_STICKY;
    }

    /**
     * Called when the service is being destroyed.
     * Resets the isRunning and madeNotification flags.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        madeNotification = false;
        Log.e("service", "service stopped");
    }
}