package com.example.diabetease;

public class Glucose {
    private String user_id;
    private double glucose_value;
    private String log_date;
    private String log_time;
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

    public String getLog_date() {
        return log_date;
    }

    public String getLog_time() {
        return log_time;
    }

    public String getGlucose_status() {
        return glucose_status;
    }
}
