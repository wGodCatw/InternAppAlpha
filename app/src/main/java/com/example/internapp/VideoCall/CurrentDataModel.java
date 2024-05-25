package com.example.internapp.VideoCall;

public class CurrentDataModel {
    private String currentUser;
    private String targetUser;
    private DataModelType callStatus;

    public CurrentDataModel(String currentUser, String targetUser, DataModelType callStatus) {
        this.currentUser = currentUser;
        this.targetUser = targetUser;
        this.callStatus = callStatus;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public DataModelType getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(DataModelType callStatus) {
        this.callStatus = callStatus;
    }
}
