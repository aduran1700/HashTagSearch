package com.hashtagsearch.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hashtagsearch.R;


public class HashtagViewHolder extends RecyclerView.ViewHolder {
    public TextView mHashtagText;
    public View mView;
    public int id;


    public HashtagViewHolder(View view) {
        super(view);
        mHashtagText = (TextView) view.findViewById(R.id.hashtag_text);
        mView = view;

    }
}
