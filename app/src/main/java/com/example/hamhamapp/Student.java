package com.example.hamhamapp;

/**
 * Student.java
 *
 * Purpose: Model class representing a student user. Extends User with
 * student-specific fields: noShowCount and accountStatus.
 * Stored in Firestore under the "users" collection with role = "student".
 * Students self-register via CreateAccount. Counselor accounts are
 * created by the Admin only. Follows the Model layer of the MVC pattern.
 *
 * Outstanding issues: None.
 */
public class Student extends User {

    private int    noShowCount;   // incremented by counselor on missed session
    private String accountStatus; // "active" | "flagged"

    /** Required no-arg constructor for Firestore deserialization. */
    public Student() {
        super();
    }

    /**
     * Constructs a Student with all fields.
     *
     * @param uid           Firebase Auth UID.
     * @param name          Full name.
     * @param email         Email address.
     * @param phone         Phone number (may be empty).
     * @param noShowCount   Number of missed appointments; starts at 0.
     * @param accountStatus "active" or "flagged".
     */
    public Student(String uid, String name, String email, String phone,
                   int noShowCount, String accountStatus) {
        super(uid, name, email, phone, "student");
        this.noShowCount   = noShowCount;
        this.accountStatus = accountStatus;
    }

    /** @return Number of missed appointments. */
    public int getNoShowCount() { return noShowCount; }

    /** @param noShowCount Number of missed appointments. */
    public void setNoShowCount(int noShowCount) { this.noShowCount = noShowCount; }

    /** @return Account status: "active" or "flagged". */
    public String getAccountStatus() { return accountStatus; }

    /** @param accountStatus "active" or "flagged". */
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }
}
