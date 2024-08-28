package com.example.internapp.MainDir;

/**
 * Represents a student entity with basic information.
 */
public class Student {
    // Student's username
    public String username;
    // URI of student's profile picture
    public String uri;
    // Student's full name
    public String name;
    // Faculty of the student
    public String faculty;
    // University attended by the student
    public String university;
    // Student's phone number
    public String phone;

    /**
     * Constructs a new Student object with the provided details.
     * @param username The username of the student.
     * @param uri The URI of the student's profile picture.
     * @param name The full name of the student.
     * @param university The university attended by the student.
     * @param faculty The faculty of the student.
     * @param phone The phone number of the student.
     */
    public Student(String username, String uri, String name, String university, String faculty, String phone) {
        this.username = username;
        this.uri = uri;
        this.name = name;
        this.university = university;
        this.faculty = faculty;
        this.phone = phone;
    }
}
