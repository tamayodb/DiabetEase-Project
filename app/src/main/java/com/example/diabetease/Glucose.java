package com.example.diabetease;

import java.util.Date;

public class Glucose {
    private String user_id;
    private double glucose_value;
    private Date timestamp;
    private String glucose_status;

    // Required empty constructor for Firestore deserialization
    public Glucose() {}

    // Getters
    public String getUser_id() {
        return user_id;
    }

    public double getGlucose_value() {
        return glucose_value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getGlucose_status() {
        return glucose_status;
    }
}
