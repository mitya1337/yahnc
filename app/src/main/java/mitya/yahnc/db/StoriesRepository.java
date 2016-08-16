package mitya.yahnc.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mitya.yahnc.domain.Story;
import mitya.yahnc.utils.FormatUtils;
import rx.Observable;

import static mitya.yahnc.db.DbHelper.FeedStory;

/**
 * Created by Mitya on 31.07.2016.
 */
public class StoriesRepository extends Repository<Story> {

    private static final String SELECTION = "id = ?";

    private SQLiteDatabase db;

    public StoriesRepository(DbHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    public void saveItem(Story story) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedStory.COLUMN_NAME_STORY_ID, story.id);
        values.put(FeedStory.COLUMN_NAME_STORY_BY, story.by);
        values.put(FeedStory.COLUMN_NAME_STORY_TITLE, story.title);
        values.put(FeedStory.COLUMN_NAME_STORY_URL, story.url);
        values.put(FeedStory.COLUMN_NAME_STORY_DESCENDANTS, story.descendantsCount);
        values.put(FeedStory.COLUMN_NAME_STORY_SCORE, story.score);
        values.put(FeedStory.COLUMN_NAME_STORY_TIME, Long.toString(story.time));
        values.put(FeedStory.COLUMN_NAME_STORY_KIDS, Arrays.toString(story.kids));
        db.insert(FeedStory.STORY_TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public Observable<Story> find(String selection, String[] selectionArgs) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(FeedStory.STORY_TABLE_NAME, null, selection, selectionArgs, null, null, FeedStory.COLUMN_NAME_ID + " DESC");
        List<Story> stories = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Story story = new Story(cursor.getInt(0), cursor.getString(1), cursor.getString(2)
                        , cursor.getString(3), cursor.getInt(4), cursor.getInt(5), Long.parseLong(cursor.getString(6))
                        , FormatUtils.stringToArray(cursor.getString(7)), "story");
                stories.add(story);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return Observable.from(stories);
    }

    @Nullable
    public Story readStory(Integer id) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(FeedStory.STORY_TABLE_NAME, null, SELECTION, new String[]{Integer.toString(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            Story story = new Story(cursor.getInt(0), cursor.getString(1), cursor.getString(2)
                    , cursor.getString(3), cursor.getInt(4), cursor.getInt(5), Long.parseLong(cursor.getString(6))
                    , FormatUtils.stringToArray(cursor.getString(7)), "story");
            cursor.close();
            db.close();
            return story;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }
}
