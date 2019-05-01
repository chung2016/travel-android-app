package com.example.myapplication;

import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.myapplication.activity.LoginActivity;

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
public class LoginTest {

    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<>(
            LoginActivity.class);

    @Test
    public void validInfomation() {
        onView(withId(R.id.et_email)).perform(typeText("travel@example.com"), closeSoftKeyboard());
        onView(withId(R.id.et_password)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());

        try{
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }
        onView(withId(R.id.miCreate)).check(matches(isDisplayed()));
    }

    @Test
    public void missingPassword() {
        onView(withId(R.id.et_email)).perform(typeText("travel@example.com"), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());
        onView(withText(R.string.password_empty)).check(matches(isDisplayed()));
    }

    @Test
    public void missingEmail() {
        onView(withId(R.id.et_password)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());
        onView(withText(R.string.email_valid)).check(matches(isDisplayed()));
    }

    @Test
    public void invalidEmailFormat1() {
        onView(withId(R.id.et_email)).perform(typeText("travel@"), closeSoftKeyboard());
        onView(withId(R.id.et_password)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());
        onView(withText(R.string.email_valid)).check(matches(isDisplayed()));
    }

    @Test
    public void invalidEmailFormat2() {
        onView(withId(R.id.et_email)).perform(typeText("travel@example"), closeSoftKeyboard());
        onView(withId(R.id.et_password)).perform(typeText("123456"), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());
        onView(withText(R.string.email_valid)).check(matches(isDisplayed()));
    }
}