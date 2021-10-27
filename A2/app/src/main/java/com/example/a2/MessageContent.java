package com.example.a2;

public class MessageContent {
    private boolean isSent; // true if send, false if recived
    private String content;
    public MessageContent(String content, boolean isSent){
        this.content = content;
        this.isSent= isSent;
    }

    public String getContent() {
        return content;
    }

    public boolean isSent() {
        return isSent;
    }
}