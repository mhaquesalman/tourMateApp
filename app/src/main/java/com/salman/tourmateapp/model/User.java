package com.salman.tourmateapp.model;

public class User {
    String userid;
    String fullname;
    String email;
    String phone;
    String imageUrl;

    public User() {
    }

    public User(String fullname, String email, String phone, String imageUrl) {
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }

    public User(String userid, String fullname, String email, String phone, String imageUrl) {
        this.userid = userid;
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }


    public String getUserid() {
        return userid;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
