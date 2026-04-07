package com.example.hamhamapp;

/**
 * Appointment.java
 * Purpose: Model class representing an appointment between a student and a counselor.
 */
public class Appointment {
    private String id;
    private String studentEmail;
    private String counselorId;
    private String counselorName;
    private String date;
    private String time;
    private String slotId;
    private String status; // upcoming, past, cancelled
    private long timestamp;

    public Appointment() {}

    public Appointment(String id, String studentEmail, String counselorId, String counselorName, String date, String time, String slotId, String status, long timestamp) {
        this.id = id;
        this.studentEmail = studentEmail;
        this.counselorId = counselorId;
        this.counselorName = counselorName;
        this.date = date;
        this.time = time;
        this.slotId = slotId;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getCounselorId() { return counselorId; }
    public void setCounselorId(String counselorId) { this.counselorId = counselorId; }

    public String getCounselorName() { return counselorName; }
    public void setCounselorName(String counselorName) { this.counselorName = counselorName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getSlotId() { return slotId; }
    public void setSlotId(String slotId) { this.slotId = slotId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}