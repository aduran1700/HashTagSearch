package com.hashtagsearch.model;

public class TwitterUser {

    private String screen_name;
    private String name;
    private String profile_image_url;

    public String getProfileImageUrl() {
        return profile_image_url;
    }

    public String getScreenName() {
        return screen_name;
    }

    public String getName() {
        return name;
    }

}