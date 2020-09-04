package com.aryupay.helpingapp.modal.bloglist;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("blog")
    @Expose
    private ArrayList<Blog> blog = null;
    @SerializedName("images")
    @Expose
    private ArrayList<Image> images = null;
    @SerializedName("likes")
    @Expose
    private ArrayList<Integer> likes = null;
    @SerializedName("views")
    @Expose
    private ArrayList<Integer> views = null;
    @SerializedName("comments")
    @Expose
    private ArrayList<Integer> comments = null;

    public ArrayList<Blog> getBlog() {
        return blog;
    }

    public void setBlog(ArrayList<Blog> blog) {
        this.blog = blog;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public ArrayList<Integer> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<Integer> likes) {
        this.likes = likes;
    }

    public ArrayList<Integer> getViews() {
        return views;
    }

    public void setViews(ArrayList<Integer> views) {
        this.views = views;
    }

    public ArrayList<Integer> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Integer> comments) {
        this.comments = comments;
    }
}