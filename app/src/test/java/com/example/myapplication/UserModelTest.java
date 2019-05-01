package com.example.myapplication;

import com.example.myapplication.models.User;

import junit.framework.TestCase;

import org.junit.Assert;

public class UserModelTest extends TestCase {
    private User user;

    private static String randomString(int length) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    protected void setUp() throws Exception {
        super.setUp();
        user = new User();
    }

    public void testGetId() {
        String expected = this.randomString(10);
        user.setId(expected);
        String actual = user.getId();
        Assert.assertEquals(expected, actual);
    }

    public void testGetUsername() {
        String expected = this.randomString(10);
        user.setUsername(expected);
        String actual = user.getUsername();
        Assert.assertEquals(expected, actual);
    }

    public void testGetEmail() {
        String expected = this.randomString(10);
        user.setEmail(expected);
        String actual = user.getEmail();
        Assert.assertEquals(expected, actual);
    }

    public void testGetGender() {
        user.setGender("Male");
        Assert.assertEquals("Male", user.getGender());
    }
}
