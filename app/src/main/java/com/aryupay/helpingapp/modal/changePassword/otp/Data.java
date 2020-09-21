package com.aryupay.helpingapp.modal.changePassword.otp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("otp")
    @Expose
    private Integer otp;
    @SerializedName("user")
    @Expose
    private User user;

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
