package com.example.techlap.booksharingapp;

/**
 * Created by tech lap on 18/12/2016.
 */
public class Review {
    String rate,username,bookId,review,userImage;

    public Review() {

    }

    public Review(String username, String userImage, String bookId, String review, String rate) {
        this.username = username;
        this.userImage = userImage;
        this.bookId = bookId;
        this.review = review;
        this.rate = rate;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
