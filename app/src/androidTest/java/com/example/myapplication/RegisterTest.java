package com.example.myapplication;

import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import com.example.myapplication.activity.RegisterActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RegisterTest {
    @Rule
    public ActivityTestRule<RegisterActivity> activityTestRule = new ActivityTestRule<>(
            RegisterActivity.class);

    @Test
    public void missingUsername() {
        onView(withId(R.id.et_email)).perform(typeText("travel@example.com"), closeSoftKeyboard());
        onView(withId(R.id.et_password)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.btn_register)).perform(click());

        onView(withText(R.string.name_empty)).check(matches(isDisplayed()));
    }

    @Test
    public void missingEmail() {
        onView(withId(R.id.et_name)).perform(typeText("traveler"), closeSoftKeyboard());
        onView(withId(R.id.et_password)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.btn_register)).perform(click());

        onView(withText(R.string.email_valid)).check(matches(isDisplayed()));
    }

    @Test
    public void missingPassword() {
        onView(withId(R.id.et_email)).perform(typeText("travel@example.com"), closeSoftKeyboard());
        onView(withId(R.id.et_name)).perform(typeText("traveler"), closeSoftKeyboard());
        onView(withId(R.id.btn_register)).perform(click());

        onView(withText(R.string.password_empty)).check(matches(isDisplayed()));
    }

    @Test
    public void missingUsernameEmail() {
        onView(withId(R.id.et_password)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.btn_register)).perform(click());

        onView(withText(R.string.email_valid)).check(matches(isDisplayed()));
        onView(withText(R.string.name_empty)).check(matches(isDisplayed()));
    }
}
