package com.hashtagsearch.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = TweetDatabase.VERSION)
public final class TweetDatabase {
    private TweetDatabase() {}

    public static final int VERSION = 1;

    @Table(TweetColumns.class)
    public static final String TWEETS = "tweets";

    @Table(HashtagColumns.class)
    public static final String HASHTAGS = "hashtags";
}
