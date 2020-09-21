package com.aryupay.helpingapp.modal.changePassword.otp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("city_id")
    @Expose
    private Integer cityId;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("profession_id")
    @Expose
    private Integer professionId;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("otp")
    @Expose
    private String otp;
    @SerializedName("verify_status")
    @Expose
    private String verifyStatus;
    @SerializedName("show_contact")
    @Expose
    private String showContact;
    @SerializedName("show_mail_id")
    @Expose
    private String showMailId;
    @SerializedName("enable_personal_chat")
    @Expose
    private String enablePersonalChat;
    @SerializedName("enable_comment_on_post")
    @Expose
    private String enableCommentOnPost;
    @SerializedName("show_post_comment_to_all")
    @Expose
    private String showPostCommentToAll;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("fullname")
    @Expose
    private String fullname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("remember_token")
    @Expose
    private Object rememberToken;
    @SerializedName("google_id")
    @Expose
    private Object googleId;
    @SerializedName("facebook_id")
    @Expose
    private Object facebookId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getProfessionId() {
        return professionId;
    }

    public void setProfessionId(Integer professionId) {
        this.professionId = professionId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getVerifyStatus() {
        return verifyStatus;
    }

    public void setVerifyStatus(String verifyStatus) {
        this.verifyStatus = verifyStatus;
    }

    public String getShowContact() {
        return showContact;
    }

    public void setShowContact(String showContact) {
        this.showContact = showContact;
    }

    public String getShowMailId() {
        return showMailId;
    }

    public void setShowMailId(String showMailId) {
        this.showMailId = showMailId;
    }

    public String getEnablePersonalChat() {
        return enablePersonalChat;
    }

    public void setEnablePersonalChat(String enablePersonalChat) {
        this.enablePersonalChat = enablePersonalChat;
    }

    public String getEnableCommentOnPost() {
        return enableCommentOnPost;
    }

    public void setEnableCommentOnPost(String enableCommentOnPost) {
        this.enableCommentOnPost = enableCommentOnPost;
    }

    public String getShowPostCommentToAll() {
        return showPostCommentToAll;
    }

    public void setShowPostCommentToAll(String showPostCommentToAll) {
        this.showPostCommentToAll = showPostCommentToAll;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Object getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(Object rememberToken) {
        this.rememberToken = rememberToken;
    }

    public Object getGoogleId() {
        return googleId;
    }

    public void setGoogleId(Object googleId) {
        this.googleId = googleId;
    }

    public Object getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(Object facebookId) {
        this.facebookId = facebookId;
    }

}