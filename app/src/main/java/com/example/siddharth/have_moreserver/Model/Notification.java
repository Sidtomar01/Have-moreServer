package com.example.siddharth.have_moreserver.Model;

/**
 * Created by Siddharth on 24-12-2017.
 */

public class Notification {
    public String body;
    public String title;

    public Notification(String s, String body) {
        this.body = body;
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
