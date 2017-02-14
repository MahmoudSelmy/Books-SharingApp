package com.example.techlap.booksharingapp;

/**
 * Created by tech lap on 18/12/2016.
 */
public class Item {
    String cover,name,id;

    public Item() {
    }

    public Item(String name, String id, String cover) {

        this.name = name;
        this.id = id;
        this.cover = cover;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
