package com.example.internapp;

public class FavoriteStudent {
    private String name;
    private String email;
    private String imageurl;

    public FavoriteStudent(String name, String email, String imageurl) {
        this.name = name;
        this.email = email;
        this.imageurl = imageurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", imageurl='" + imageurl + '\'' +
                '}';
    }
}
