package com.example.myapplication;

import com.example.myapplication.models.Comment;
import com.example.myapplication.models.Place;
import com.example.myapplication.models.User;

import junit.framework.TestCase;

import org.junit.Assert;

import java.util.Date;

public class CommentModelTest extends TestCase {
    private Comment comment;

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
        comment = new Comment();
    }

    public void testGetMessage() {
        String expected = this.randomString(10);
        comment.setMessage(expected);
        String actual = comment.getMessage();
        Assert.assertEquals(expected, actual);
    }

    public void testGetCreatedAt() {
        Date expected = new Date();
        comment.setCreatedAt(expected);
        Date actual = comment.getCreatedAt();
        Assert.assertEquals(expected, actual);
    }

    public void testGetUser() {
        User user = new User();
        user.setId(randomString(10));
        comment.setUser(user);
        User actual = comment.getUser();
        Assert.assertEquals(user, actual);
    }

    public void testGetPlace() {
        String expected = this.randomString(10);
        comment.setPlace(expected);
        String actual = comment.getPlace();
        Assert.assertEquals(expected, actual);
    }
}
