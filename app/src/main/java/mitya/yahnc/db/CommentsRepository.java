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

/**
 * Created by Mitya on 31.07.2016.
 */
public class CommentsRepository {
    private static final String SELECTION = "story_id = ?";

    private SQLiteDatabase db;
    private DbHelper dbHelper;

    public CommentsRepository(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void createComment(Comment comment, Integer storyId) {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedComment.COLUMN_NAME_COMMENT_ID, comment.id);
        values.put(FeedComment.COLUMN_NAME_COMMENT_BY, comment.by);
        values.put(FeedComment.COLUMN_NAME_COMMENT_KIDS, Arrays.toString(comment.kids));
        values.put(FeedComment.COLUMN_NAME_COMMENT_PARENT, comment.parent);
        values.put(FeedComment.COLUMN_NAME_COMMENT_TEXT, comment.text);
        values.put(FeedComment.COLUMN_NAME_COMMENT_TIME, Long.toString(comment.time));
        values.put(FeedComment.COLUMN_NAME_COMMENT_NESTINGLEVEL, comment.nestingLevel);
        values.put(FeedComment.COLUMN_NAME_STORY_ID, storyId);
        db.insert(FeedComment.COMMENTS_TABLE_NAME, null, values);
        db.close();
    }

    public List<Comment> readAllComments(Integer storyId) {
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(FeedComment.COMMENTS_TABLE_NAME, null, SELECTION, new String[]{Integer.toString(storyId)}, null, null, FeedComment.COLUMN_NAME_STORY_ID + " ASC");
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
        return comments;
    }
}
