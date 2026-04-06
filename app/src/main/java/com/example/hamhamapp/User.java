package com.example.hamhamapp;

/**
 * User.java
 *
 * Purpose: Base model class for all users in the HamhamApp system.
 * Holds fields common to every role: Student, Counselor, and Admin.
 * Instances are stored in the Firestore "users" collection, keyed by
 * the Firebase Auth UID. Subclassed by Student and Counselor for
 * role-specific fields. Follows the Model layer of the MVC pattern.
 */
public class User {

    private String uid;
    private String name;
    private String email;
    private String phone;
    private String role; // "student" | "counselor" | "admin"
    private boolean isActive = true; // defaults to true

    /** Required no-arg constructor for Firestore deserialization. */
    public User() {}

    /**
     * Constructs a User with all fields populated.
     *
     * @param uid   Firebase Auth UID — unique identifier for this user.
     * @param name  Full display name.
     * @param email Email address, also used as the login credential.
     * @param phone Phone number; may be an empty string if not provided.
     * @param role  Role string: "student", "counselor", or "admin".
     */
    public User(String uid, String name, String email, String phone, String role) {
        this.uid   = uid;
        this.name  = name;
        this.email = email;
        this.phone = phone;
        this.role  = role;
        this.isActive = true;
    }

    /** @return Firebase Auth UID. */
    public String getUid() { return uid; }

    /** @param uid Firebase Auth UID. */
    public void setUid(String uid) { this.uid = uid; }

    /** @return Full display name. */
    public String getName() { return name; }

    /** @param name Full display name. */
    public void setName(String name) { this.name = name; }

    /** @return Email address. */
    public String getEmail() { return email; }

    /** @param email Email address. */
    public void setEmail(String email) { this.email = email; }

    /** @return Phone number, or empty string if not set. */
    public String getPhone() { return phone; }

    /** @param phone Phone number. */
    public void setPhone(String phone) { this.phone = phone; }

    /** @return Role: "student", "counselor", or "admin". */
    public String getRole() { return role; }

    /** @param role Role string. */
    public void setRole(String role) { this.role = role; }

    /** @return true if account is active. */
    public boolean getIsActive() { return isActive; }

    /** @param active status. */
    public void setIsActive(boolean active) { isActive = active; }
}
