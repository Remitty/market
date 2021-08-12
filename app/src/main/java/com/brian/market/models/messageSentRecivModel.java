package com.brian.market.models;

import com.brian.market.utills.UrlController;

public class messageSentRecivModel {

    private String name;
    private String active;
    private String topic;
    private String price;
    private String tumbnail;
    private String id;
    private String sender_id;
    private String receiver_id;
    private String isBlock;
    private String type;
    private String message;
    private boolean isMessageRead;
    private boolean isMine;
    private String profile;
    private String chatTime;


    public String getIsBlock() {
        return isBlock;
    }

    public void setIsBlock(String isBlock) {
        this.isBlock = isBlock;
    }
    public boolean isMessageRead() {
        return isMessageRead;
    }

    public void setMessageRead(boolean messageRead) {
        isMessageRead = messageRead;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTumbnail() {
        return tumbnail;
    }

    public void setTumbnail(String tumbnail) {
        if(!tumbnail.startsWith("http"))
            tumbnail = UrlController.IP_ADDRESS+"storage/"+tumbnail;
        this.tumbnail = tumbnail;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        if(!profile.startsWith("http"))
            profile = UrlController.IP_ADDRESS+"storage/"+profile;
        this.profile = profile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isMine() {return isMine;}

    public void setMine(boolean mine){this.isMine = mine;}

    public String getChatTime() {
        return chatTime;
    }

    public void setChatTime(String time) {
        this.chatTime = time;
    }
}
