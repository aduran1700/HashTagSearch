package com.hashtagsearch.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class TwitterSyncService extends Service {
    private static final Object mSyncAdapterLock = new Object();
    private static TwitterSyncAdapter mTwitterSyncAdapter = null;

    public TwitterSyncService() {
        super();
    }

    @Override
    public void onCreate() {
        Log.d("TwitterSyncAdapter", "onCreate - TwitterSyncService");
        synchronized (mSyncAdapterLock) {
            if (mTwitterSyncAdapter == null) {
                mTwitterSyncAdapter = new TwitterSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mTwitterSyncAdapter.getSyncAdapterBinder();
    }
}
