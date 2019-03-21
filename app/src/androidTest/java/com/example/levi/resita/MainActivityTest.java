package com.example.levi.resita;

import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.espresso.Espresso;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    // espresso rule which tells which activity to start
    @Rule
    public final ActivityTestRule<MainActivity> mActivityRule =
         new ActivityTestRule<>(MainActivity.class, true, false);

    @Test
    public void testProcessActivityIsStarted() {
//        mActivityRule.launchActivity(null);
//        Intents.init();
//        Espresso.onView(withId(R.id.start_button)).perform(click());
//        intended(hasComponent(ProcessReceipt.class.getName()));
//        Intents.release();
    }
}
