package com.example.diabetease;

public class Users {
    private String birthdate;
    private String email;
    private String firstName;
    private String lastName;

    // Required empty constructor for Firestore
    public Users() {}

    // Getters
    public String getBirthdate() {
        return birthdate;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
