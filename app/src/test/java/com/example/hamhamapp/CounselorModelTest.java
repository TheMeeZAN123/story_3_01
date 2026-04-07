package com.example.hamhamapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for the Counselor model class.
 * Ensures data integrity and correct field mapping.
 */
public class CounselorModelTest {

    @Test
    public void testCounselorCreation() {
        List<String> specialties = Arrays.asList("Anxiety", "Stress");
        Counselor counselor = new Counselor(
                "uid123",
                "Dr. Smith",
                "smith@example.com",
                "1234567890",
                specialties,
                "Description test",
                4.5
        );

        assertEquals("uid123", counselor.getUid());
        assertEquals("Dr. Smith", counselor.getName());
        assertEquals("smith@example.com", counselor.getEmail());
        assertEquals("1234567890", counselor.getPhone());
        assertEquals(specialties, counselor.getSpecialties());
        assertEquals("Description test", counselor.getDescription());
        assertEquals(4.5, counselor.getRating(), 0.01);
        assertTrue(counselor.getIsActive()); // Default should be true
    }

    @Test
    public void testCounselorStatusToggle() {
        Counselor counselor = new Counselor();
        counselor.setIsActive(true);
        assertTrue(counselor.getIsActive());

        counselor.setIsActive(false);
        assertEquals(false, counselor.getIsActive());
    }
}
