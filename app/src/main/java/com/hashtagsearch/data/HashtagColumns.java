package com.hashtagsearch.data;


import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;

public interface HashtagColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement String _ID = "_id";
    @DataType(DataType.Type.TEXT) String COLUMN_HASHTAG_TEXT = "hashtag_text";

}
