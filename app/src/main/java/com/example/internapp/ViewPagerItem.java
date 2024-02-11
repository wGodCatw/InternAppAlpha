package com.example.internapp;

public class ViewPagerItem {
    private final String imageUrl;
    private String whatsappNumber;
    private final String name, faculty, username;

    public ViewPagerItem(Student student) {
        this.name = student.name;
        this.whatsappNumber = student.phone;
        this.imageUrl = student.uri;
        this.faculty = student.faculty;
        this.username = student.username;

    }

    public String getName() {
        return name;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUsername() {
        return username;
    }
}
