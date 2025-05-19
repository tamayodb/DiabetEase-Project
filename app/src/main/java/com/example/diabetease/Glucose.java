package com.example.diabetease;

import java.sql.Timestamp;

public class Glucose {
    private String user_id;
    private double glucose_value;
    private Timestamp timestamp;
    private String glucose_status;

    // Required empty constructor for Firestore
    public Glucose() {}

    // Getters
    public String getUser_id() {
        return user_id;
    }

    public double getGlucose_value() {
        return glucose_value;
    }

    public Timestamp getTimestamp() { return timestamp; }

    public String getGlucose_status() {
        return glucose_status;
    }
}
