package com.hashtagsearch;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hashtagsearch.data.TweetColumns;
import com.hashtagsearch.data.TweetProvider;
import com.hashtagsearch.view.TweetCursorRecyclerViewAdapter;

public class TweetListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CURSOR_LOADER_ID = 1;
    private TweetCursorRecyclerViewAdapter tweetCursorRecyclerViewAdapter;
    private RecyclerView recyclerView;
    private String mSearch;

    public TweetListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mSearch = getArguments().getString(TweetColumns.COLUMN_TWEET_SEARCH);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        if(getActivity().findViewById(R.id.progress_bar).getVisibility() == View.VISIBLE) {
            getActivity().findViewById(R.id.progress_bar).setVisibility(View.GONE);
            getActivity().findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);

        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tweetCursorRecyclerViewAdapter = new TweetCursorRecyclerViewAdapter(getActivity(), null);
        recyclerView.setAdapter(tweetCursorRecyclerViewAdapter);

        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] selectionArgs = {mSearch};
        return new CursorLoader(getActivity(), TweetProvider.Tweets.CONTENT_URI,
                null,
                TweetColumns.COLUMN_TWEET_SEARCH + " = ?",
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        tweetCursorRecyclerViewAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        tweetCursorRecyclerViewAdapter.swapCursor(null);
    }
}
