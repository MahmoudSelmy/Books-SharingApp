package com.example.techlap.booksharingapp;

/**
 * Created by tech lap on 18/12/2016.
 */
public class UserModel {
    //name,id,photo_profile
    private String name,email,phone, birthday, profileImage, address;


    public UserModel() {


    }


    public UserModel(String profileImage, String name, String email, String phone, String birthDate, String birthday, String address) {
        this.profileImage = profileImage;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birthday = birthday;
        this.address = address;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
