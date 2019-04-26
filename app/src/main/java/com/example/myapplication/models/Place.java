package com.example.myapplication.models;

import java.util.Date;

public class Place {

    private String id;
    private String name;
    private String location;
    private String photo;
    private Date createdAt;

    public Place(String id, String name, String location, String photo) {
        this.name = name;
        this.location = location;
        this.photo = photo;
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
}
