package com.example.myapplication.ListView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlaceList {

    private String name;
    private String location;
    private String photo;
    private Date createdAt;

    public PlaceList(String name, String location, String photo) {
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
}
