package com.example.hamhamapp;
import java.util.List;

/**
 * Counselor.java
 *
 * Purpose: Model class representing a counselor user. Extends User with
 * counselor-specific fields: specialties, description, and rating.
 * Stored in Firestore under the "users" collection with role = "counselor".
 * Counselor accounts are created exclusively by the Admin; counselors
 * cannot self-register. Rating is the average score from student feedback.
 * Follows the Model layer of the MVC pattern.
 *
 * Outstanding issues: None.
 */
public class Counselor extends User {

    private List<String> specialties; // e.g. ["Anxiety", "Stress", "Depression"]
    private String       description; // brief bio shown on profile
    private double       rating;      // average rating out of 5.0

    /** Required no-arg constructor for Firestore deserialization. */
    public Counselor() {
        super();
    }

    /**
     * Constructs a Counselor with all fields.
     *
     * @param uid         Firebase Auth UID.
     * @param name        Full name (typically "Dr. FirstName LastName").
     * @param email       Email address.
     * @param phone       Phone number (may be empty).
     * @param specialties List of specialty strings.
     * @param description Brief bio or clinical description.
     * @param rating      Average student rating, 0.0 – 5.0.
     */
    public Counselor(String uid, String name, String email, String phone,
                     List<String> specialties, String description, double rating) {
        super(uid, name, email, phone, "counselor");
        this.specialties = specialties;
        this.description = description;
        this.rating      = rating;
    }

    /** @return List of specialty strings. */
    public List<String> getSpecialties() { return specialties; }

    /** @param specialties List of specialty strings. */
    public void setSpecialties(List<String> specialties) { this.specialties = specialties; }

    /** @return Bio / clinical description. */
    public String getDescription() { return description; }

    /** @param description Bio / clinical description. */
    public void setDescription(String description) { this.description = description; }

    /** @return Average rating out of 5.0. */
    public double getRating() { return rating; }

    /** @param rating Average rating. */
    public void setRating(double rating) { this.rating = rating; }
}
