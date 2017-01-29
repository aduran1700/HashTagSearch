package com.hashtagsearch.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hashtagsearch.R;
import com.hashtagsearch.data.HashtagColumns;
import com.hashtagsearch.data.TweetColumns;
import com.hashtagsearch.data.TweetProvider;
import com.hashtagsearch.model.AccessToken;
import com.hashtagsearch.model.Tweet;
import com.hashtagsearch.model.TweetList;
import com.hashtagsearch.service.TwitterService;
import com.hashtagsearch.service.TwitterServiceGenerator;

import java.util.ArrayList;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TwitterSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String TAG = TwitterSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static AccessToken mAccessToken;

    public TwitterSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "Starting sync");
        if(mAccessToken != null) {
            ContentResolver contentResolver = getContext().getContentResolver();
            Cursor cursor = contentResolver.query(TweetProvider.Hashtags.CONTENT_URI, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    final String tweetSearch = cursor.getString(cursor.getColumnIndex(HashtagColumns.COLUMN_HASHTAG_TEXT));


                    TwitterService twitterService = TwitterServiceGenerator.createService(TwitterService.class, mAccessToken);
                    final Call<TweetList> call = twitterService.getTweetsWithHashtag(tweetSearch);
                    call.enqueue(new Callback<TweetList>() {
                        @Override
                        public void onResponse(Call<TweetList> call, Response<TweetList> response) {
                            ArrayList<Tweet> tweets = (ArrayList<Tweet>) response.body().getTweets();
                            ContentResolver contentResolver = getContext().getContentResolver();
                            String[] selectionArgs = {tweetSearch};
                            contentResolver.delete(TweetProvider.Tweets.CONTENT_URI, TweetColumns.COLUMN_TWEET_SEARCH + " = ?", selectionArgs);

                            addTweets(tweets, tweetSearch, contentResolver);

                        }

                        @Override
                        public void onFailure(Call<TweetList> call, Throwable t) {
                            Log.d(TAG, t.toString());
                        }
                    });


                }
                cursor.close();
            }
        }
    }

    public static void addTweets(ArrayList<Tweet> tweets, String tweetSearch, ContentResolver contentResolver) {
        ContentValues contentValues;
        Gson gson = new Gson();
        Vector<ContentValues> contentValuesVector = new Vector<>(tweets.size());

        for(Tweet tweet : tweets) {
            contentValues = new ContentValues();
            contentValues.put(TweetColumns.COLUMN_TWEET_ID, tweet.getId());
            contentValues.put(TweetColumns.COLUMN_TWEET_CREATED_AT, tweet.getCreatedAt());
            contentValues.put(TweetColumns.COLUMN_TWEET_TEXT, tweet.getText());
            contentValues.put(TweetColumns.COLUMN_TWEET_USER, gson.toJson(tweet.getUser()));
            contentValues.put(TweetColumns.COLUMN_TWEET_SEARCH, tweetSearch);

            contentValuesVector.add(contentValues);
        }

        ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
        contentValuesVector.toArray(contentValuesArray);
        contentResolver.bulkInsert(TweetProvider.Tweets.CONTENT_URI, contentValuesArray);
    }

    public static void setAccessToken(AccessToken accessToken) {
        mAccessToken = accessToken;
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        TwitterSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }


}
