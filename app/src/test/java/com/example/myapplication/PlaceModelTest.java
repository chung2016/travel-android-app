package com.example.myapplication;

import com.example.myapplication.models.Place;
import com.example.myapplication.models.User;

import junit.framework.TestCase;

import org.junit.Assert;

import java.util.Date;

public class PlaceModelTest extends TestCase {
    private Place place;

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
        place = new Place();
    }

    public void testGetCreatedAt() {
        Date expected = new Date();
        place.setCreatedAt(expected);
        Date actual = place.getCreatedAt();
        Assert.assertEquals(expected, actual);
    }

    public void testGetDescription() {
        String expected = this.randomString(10);
        place.setDescription(expected);
        String actual = place.getDescription();
        Assert.assertEquals(expected, actual);
    }

    public void testGetAuthorComment() {
        String expected = this.randomString(10);
        place.setAuthorComment(expected);
        String actual = place.getAuthorComment();
        Assert.assertEquals(expected, actual);
    }

    public void testGetAuthor() {
        User user = new User();
        user.setId(randomString(10));
        place.setAuthor(user);
        User actual = place.getAuthor();
        Assert.assertEquals(user, actual);
    }
}
