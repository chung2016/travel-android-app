package com.example.myapplication;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.myapplication.activity.LoginActivity;
import com.example.myapplication.activity.PlaceFormActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class AddPlaceTest {
    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Before
    public void setUp() {
        onView(ViewMatchers.withId(R.id.et_email)).perform(typeText("travel@example.com"), closeSoftKeyboard());
        onView(withId(R.id.et_password)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        onView(withId(R.id.miCreate)).perform(click());
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void missingLocation() {
        onView(withId(R.id.et_place_name)).perform(typeText("test name"), closeSoftKeyboard());
        onView(withId(R.id.et_description)).perform(typeText("test description"), closeSoftKeyboard());
        onView(withId(R.id.et_author_comment)).perform(typeText("test author comment"), closeSoftKeyboard());

        onView(withId(R.id.btn_submit)).perform(scrollTo(), click());
        onView(withText(R.string.location_empty)).check(matches(isDisplayed()));
    }

    @Test
    public void missingName() {
        onView(withId(R.id.et_place_location)).perform(scrollTo(), typeText("test location"), closeSoftKeyboard());
        onView(withId(R.id.et_description)).perform(scrollTo(), typeText("test description"), closeSoftKeyboard());
        onView(withId(R.id.et_author_comment)).perform(scrollTo(), typeText("test author comment"), closeSoftKeyboard());

        onView(withId(R.id.btn_submit)).perform(scrollTo(), click());
        onView(withText(R.string.name_empty)).check(matches(isDisplayed()));
    }


}
