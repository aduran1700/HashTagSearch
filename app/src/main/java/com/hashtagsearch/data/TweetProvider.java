package com.hashtagsearch.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = TweetProvider.AUTHORITY, database = TweetDatabase.class)
public class TweetProvider {
    public static final String AUTHORITY = "com.hashtagsearch.data.TweetProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String TWEETS = "tweets";
        String HASHTAGS = "hashtags";
    }

    private static Uri buildUri(String ... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }

        return builder.build();
    }

    @TableEndpoint(table = TweetDatabase.TWEETS)
    public static class Tweets{
        @ContentUri(
                path = Path.TWEETS,
                type = "vnd.android.cursor.dir/tweets",
                defaultSort = TweetColumns._ID
        )
        public static final Uri CONTENT_URI = buildUri(Path.TWEETS);

        @InexactContentUri(
                name = "TWEET_ID",
                path = Path.TWEETS + "/#",
                type = "vnd.android.cursor.item/tweet",
                whereColumn = TweetColumns._ID,
                pathSegment = 1
        )
        public static Uri withId(long id){
            return buildUri(Path.TWEETS, String.valueOf(id));
        }
    }

    @TableEndpoint(table = TweetDatabase.HASHTAGS)
    public static class Hashtags{
        @ContentUri(
                path = Path.HASHTAGS,
                type = "vnd.android.cursor.dir/hashtags",
                defaultSort = HashtagColumns._ID
        )
        public static final Uri CONTENT_URI = buildUri(Path.HASHTAGS);

        @InexactContentUri(
                name = "_ID",
                path = Path.HASHTAGS + "/#",
                type = "vnd.android.cursor.item/hashtag",
                whereColumn = HashtagColumns._ID,
                pathSegment = 1
        )
        public static Uri withId(long id){
            return buildUri(Path.HASHTAGS, String.valueOf(id));
        }
    }
}
