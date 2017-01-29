package com.hashtagsearch.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;


public interface TweetColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(DataType.Type.TEXT) String COLUMN_TWEET_ID = "tweet_id";
    @DataType(DataType.Type.TEXT) String COLUMN_TWEET_TEXT = "tweet_text";
    @DataType(DataType.Type.TEXT) String COLUMN_TWEET_CREATED_AT = "tweet_created_at";
    @DataType(DataType.Type.BLOB) String COLUMN_TWEET_USER = "tweet_user";
    @DataType(DataType.Type.TEXT) String COLUMN_TWEET_SEARCH = "tweet_search";
}
