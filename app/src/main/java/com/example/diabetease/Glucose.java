package com.example.diabetease;

public class Glucose {
    private String glucose_status;
    private int glucose_value;
    private long timestamp;
    private String user_id;

    public Glucose() {}

    public Glucose(String glucose_status, int glucose_value, long timestamp, String user_id) {
        this.glucose_status = glucose_status;
        this.glucose_value = glucose_value;
        this.timestamp = timestamp;
        this.user_id = user_id;
    }

    public String getGlucose_status() {
        return glucose_status;
    }

    public int getGlucose_value() {
        return glucose_value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUser_id() {
        return user_id;
    }
}

