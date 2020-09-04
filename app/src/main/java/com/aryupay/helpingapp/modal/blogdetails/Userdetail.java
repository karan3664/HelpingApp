package com.aryupay.helpingapp.modal.blogdetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Userdetail {

    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("contact")
    @Expose
    private String contact;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

}