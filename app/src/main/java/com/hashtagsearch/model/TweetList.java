package com.hashtagsearch.model;

import com.google.gson.annotations.SerializedName;
import com.hashtagsearch.model.Tweet;

import java.util.List;


public class TweetList {

    @SerializedName("statuses")
    private List<Tweet> tweets;

    public List<Tweet> getTweets() {
        return tweets;
    }
}
