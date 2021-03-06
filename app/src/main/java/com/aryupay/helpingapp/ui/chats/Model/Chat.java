package com.aryupay.helpingapp.ui.chats.Model;

import com.aryupay.helpingapp.utils.Tools;

import java.util.Map;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private String userid;

    private boolean isseen;
    private Long timestamp;



    public Chat(String sender, String receiver, String message,String userid,Long timestamp, boolean isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.userid = userid;
        this.timestamp = timestamp;
        this.isseen = isseen;
    }

    public Chat() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
