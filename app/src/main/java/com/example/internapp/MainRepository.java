package com.example.internapp;

public class MainRepository {
    private final FirebaseClient firebaseClient;
    private String currentUsername;
    private MainRepository(){
        this.firebaseClient = new FirebaseClient();
    }

    private static MainRepository instance;

    public static MainRepository getInstance() {
        if(instance == null){
            instance = new MainRepository();
        }
        return instance;
    }

    public void sendCallRequest(String target, ErrorCallback errorCallback){
        firebaseClient.sendMessageToOtherUser(new DataModel(target, currentUsername, "", DataModelType.StartCall), errorCallback);
    }

    public void subscribeForLatestEvent(NewEventCallback callback){
        firebaseClient.observeIncomingLatestEvent(model -> {
            switch (model.getType()){
                case Offer:

                    break;
                case Answer:

                    break;
                case  IceCandidate:

                    break;
                case StartCall:
                    callback.onNewEventReceived(model);
                    break;
            }
        });
    }
}
