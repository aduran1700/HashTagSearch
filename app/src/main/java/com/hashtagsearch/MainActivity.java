package com.hashtagsearch;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hashtagsearch.data.TweetColumns;
import com.hashtagsearch.data.HashtagColumns;
import com.hashtagsearch.data.TweetProvider;
import com.hashtagsearch.model.AccessToken;
import com.hashtagsearch.model.Tweet;
import com.hashtagsearch.model.TweetList;
import com.hashtagsearch.service.TwitterService;
import com.hashtagsearch.service.TwitterServiceGenerator;
import com.hashtagsearch.sync.TwitterSyncAdapter;

import java.util.ArrayList;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {
    private static final String TAG = MainActivity.class.getName();
    private static final String HASHTAG_FRAGMENT_TAG = "HASHTAGS";
    private static final String TWEETS_FRAGMENT_TAG = "TWEETS";
    private TwitterService mTwitterService;
    private AccessToken mAccessToken;
    private boolean mIsConnected;
    private ProgressBar mProgressbar;
    private View mView;

    private BroadcastReceiver mConnectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            mIsConnected = networkInfo != null &&
                    networkInfo.isConnectedOrConnecting();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressbar = (ProgressBar) findViewById(R.id.progress_bar);
        mView = findViewById(R.id.fragment_container);
        TwitterSyncAdapter.initializeSyncAdapter(this);

        HashtagListFragment hashtagListFragment = new HashtagListFragment();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, hashtagListFragment, HASHTAG_FRAGMENT_TAG)
                    .commit();
        }
    }

    public void onResume() {
        if(mAccessToken == null)
            getAccessToken();
        registerReceiver(mConnectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        super.onResume();
    }

    public void onPause() {
        unregisterReceiver(mConnectivityReceiver);
        super.onPause();
    }

    private void getAccessToken() {
        loadView();

        mTwitterService = TwitterServiceGenerator.createService(TwitterService.class);
        final Call<AccessToken> call = mTwitterService.getAccessToken("client_credentials");
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                mAccessToken = response.body();
                TwitterSyncAdapter.setAccessToken(mAccessToken);
                loadView();
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.d(TAG, t.toString());
                loadView();
                checkConnection(null);
            }
        });
    }

    private void getHashTag(AccessToken accessToken,  String search) {
        final String tweetSearch = search;
        mTwitterService = TwitterServiceGenerator.createService(TwitterService.class, accessToken);
        final Call<TweetList> call = mTwitterService.getTweetsWithHashtag(tweetSearch);
        call.enqueue(new Callback<TweetList>() {
            @Override
            public void onResponse(Call<TweetList> call, Response<TweetList> response) {
                ArrayList<Tweet> tweets = (ArrayList<Tweet>) response.body().getTweets();
                ContentValues contentValues = new ContentValues();
                ContentResolver contentResolver = getApplicationContext().getContentResolver();
                String[] selectionArgs = {tweetSearch};

                Cursor cursor = contentResolver.query(TweetProvider.Hashtags.CONTENT_URI, null, HashtagColumns.COLUMN_HASHTAG_TEXT + " = ?", selectionArgs, null );

                if(cursor != null && cursor.moveToFirst()) {
                    showTweetsFragments(tweetSearch);
                    cursor.close();
                } else {
                    contentValues.put(HashtagColumns.COLUMN_HASHTAG_TEXT, tweetSearch);
                    contentResolver.insert(TweetProvider.Hashtags.CONTENT_URI, contentValues);


                    TwitterSyncAdapter.addTweets(tweets, tweetSearch, contentResolver);
                    showTweetsFragments(tweetSearch);
                }
            }

            @Override
            public void onFailure(Call<TweetList> call, Throwable t) {
                Log.d(TAG, t.toString());
                loadView();
                checkConnection(tweetSearch);
            }
        });
    }

    @Override
    public void onListFragmentInteraction(String hashtag) {
        getHashTag(mAccessToken, hashtag);
    }

    @Override
    public void onSearchInteraction(String hashtag) {
        getHashTag(mAccessToken, hashtag);
    }

    private void showTweetsFragments(String search) {
        Bundle args = new Bundle();
        args.putString(TweetColumns.COLUMN_TWEET_SEARCH, search);
        TweetListFragment tweetListFragment = new TweetListFragment();
        tweetListFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, tweetListFragment, TWEETS_FRAGMENT_TAG )
                .addToBackStack(HASHTAG_FRAGMENT_TAG)
                .commit();
    }

    private void checkConnection(String search) {
        loadView();

        if(search != null)
            if(!mIsConnected) {
                ContentResolver contentResolver = getApplicationContext().getContentResolver();
                String[] selectionArgs = {search};

                Cursor cursor = contentResolver.query(TweetProvider.Hashtags.CONTENT_URI, null, HashtagColumns.COLUMN_HASHTAG_TEXT + " = ?", selectionArgs, null );
                if(cursor != null && cursor.moveToFirst()) {
                    cursor.close();
                    showTweetsFragments(search);
                } else
                    Toast.makeText(getApplicationContext(), "Internet connection lost", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Internet connection lost", Toast.LENGTH_SHORT).show();
            }
        else
            Toast.makeText(getApplicationContext(), "Internet connection lost", Toast.LENGTH_SHORT).show();
    }

    private void loadView() {
        if(mView.getVisibility() == View.VISIBLE) {
            mView.setVisibility(View.GONE);
            mProgressbar.setVisibility(View.VISIBLE);
        } else {
            mView.setVisibility(View.VISIBLE);
            mProgressbar.setVisibility(View.GONE);
        }
    }
}
