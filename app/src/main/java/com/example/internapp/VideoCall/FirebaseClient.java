package com.example.internapp.VideoCall;

import androidx.annotation.NonNull;

import com.example.internapp.VideoCall.DataModel;
import com.example.internapp.VideoCall.ErrorCallback;
import com.example.internapp.VideoCall.NewEventCallback;
import com.example.internapp.VideoCall.SuccessCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * Provides methods to interact with Firebase Realtime Database.
 */
public class FirebaseClient {

    private static final String LATEST_EVENT_FIELD_NAME = "latest_event";
    private final Gson gson = new Gson();
    private final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
    private String currentUsername;

    /**
     * Logs in the user with the specified username.
     *
     * @param username The username of the user to log in.
     * @param callBack Callback for successful login.
     */
    public void login(String username, SuccessCallback callBack) {
        dbRef.child(username).setValue("").addOnCompleteListener(task -> {
            currentUsername = username;
            callBack.onSuccess();
        });
    }

    /**
     * Sends a message to another user.
     *
     * @param dataModel     The data model containing the message and target user.
     * @param errorCallBack Callback for error in sending message.
     */
    public void sendMessageToOtherUser(DataModel dataModel, ErrorCallback errorCallBack) {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(dataModel.getTarget()).exists()) {
                    //send the signal to other user
                    dbRef.child(dataModel.getTarget()).child(LATEST_EVENT_FIELD_NAME)
                            .setValue(gson.toJson(dataModel));

                } else {
                    errorCallBack.onError();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorCallBack.onError();
            }
        });
    }

    /**
     * Observes incoming latest event.
     *
     * @param callBack Callback for receiving new events.
     */
    public void observeIncomingLatestEvent(NewEventCallback callBack) {
        dbRef.child(currentUsername).child(LATEST_EVENT_FIELD_NAME).addValueEventListener(
                new ValueEventListener() {
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
                }
        );
    }
}