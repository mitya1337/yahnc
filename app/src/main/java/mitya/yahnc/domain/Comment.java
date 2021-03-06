package mitya.yahnc.domain;

/**
 * Created by Mitya on 16.07.2016.
 */
public class Comment {
    public final String by;
    public final int id;
    public final Integer[] kids;
    public final int parent;
    public final String text;
    public final long time;
    public final String type;
    public int nestingLevel;
    public int storyId;

    public Comment(int id,String by,  Integer[] kids, int parent, String text, long time, String type) {
        this.by = by;
        this.id = id;
        this.kids = kids;
        this.parent = parent;
        this.text = text;
        this.time = time;
        this.type = type;
        nestingLevel = 0;
    }
}
