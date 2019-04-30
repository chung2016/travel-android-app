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

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthorComment(String authorComment) {
        this.authorComment = authorComment;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
