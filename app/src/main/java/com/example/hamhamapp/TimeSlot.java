package com.example.hamhamapp;

/**
 * TimeSlot.java
 * Purpose: Model class representing a counselor's availability slot.
 */
public class TimeSlot {
    private String id;
    private String counselorId;
    private String date; // YYYY-MM-DD
    private String time; // HH:MM AM/PM
    private boolean isBooked;

    public TimeSlot() {}

    public TimeSlot(String id, String counselorId, String date, String time, boolean isBooked) {
        this.id = id;
        this.counselorId = counselorId;
        this.date = date;
        this.time = time;
        this.isBooked = isBooked;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCounselorId() { return counselorId; }
    public void setCounselorId(String counselorId) { this.counselorId = counselorId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean booked) { isBooked = booked; }
}