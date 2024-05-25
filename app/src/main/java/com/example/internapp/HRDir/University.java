package com.example.internapp.HRDir;

import androidx.annotation.NonNull;

/**
 * Represents a university entity with a name and an image URL.
 */
public class University {
    private final String imageUrl;
    private String name;

    /**
     * Constructs a University object with the given name and image URL.
     *
     * @param name     The name of the university.
     * @param imageUrl The URL of the university's image.
     */
    public University(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    /**
     * Retrieves the name of the university.
     *
     * @return The name of the university.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the university.
     *
     * @param name The new name for the university.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the URL of the university's image.
     *
     * @return The URL of the university's image.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Returns a string representation of the University object.
     *
     * @return A string containing the name and image URL of the university.
     */
    @NonNull
    @Override
    public String toString() {
        return "University{" + "name='" + name + '\'' + ", imageUrl='" + imageUrl + '\'' + '}';
    }
}
