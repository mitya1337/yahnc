package mitya.yahnc.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mitya.yahnc.domain.Comment;
import mitya.yahnc.db.DbHelper.FeedComment;
import mitya.yahnc.utils.FormatUtils;
import rx.Observable;

/**
 * Created by Mitya on 31.07.2016.
 */
public class CommentsRepository extends Repository<Comment> {
    private static final String SELECTION = "story_id = ?";

    public CommentsRepository(DbHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    public void saveItem(Comment comment) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedComment.COLUMN_NAME_COMMENT_ID, comment.id);
        values.put(FeedComment.COLUMN_NAME_COMMENT_BY, comment.by);
        values.put(FeedComment.COLUMN_NAME_COMMENT_KIDS, Arrays.toString(comment.kids));
        values.put(FeedComment.COLUMN_NAME_COMMENT_PARENT, comment.parent);
        values.put(FeedComment.COLUMN_NAME_COMMENT_TEXT, comment.text);
        values.put(FeedComment.COLUMN_NAME_COMMENT_TIME, Long.toString(comment.time));
        values.put(FeedComment.COLUMN_NAME_COMMENT_NESTINGLEVEL, comment.nestingLevel);
        values.put(FeedComment.COLUMN_NAME_STORY_ID, comment.storyId);
        db.insert(FeedComment.COMMENTS_TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public Observable<Comment> find(String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(FeedComment.COMMENTS_TABLE_NAME, null, selection, selectionArgs, null, null, FeedComment.COLUMN_NAME_ID + " DESC");
        List<Comment> comments = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Comment comment = new Comment(cursor.getInt(0), cursor.getString(2)
                        , FormatUtils.stringToArray(cursor.getString(3)), cursor.getInt(4), cursor.getString(5)
                        , Long.parseLong(cursor.getString(6)), "comment");
                comment.nestingLevel = cursor.getInt(7);
                comments.add(comment);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return Observable.from(comments);
    }
}
