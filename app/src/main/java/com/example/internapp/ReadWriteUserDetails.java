package com.example.internapp;

public class ReadWriteUserDetails {
    public String username;
    public String doB;
    public String userPic;
    public String role;
    public String mobile;
    public String university;
    public String faculty;
    public String company;
    public String name;

    public ReadWriteUserDetails() {
    }

    public ReadWriteUserDetails(String userPic) {
        this.userPic = userPic;
    }



    public ReadWriteUserDetails(String username, String name, String doB, String role, String mobile, String university, String faculty, String userPic) {
        this.username = username;
        this.name = name;
        this.doB = doB;
        this.role = role;
        this.mobile = mobile;
        this.university = university;
        this.faculty = faculty;
        this.userPic = userPic;
    }

    public ReadWriteUserDetails(String username, String name, String doB, String role, String mobile, String company, String userPic) {
        this.username = username;
        this.name = name;
        this.doB = doB;
        this.role = role;
        this.mobile = mobile;
        this.company = company;
        this.userPic = userPic;
    }
}
