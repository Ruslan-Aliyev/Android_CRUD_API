package com.ruslan_website.travelblog.utils.database;

public class Entry {

    private int id;
    private String username;
    private String date;
    private String place;
    private String comments;
    private String imageUrl;

    public Entry (int id, String username, String date, String place, String comments, String imageUrl){
        this.id = id;
        this.username = username;
        this.date = date;
        this.place = place;
        this.comments = comments;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
