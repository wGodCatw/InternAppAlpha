package com.example.internapp.StudentDir;

import java.util.Objects;

/**
 * Represents a project of the student with title, link, description and image URL.
 */
public class Project {
    private String imageUrl;
    private String title;
    private String StudentUsername;
    private String description;
    private String link;

    /**
     * Constructs a new Project object with the given title, description, link, and image URL.
     *
     * @param title       The name of the project.
     * @param link        The link to the project itself.
     * @param imageUrl    The URL of the image associated with the project.
     * @param description The description of the project.
     */
    public Project(String StudentUsername, String title, String link, String imageUrl, String description) {
        this.title = title;
        this.link = link;
        this.StudentUsername = StudentUsername;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public Project() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getStudentUsername() {
        return StudentUsername;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Project project = (Project) obj;
        return Objects.equals(title, project.title) &&
                Objects.equals(description, project.description) &&
                Objects.equals(imageUrl, project.imageUrl) &&
                Objects.equals(link, project.link);
    }


}