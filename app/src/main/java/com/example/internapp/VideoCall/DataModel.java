package com.example.internapp.VideoCall;

/**
 * The DataModel class represents a data model that holds information about a message or event.
 * It includes the target recipient, sender, data payload, and the type of the data model.
 */
public class DataModel {
    private String target;
    private String sender;
    private String data;
    private DataModelType type;

    /**
     * Constructor for the DataModel class.
     *
     * @param target The target recipient of the data model.
     * @param sender The sender of the data model.
     * @param data   The data payload of the data model.
     * @param type   The type of the data model.
     */
    public DataModel(String target, String sender, String data, DataModelType type) {
        this.target = target;
        this.sender = sender;
        this.data = data;
        this.type = type;
    }

    /**
     * Gets the target recipient of the data model.
     *
     * @return The target recipient.
     */
    public String getTarget() {
        return target;
    }

    /**
     * Sets the target recipient of the data model.
     *
     * @param target The target recipient.
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Gets the sender of the data model.
     *
     * @return The sender.
     */
    public String getSender() {
        return sender;
    }

    /**
     * Sets the sender of the data model.
     *
     * @param sender The sender.
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Gets the data payload of the data model.
     *
     * @return The data payload.
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the data payload of the data model.
     *
     * @param data The data payload.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Gets the type of the data model.
     *
     * @return The type of the data model.
     */
    public DataModelType getType() {
        return type;
    }

    /**
     * Sets the type of the data model.
     *
     * @param type The type of the data model.
     */
    public void setType(DataModelType type) {
        this.type = type;
    }
}