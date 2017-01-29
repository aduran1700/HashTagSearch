package com.hashtagsearch.model;


public class Tweet {

    private String created_at;
    private String id;
    private String text;
    private TwitterUser user;

    public String getCreatedAt() {
        return created_at;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public TwitterUser getUser() {
        return user;
    }

}
