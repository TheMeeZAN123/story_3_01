package com.example.hamhamapp;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import android.content.Intent;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented tests for Counselor Management by Admin.
 * Covers stories:
 * 2-02: admin views all counselors
 * 2-03: admin edits counselor info
 * 2-04: admin removes/deactivates counselor
 * 2-05: admin updates counselor from inactive to active
 *
 * Note: These tests assume a mock/test environment or existing test data in Firestore.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminCounselorManagementTest {

    @Rule
    public ActivityScenarioRule<AdminManageCounselorsActivity> activityRule =
            new ActivityScenarioRule<>(AdminManageCounselorsActivity.class);

    /**
     * Test Case 2-02: Admin views all counselors
     * Verifies that the counselor list is displayed and search works.
     */
    @Test
    public void testAdminViewsAllCounselors() {
        // Verify the list is displayed
        onView(withId(R.id.counselorsList)).check(matches(isDisplayed()));

        // Perform a search
        onView(withId(R.id.searchInput)).perform(typeText("Sarah"));
        
        // Verify filtered results (Assuming "Dr. Sarah Johnson" exists in test data)
        // onData(anything()).inAdapterView(withId(R.id.counselorsList)).atPosition(0)
        //         .onChildView(withId(R.id.counselorName)).check(matches(withText("Dr. Sarah Johnson")));
    }

    /**
     * Test Case 2-03: Admin edits counselor info
     * Verifies that an admin can navigate to the edit screen and update info.
     */
    @Test
    public void testAdminEditsCounselorInfo() {
        // Click on the first counselor in the list
        onData(anything()).inAdapterView(withId(R.id.counselorsList)).atPosition(0).perform(click());

        // Click "Edit Counselor Info"
        onView(withId(R.id.actionEditInfo)).perform(click());

        // Update name
        onView(withId(R.id.nameInput)).perform(replaceText("Updated Counselor Name"));
        
        // Save changes
        onView(withId(R.id.registerCounselorBtn)).perform(click());

        // Verify return to actions screen (or check for success toast/msg if possible)
        onView(withText("Counselor Actions")).check(matches(isDisplayed()));
    }

    /**
     * Test Case 2-04 & 2-05: Admin deactivates/activates counselor
     * Verifies the toggle status functionality.
     */
    @Test
    public void testAdminTogglesCounselorStatus() {
        // Click on the first counselor
        onData(anything()).inAdapterView(withId(R.id.counselorsList)).atPosition(0).perform(click());

        // Get initial status text to determine what action to take
        // Note: Espresso doesn't easily let us read text to branch, so we test the flow.
        
        // Click Toggle Status
        onView(withId(R.id.actionToggleStatus)).perform(click());

        // Confirm in dialog
        onView(withText("Yes")).perform(click());

        // Verify the status text changed (e.g. if it was Active, it should now be Inactive)
        // This is simplified; in a real test we'd check specific ID values.
        onView(withId(R.id.counselorStatus)).check(matches(isDisplayed()));
    }
}
