package com.example.internapp;

public class ViewPagerItem {
    String imageUrl;
    String whatsappNumber;
    String name, faculty, location;

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public void setWhatsappNumber(String whatsappNumber) {
        this.whatsappNumber = whatsappNumber;
    }

    public ViewPagerItem(String imageUrl, String whatsappNumber, String name, String faculty, String location) {
        this.name = name;
        this.whatsappNumber = whatsappNumber;
        this.imageUrl = imageUrl;
        this.faculty = faculty;
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
