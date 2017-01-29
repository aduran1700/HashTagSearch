package com.hashtagsearch.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hashtagsearch.R;

public class TweetViewHolder extends RecyclerView.ViewHolder {
    public ImageView mTweetUserImage;
    public TextView mTweetUserName;
    public TextView mTweetUserScreenName;
    public TextView mTweetText;
    public int id;

    public TweetViewHolder(View view) {
        super(view);

        mTweetUserImage = (ImageView) view.findViewById(R.id.tweet_user_image);
        mTweetUserName = (TextView) view.findViewById(R.id.tweet_user_name);
        mTweetText = (TextView) view.findViewById(R.id.tweet_text);
        mTweetUserScreenName = (TextView) view.findViewById(R.id.tweet_user_screen_name);
    }
}
