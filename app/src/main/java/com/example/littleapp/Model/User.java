package com.example.littleapp.Model;

public class User {

    private String id;

    private String imageURl;
    private String username;
    private String status;
    private Chatlist chatlist;


    public User(String id, String imageURl, String username, String status,Chatlist chatlist) {
        this.id = id;
        this.imageURl = imageURl;
        this.username = username;
        this.status = status;
        this.chatlist=chatlist;

    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURl() {
        return imageURl;
    }

    public void setImageURl(String imageURl) {
        this.imageURl = imageURl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Chatlist getChatlist() {
        return chatlist;
    }

    public void setChatlist(Chatlist chatlist) {
        this.chatlist = chatlist;
    }
}
