package mitya.yahnc;

/**
 * Created by Mitya on 23.06.2016.
 */
public class Story {
    public final String by;
    public final String title;
    public final String url;
    public final String type;
    public final int id;
    public final int descendants;
    public final int score;
    public final long time;
    public final int[] kids;

    public Story(String by, Integer descendants, Integer id, int[] kids, Integer score, long time, String title, String type, String url) {
        this.by = by;
        this.descendants = descendants;
        this.id = id;
        this.kids = kids;
        this.score = score;
        this.time = time;
        this.title = title;
        this.type = type;
        this.url = url;
    }
}
