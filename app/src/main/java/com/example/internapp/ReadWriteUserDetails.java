package com.example.internapp;

public class ReadWriteUserDetails {
    public String doB, role, mobile, university, faculty, company;

    public ReadWriteUserDetails() {
    }



    public ReadWriteUserDetails(String doB, String role, String mobile, String university, String faculty) {
        this.doB = doB;
        this.role = role;
        this.mobile = mobile;
        this.university = university;
        this.faculty = faculty;
    }

    public ReadWriteUserDetails(String doB, String role, String mobile, String company) {
        this.doB = doB;
        this.role = role;
        this.mobile = mobile;
        this.company = company;
    }
}
