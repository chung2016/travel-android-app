package com.example.myapplication.models;

import java.util.Date;

public class Place {

    private String id;
    private String name;
    private String location;
    private String photo;
    private String description;
    private String authorComment;
    private User author;
    private String type;
    private Date updatedAt;
    private Date createdAt;

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getPhoto() {
        return photo;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthorComment() {
        return authorComment;
    }

    public User getAuthor() {
        return author;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getType() {
        return type;
    }
}
