package com.example.hamhamapp;

/**
 * TimeSlot.java
 * Purpose: Model class representing a counselor's availability slot.
 */
public class TimeSlot {
    private String id;
    private String counselorId;
    private String date; // YYYY-MM-DD
    private String startTime; // HH:MM AM/PM
    private String endTime; // HH:MM AM/PM
    private boolean isBooked;

    public TimeSlot() {}

    public TimeSlot(String id, String counselorId, String date, String startTime, String endTime, boolean isBooked) {
        this.id = id;
        this.counselorId = counselorId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isBooked = isBooked;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCounselorId() { return counselorId; }
    public void setCounselorId(String counselorId) { this.counselorId = counselorId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean booked) { isBooked = booked; }

    /** @return Combined time string "startTime - endTime" */
    public String getTime() {
        return startTime + " - " + endTime;
    }
}