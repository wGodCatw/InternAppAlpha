package com.example.internapp;

public class ViewPagerItem {
    String imageUrl;
    String name, faculty, location;

    public ViewPagerItem(String imageUrl, String name, String faculty, String location) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.faculty = faculty;
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
