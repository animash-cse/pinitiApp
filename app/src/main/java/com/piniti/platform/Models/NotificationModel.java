package com.piniti.platform.Models;

public class NotificationModel {
    public String to;
    public String text;
    //public String time;


    public NotificationModel(){

    }

    public NotificationModel(String to) {
        this.to = to;
    }

    public String getTo() {
        return to;
    }


    public String getText() {
        return text;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setText(String text) {
        this.text = text;
    }

    /*public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }*/
}
