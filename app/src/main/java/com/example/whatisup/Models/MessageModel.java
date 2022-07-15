package com.example.whatisup.Models;

public class MessageModel {
    String uId,message;
    Long timeStamp;
    int reaction;


    public MessageModel(String uId, String message,Long timeStamp,int reaction) {
        this.uId = uId;
        this.message = message;
        this.timeStamp=timeStamp;
        this.reaction=reaction;

    }

    public MessageModel() {
    }

    public String getUId() {
        return uId;
    }

    public void setUId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getReaction() {
        return reaction;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }
}
