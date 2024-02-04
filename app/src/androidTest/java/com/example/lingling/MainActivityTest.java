package com.example.lingling;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;

import com.view.activity.MainActivity;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Before
    public void setUp() {
        // Initialization code before each test, if needed
    }

    @Test
    public void testSettingsButton() {
        ActivityScenario.launch(MainActivity.class);
        Espresso.onView(ViewMatchers.withId(R.id.settings1)).perform(click());
        // Add assertions or verifications based on the behavior you expect after clicking the settings button
    }

    @Test
    public void testNewTaskButton() {
        ActivityScenario.launch(MainActivity.class);
        Espresso.onView(ViewMatchers.withId(R.id.new_task)).perform(click());
        // Add assertions or verifications based on the behavior you expect after clicking the new task button
    }

    @Test
    public void testPersonalBackgroundButton() {
        ActivityScenario.launch(MainActivity.class);
        Espresso.onView(ViewMatchers.withId(R.id.personal_background)).perform(click());
        // Add assertions or verifications based on the behavior you expect after clicking the personal background button
    }

    @Test
    public void testRecyclerViewDisplayed() {
        ActivityScenario.launch(MainActivity.class);
        Espresso.onView(ViewMatchers.withId(R.id.all_tasks_view)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        // Add assertions or verifications based on the behavior you expect regarding the RecyclerView
    }

    // Add more test methods as needed for other functionality
}
