package com.hashtagsearch.view;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.hashtagsearch.R;
import com.hashtagsearch.data.TweetColumns;
import com.hashtagsearch.model.TwitterUser;
import com.squareup.picasso.Picasso;

import java.io.StringReader;


public class TweetCursorRecyclerViewAdapter extends CursorRecyclerViewAdapter<TweetViewHolder> {
    private final Context mContext;

    public TweetCursorRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.mContext = context;
    }

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tweet_item, parent, false);
        return new TweetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TweetViewHolder viewHolder, final Cursor cursor) {
        DatabaseUtils.dumpCursor(cursor);
        Gson gson = new Gson();
        String json = new String(cursor.getBlob(cursor.getColumnIndex(TweetColumns.COLUMN_TWEET_USER)));
        JsonReader reader = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        TwitterUser twitterUser = gson.fromJson(reader, new TypeToken<TwitterUser>(){}.getType());

        Picasso.with(mContext).load(twitterUser.getProfileImageUrl()).into(viewHolder.mTweetUserImage);
        viewHolder.mTweetUserName.setText(twitterUser.getName());
        String userScreenName = " @" + twitterUser.getScreenName();
        viewHolder.mTweetUserScreenName.setText(userScreenName);
        viewHolder.mTweetText.setText(cursor.getString(cursor.getColumnIndex(TweetColumns.COLUMN_TWEET_TEXT)));
        viewHolder.id = cursor.getInt(cursor.getColumnIndex(TweetColumns._ID));

    }
}
