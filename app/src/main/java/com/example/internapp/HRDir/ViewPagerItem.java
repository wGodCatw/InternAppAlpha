package com.example.internapp.HRDir;

import com.example.internapp.MainDir.Student;

/**
 * Represents an item for the ViewPager.
 */
public class ViewPagerItem {
    private final String imageUrl;
    private String whatsappNumber;
    private final String name, university, faculty, username;

    /**
     * Constructs a ViewPagerItem from a Student object.
     *
     * @param student The Student object to create ViewPagerItem from.
     */
    public ViewPagerItem(Student student) {
        this.name = student.name;
        this.whatsappNumber = student.phone;
        this.imageUrl = student.uri;
        this.faculty = student.faculty;
        this.university = student.university;
        this.username = student.username;
    }

    /**
     * Gets the university of the student.
     *
     * @return The university of the student.
     */
    public String getUniversity() {
        return university;
    }

    /**
     * Gets the name of the student.
     *
     * @return The name of the student.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the faculty of the student.
     *
     * @return The faculty of the student.
     */
    public String getFaculty() {
        return faculty;
    }

    /**
     * Gets the WhatsApp number of the student.
     *
     * @return The WhatsApp number of the student.
     */
    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    /**
     * Sets the WhatsApp number of the student.
     *
     * @param whatsappNumber The WhatsApp number to set.
     */
    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    /**
     * Gets the image URL of the student.
     *
     * @return The image URL of the student.
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Gets the username of the student.
     *
     * @return The username of the student.
     */
    public String getUsername() {
        return username;
    }
}
