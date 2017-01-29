package com.hashtagsearch.service;

import com.hashtagsearch.model.AccessToken;
import com.hashtagsearch.model.TweetList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface TwitterService {
    @FormUrlEncoded
    @POST("/oauth2/token")
    Call<AccessToken> getAccessToken(
            @Field("grant_type") String grantType
    );

    @GET("/1.1/search/tweets.json?result_type=popular&count=10")
    Call<TweetList> getTweetsWithHashtag(
            @Query("q") String hashtag
    );

}