package com.example.techlap.booksharingapp;

/**
 * Created by tech lap on 18/12/2016.
 */
public class Book {
    private String name,user,desc,pdf,userId, cover,userImage,author;



    public Book() {
    }

    public Book(String name, String user, String desc, String pdf, String userId, String cover, String userImage, String author) {
        this.name = name;
        this.user = user;
        this.desc = desc;
        this.pdf = pdf;
        this.userId = userId;
        this.cover = cover;
        this.userImage = userImage;
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }





}
