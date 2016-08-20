package mitya.yahnc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Mitya on 28.07.2016.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "commments.db";
    public static final int DATABASE_VERSION = 3;

    private static final String CREATE_TABLE_STORIES = "CREATE TABLE "
            + FeedStory.STORY_TABLE_NAME + " ( "
            + FeedStory.COLUMN_NAME_STORY_ID + " INTEGER NOT NULL , "
            + FeedStory.COLUMN_NAME_STORY_BY + " TEXT NOT NULL , "
            + FeedStory.COLUMN_NAME_STORY_TITLE + " TEXT NOT NULL , "
            + FeedStory.COLUMN_NAME_STORY_URL + " TEXT , "
            + FeedStory.COLUMN_NAME_STORY_DESCENDANTS + " INTEGER , "
            + FeedStory.COLUMN_NAME_STORY_SCORE + " INTEGER , "
            + FeedStory.COLUMN_NAME_STORY_TIME + " TEXT , "
            + FeedStory.COLUMN_NAME_STORY_KIDS + " TEXT , "
            + FeedStory.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT "
            + FeedStory.COLUMN_NAME_STORY_TEXT + "TEXT );";

    private static final String CREATE_TABLE_COMMENTS = "CREATE TABLE "
            + FeedComment.COMMENTS_TABLE_NAME + " ( "
            + FeedComment.COLUMN_NAME_COMMENT_ID + " INTEGER NOT NULL , "
            + FeedComment.COLUMN_NAME_STORY_ID + " INTEGER NOT NULL , "
            + FeedComment.COLUMN_NAME_COMMENT_BY + " TEXT NOT NULL , "
            + FeedComment.COLUMN_NAME_COMMENT_KIDS + " TEXT , "
            + FeedComment.COLUMN_NAME_COMMENT_PARENT + " INTEGER , "
            + FeedComment.COLUMN_NAME_COMMENT_TEXT + " TEXT , "
            + FeedComment.COLUMN_NAME_COMMENT_TIME + " TEXT , "
            + FeedComment.COLUMN_NAME_COMMENT_NESTINGLEVEL + " INTEGER , "
            + FeedComment.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + "FOREIGN KEY(" + FeedComment.COLUMN_NAME_STORY_ID + ") "
            + "REFERENCES " + FeedStory.STORY_TABLE_NAME
            + "(" + FeedStory.COLUMN_NAME_STORY_ID + "));";

    private static final String DROP_STORIES_TABLE = "DROP TABLE IF EXISTS "
            + FeedStory.STORY_TABLE_NAME;

    private static final String DROP_COMMENTS_TABLE = "DROP TABLE IF EXISTS "
            + FeedComment.COMMENTS_TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STORIES);
        db.execSQL(CREATE_TABLE_COMMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_COMMENTS_TABLE);
        db.execSQL(DROP_STORIES_TABLE);
        onCreate(db);
    }

    public static abstract class FeedStory implements BaseColumns {
        public static final String STORY_TABLE_NAME = "story";
        public static final String COLUMN_NAME_STORY_ID = "story_id";
        public static final String COLUMN_NAME_STORY_BY = "by";
        public static final String COLUMN_NAME_STORY_TITLE = "title";
        public static final String COLUMN_NAME_STORY_URL = "url";
        public static final String COLUMN_NAME_STORY_DESCENDANTS = "descendants";
        public static final String COLUMN_NAME_STORY_SCORE = "score";
        public static final String COLUMN_NAME_STORY_TIME = "time";
        public static final String COLUMN_NAME_STORY_KIDS = "kids";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_STORY_TEXT = "text";
    }

    public static abstract class FeedComment implements BaseColumns {
        public static final String COMMENTS_TABLE_NAME = "comment";
        public static final String COLUMN_NAME_COMMENT_ID = "comment_id";
        public static final String COLUMN_NAME_COMMENT_BY = "by";
        public static final String COLUMN_NAME_COMMENT_KIDS = "kids";
        public static final String COLUMN_NAME_COMMENT_PARENT = "parent";
        public static final String COLUMN_NAME_COMMENT_TEXT = "text";
        public static final String COLUMN_NAME_COMMENT_TIME = "time";
        public static final String COLUMN_NAME_COMMENT_NESTINGLEVEL = "nestingLevel";
        public static final String COLUMN_NAME_STORY_ID = "story_id";
        public static final String COLUMN_NAME_ID = "id";
    }
}
