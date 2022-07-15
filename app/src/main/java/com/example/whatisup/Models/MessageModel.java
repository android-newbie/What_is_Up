package com.example.whatisup.Models;

public class MessageModel {
    String uId,message,messageId;
    Long timeStamp;
    int reaction=-1;


    public MessageModel(String uId, String message,Long timeStamp) {
        this.uId = uId;
        this.message = message;
        this.timeStamp=timeStamp;


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


    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
