package com.piniti.platform.Models;

public class NotificationModel {
    private String from;
    private String to;
    private String text;
    private String time;

    public NotificationModel(){

    }

    public NotificationModel(String from, String to, String text, String time) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
