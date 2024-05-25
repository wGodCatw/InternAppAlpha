package com.example.internapp.MainDir;

/**
 * ReadWriteUserDetails class represents user details with read and write access.
 * It includes various attributes such as username, date of birth, user picture, role, mobile number,
 * university, faculty, company, and name.
 */
public class ReadWriteUserDetails {
    /**
     * The username of the user.
     */
    public String username;
    /**
     * The date of birth of the user.
     */
    public String doB;
    /**
     * The user's profile picture.
     */
    public String userPic;
    /**
     * The role of the user.
     */
    public String role;
    /**
     * The mobile number of the user.
     */
    public String mobile;
    /**
     * The university the user is associated with.
     */
    public String university;
    /**
     * The faculty the user belongs to.
     */
    public String faculty;
    /**
     * The company the user works for.
     */
    public String company;
    /**
     * The name of the user.
     */
    public String name;

    /**
     * Needed to fix an error with Firebase not recognizing empty call for a user in database
     * Constructs a new ReadWriteUserDetails object with default values.
     */
    public ReadWriteUserDetails() {
    }

    /**
     * Constructs a new ReadWriteUserDetails object with the specified attributes.
     *
     * @param username   The username of the user.
     * @param name       The name of the user.
     * @param doB        The date of birth of the user.
     * @param role       The role of the user.
     * @param mobile     The mobile number of the user.
     * @param university The university the user is associated with.
     * @param faculty    The faculty the user belongs to.
     * @param userPic    The user's profile picture.
     */
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

    /**
     * Constructs a new ReadWriteUserDetails object with the specified attributes.
     *
     * @param username The username of the user.
     * @param name     The name of the user.
     * @param doB      The date of birth of the user.
     * @param role     The role of the user.
     * @param mobile   The mobile number of the user.
     * @param company  The company the user works for.
     * @param userPic  The user's profile picture.
     */
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