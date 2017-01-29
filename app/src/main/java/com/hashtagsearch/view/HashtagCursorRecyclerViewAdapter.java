package com.hashtagsearch.view;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hashtagsearch.OnFragmentInteractionListener;
import com.hashtagsearch.R;
import com.hashtagsearch.data.TweetColumns;
import com.hashtagsearch.data.HashtagColumns;


public class HashtagCursorRecyclerViewAdapter extends CursorRecyclerViewAdapter<HashtagViewHolder> {

    private final OnFragmentInteractionListener mListener;
    private final Context mContext;

    public HashtagCursorRecyclerViewAdapter(Context context, OnFragmentInteractionListener listener, Cursor cursor) {
        super(context, cursor);

        this.mContext = context;
        mListener = listener;
    }


    @Override
    public HashtagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hashtag_item, parent, false);
        return new HashtagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HashtagViewHolder viewHolder, final Cursor cursor) {
        DatabaseUtils.dumpCursor(cursor);

        final String hashtag = cursor.getString(cursor.getColumnIndex(HashtagColumns.COLUMN_HASHTAG_TEXT));
        viewHolder.mHashtagText.setText(hashtag);
        viewHolder.id = cursor.getInt(cursor.getColumnIndex(TweetColumns._ID));

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(hashtag);
                }
            }
        });
    }
}
