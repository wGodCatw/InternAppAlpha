package com.example.internapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Objects;

public class FirebaseClient {

    private static final String LATEST_EVENT_FIELD_NAME = "latest_event";
    private final Gson gson = new Gson();
    private DatabaseReference dbRef;
    private DatabaseReference selfDbRef;
    private String currentUsername;
    private String UID;


    public void login(String username, SuccessCallback callBack) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/HRs");
        reference.orderByChild("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        selfDbRef = reference;
                        UID = snap.getKey();
                        currentUsername = UID;
                        Log.e("UID", UID);
                        callBack.onSuccess();
                    }

                } else {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered users/Students");

                    reference.orderByChild("username").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    selfDbRef = reference;
                                    UID = snap.getKey();
                                    currentUsername = UID;
                                    Log.e("UID", UID);
                                    callBack.onSuccess();
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

    public void sendMessageToOtherUser(DataModel dataModel, ErrorCallback errorCallBack) {
        dbRef = FirebaseDatabase.getInstance().getReference().child("Registered users/HRs");
        dbRef.orderByKey().equalTo(dataModel.getTarget()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //send the signal to other user
                    dbRef.child(UID).child(LATEST_EVENT_FIELD_NAME).setValue(gson.toJson(dataModel));
                } else {
                    dbRef = FirebaseDatabase.getInstance().getReference().child("Registered users/Students");
                    dbRef.orderByKey().equalTo(dataModel.getTarget()).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                //send the signal to other user
                                dbRef.child(UID).child(LATEST_EVENT_FIELD_NAME).setValue(gson.toJson(dataModel));
                            } else {
                                errorCallBack.onError();
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
                errorCallBack.onError();
            }

        });
    }

    public void observeIncomingLatestEvent(NewEventCallback callBack) {
        selfDbRef.child(LATEST_EVENT_FIELD_NAME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String data = Objects.requireNonNull(snapshot.getValue()).toString();
                    DataModel dataModel = gson.fromJson(data, DataModel.class);
                    callBack.onNewEventReceived(dataModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
