package com.example.hamhamapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * AuthRepository.java
 *
 * Purpose: Repository class (Control layer, MVC) that encapsulates all
 * Firebase Authentication and Firestore user-document operations.
 * Provides a single, testable entry point for: student registration,
 * login for all roles, logout, account deletion, password change, and
 * password reset email. Activity classes delegate all auth logic here
 * and receive results via the AuthCallback interface, keeping UI code
 * free of Firebase SDK calls.
 *
 * Outstanding issues: None.
 */
public class AuthRepository {

    /**
     * Callback interface used by all auth operations.
     * Implemented as anonymous classes in the calling Activity.
     */
    public interface AuthCallback {
        /**
         * Called when the operation succeeds.
         * @param result Varies by operation: UID for register, role string
         *               for login, descriptive message for others.
         */
        void onSuccess(String result);

        /**
         * Called when the operation fails.
         * @param error User-facing error message suitable for display in a Toast.
         */
        void onError(String error);
    }

    private final FirebaseAuth      auth;
    private final FirebaseFirestore db;

    /** Constructs AuthRepository using the default Firebase singleton instances. */
    public AuthRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.db   = FirebaseFirestore.getInstance();
    }

    /**
     * Constructor for dependency injection in unit / instrumented tests.
     *
     * @param auth FirebaseAuth instance (may be a test double).
     * @param db   FirebaseFirestore instance (may be a test double).
     */
    public AuthRepository(FirebaseAuth auth, FirebaseFirestore db) {
        this.auth = auth;
        this.db   = db;
    }

    /**
     * Registers a new student account. Creates a Firebase Auth user, then
     * writes a student document to the Firestore "users" collection.
     *
     * @param name     Student's full name.
     * @param email    Student's email address.
     * @param password Chosen password — Firebase requires minimum 6 characters.
     * @param callback onSuccess receives the new Firebase Auth UID;
     *                 onError receives a user-facing message.
     */
    public void registerStudent(String name, String email,
                                String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    Map<String, Object> data = new HashMap<>();
                    data.put("uid",           uid);
                    data.put("name",          name);
                    data.put("email",         email);
                    data.put("phone",         "");
                    data.put("role",          "student");
                    data.put("noShowCount",   0);
                    data.put("accountStatus", "active");

                    db.collection("users").document(uid)
                            .set(data)
                            .addOnSuccessListener(unused -> callback.onSuccess(uid))
                            .addOnFailureListener(e ->
                                    callback.onError("Account created but profile save failed: "
                                            + e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(friendlyAuthError(e.getMessage())));
    }

    /**
     * Registers a new counselor account. (Admin use only)
     *
     * @param name        Counselor's full name.
     * @param email       Counselor's email address.
     * @param password    Temporary password.
     * @param phone       Counselor's phone number.
     * @param specialties Comma-separated specialties.
     * @param description Counselor's description.
     * @param callback    onSuccess receives the new counselor UID.
     */
    public void registerCounselor(String name, String email, String password,
                                  String phone, String specialties, String description,
                                  AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    Map<String, Object> data = new HashMap<>();
                    data.put("uid",           uid);
                    data.put("name",          name);
                    data.put("email",         email);
                    data.put("phone",         phone);
                    data.put("role",          "counselor");
                    data.put("description",   description);
                    
                    String[] specs = specialties.split(",");
                    for (int i = 0; i < specs.length; i++) specs[i] = specs[i].trim();
                    data.put("specialties",   Arrays.asList(specs));

                    db.collection("users").document(uid)
                            .set(data)
                            .addOnSuccessListener(unused -> callback.onSuccess(uid))
                            .addOnFailureListener(e ->
                                    callback.onError("Account created but profile save failed: "
                                            + e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(friendlyAuthError(e.getMessage())));
    }

    /**
     * Signs in any user (student, counselor, or admin) with email and password.
     * On success, fetches the user's role from Firestore so the caller can
     * route to the correct dashboard without hard-coding role assumptions.
     *
     * @param email    Email address.
     * @param password Password.
     * @param callback onSuccess receives the role string ("student", "counselor",
     *                 or "admin"); onError receives a user-facing error message.
     */
    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    db.collection("users").document(uid).get()
                            .addOnSuccessListener(doc -> {
                                if (doc.exists()) {
                                    String role = doc.getString("role");
                                    callback.onSuccess(role != null ? role : "student");
                                } else {
                                    callback.onError("User profile not found in database.");
                                }
                            })
                            .addOnFailureListener(e ->
                                    callback.onError("Could not fetch user role: " + e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(friendlyAuthError(e.getMessage())));
    }

    /**
     * Signs out the currently authenticated user via Firebase Auth.
     * Sign-out is synchronous and cannot fail, so onSuccess is always called.
     *
     * @param callback onSuccess is always invoked with a confirmation message.
     */
    public void logout(AuthCallback callback) {
        auth.signOut();
        callback.onSuccess("Logged out successfully.");
    }

    /**
     * Deletes the currently signed-in student's Firebase Auth account and
     * removes their document from the Firestore "users" collection.
     * Both deletions must succeed; if Firestore deletion fails, the Auth
     * account is not deleted so data stays consistent.
     *
     * @param callback onSuccess receives a confirmation message;
     *                 onError receives a user-facing error message.
     */
    public void deleteStudentAccount(AuthCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError("No user is currently signed in.");
            return;
        }
        String uid = user.getUid();

        db.collection("users").document(uid)
                .delete()
                .addOnSuccessListener(unused ->
                        user.delete()
                                .addOnSuccessListener(v ->
                                        callback.onSuccess("Account deleted successfully."))
                                .addOnFailureListener(e ->
                                        callback.onError("Firestore doc deleted but Auth delete failed: "
                                                + e.getMessage())))
                .addOnFailureListener(e ->
                        callback.onError("Could not delete user data: " + e.getMessage()));
    }

    /**
     * Updates the password for the currently signed-in user via Firebase Auth.
     * Used by counselors from their Edit Profile screen.
     * Requires the user to have signed in recently; if a "requires-recent-login"
     * error is returned, the caller should prompt re-authentication.
     *
     * @param newPassword New password — minimum 6 characters.
     * @param callback    onSuccess receives a confirmation message;
     *                    onError receives a user-facing error message.
     */
    public void changePassword(String newPassword, AuthCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError("No user is currently signed in.");
            return;
        }
        user.updatePassword(newPassword)
                .addOnSuccessListener(unused ->
                        callback.onSuccess("Password updated successfully."))
                .addOnFailureListener(e ->
                        callback.onError("Password change failed: " + e.getMessage()));
    }

    /**
     * Sends a password reset email to the given address via Firebase Auth.
     * Used by ForgotPasswordActivity. Firebase handles the reset link and
     * expiry — no further in-app action is needed after this call.
     *
     * @param email    Email address to send the reset link to.
     * @param callback onSuccess receives a confirmation message;
     *                 onError receives a user-facing error message.
     */
    public void sendPasswordResetEmail(String email, AuthCallback callback) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> callback.onSuccess("Reset email sent."))
                .addOnFailureListener(e ->
                        callback.onError("Could not send reset email: " + e.getMessage()));
    }

    /**
     * Returns the currently signed-in Firebase user, or null if nobody is signed in.
     *
     * @return FirebaseUser or null.
     */
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    /**
     * Converts a raw Firebase Auth exception message into a short,
     * user-friendly string suitable for display in a Toast.
     *
     * @param raw Raw exception message from Firebase.
     * @return Simplified error string.
     */
    private String friendlyAuthError(String raw) {
        if (raw == null) return "An unexpected error occurred.";
        if (raw.contains("password"))          return "Incorrect password. Please try again.";
        if (raw.contains("no user record"))    return "No account found with this email.";
        if (raw.contains("email address is already")) return "An account with this email already exists.";
        if (raw.contains("badly formatted"))   return "Please enter a valid email address.";
        return "Authentication failed. Please try again.";
    }
}
