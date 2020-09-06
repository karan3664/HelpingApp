package com.aryupay.helpingapp.modal.profile.my_profile;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("by_id")
    @Expose
    private Integer byId;
    @SerializedName("to_id")
    @Expose
    private Integer toId;
    @SerializedName("rate")
    @Expose
    private String rate;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("photo")
    @Expose
    private Photo photo;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("like")
    @Expose
    private Boolean like;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getById() {
        return byId;
    }

    public void setById(Integer byId) {
        this.byId = byId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getLike() {
        return like;
    }

    public void setLike(Boolean like) {
        this.like = like;
    }

}