package com.aryupay.helpingapp.modal.profile.my_profile;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("comments")
    @Expose
    private ArrayList<Comment> comments = null;
    @SerializedName("followers")
    @Expose
    private Integer followers;
    @SerializedName("following")
    @Expose
    private Integer following;
    @SerializedName("helping")
    @Expose
    private Integer helping;
    @SerializedName("reviews")
    @Expose
    private Integer reviews;
    @SerializedName("rating")
    @Expose
    private Integer rating;

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public Integer getFollowers() {
        return followers;
    }

    public void setFollowers(Integer followers) {
        this.followers = followers;
    }

    public Integer getFollowing() {
        return following;
    }

    public void setFollowing(Integer following) {
        this.following = following;
    }

    public Integer getHelping() {
        return helping;
    }

    public void setHelping(Integer helping) {
        this.helping = helping;
    }

    public Integer getReviews() {
        return reviews;
    }

    public void setReviews(Integer reviews) {
        this.reviews = reviews;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

}