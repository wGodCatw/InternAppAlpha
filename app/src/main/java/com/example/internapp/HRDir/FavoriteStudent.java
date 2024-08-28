package com.example.internapp.HRDir;

import androidx.annotation.NonNull;

/**
 * Represents a favorite student with name, email, username and image URL.
 */
public class FavoriteStudent {
    private final String imageUrl;
    private String name;
    private String email;
    private String username;

    /**
     * Constructs a new FavoriteStudent object with the given name, email, and image URL.
     *
     * @param name     The name of the favorite student.
     * @param email    The email of the favorite student.
     * @param imageUrl The URL of the image associated with the favorite student.
     * @param username The username of the favorite student.
     */
    public FavoriteStudent(String name, String email, String imageUrl, String username) {
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.username = username;
    }

    /**
     * Retrieves the name of the favorite student.
     *
     * @return The name of the favorite student.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the favorite student.
     *
     * @param name The new name to set for the favorite student.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the email of the favorite student.
     *
     * @return The email of the favorite student.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the favorite student.
     *
     * @param email The new email to set for the favorite student.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the URL of the image associated with the favorite student.
     *
     * @return The URL of the image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "FavoriteStudent{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    /**
     * Retrieves the username of the favorite student.
     *
     * @return The username of the favorite student.
     */
    public String getUsername() {
        return username;
    }
}