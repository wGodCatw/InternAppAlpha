package com.example.internapp;

/**
 * Represents a project of the student with title, link, description and image URL.
 */
public class Project {
    private final String imageUrl;
    private final String title;

    private final String description;
    private final String link;

    /**
     * Constructs a new Project object with the given title, description, link, and image URL.
     *
     * @param title       The name of the project.
     * @param link        The link to the project itself.
     * @param imageUrl    The URL of the image associated with the project.
     * @param description The description of the project.
     */
    public Project(String title, String link, String imageUrl, String description) {
        this.title = title;
        this.link = link;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }
}