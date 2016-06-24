package mitya.yahnc;

import java.util.Arrays;

/**
 * Created by Mitya on 23.06.2016.
 */
public class Story {
    private String by, title, url, type;
    private Integer id, descendants, score;
    private long time;
    private Integer[] kids;

    public Story() {
    }

    public Story(String by, Integer descendants, Integer id, Integer[] kids, Integer score, long time, String title, String url) {
        this.by = by;
        this.descendants = descendants;
        this.id = id;
        this.kids = kids;
        this.score = score;
        this.time = time;
        this.title = title;
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBy() {
        return by;
    }

    public Integer getDescendants() {
        return descendants;
    }

    public Integer getId() {
        return id;
    }

    public Integer[] getKids() {
        return kids;
    }

    public Integer getScore() {
        return score;
    }

    public long getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public void setDescendants(Integer descendants) {
        this.descendants = descendants;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setKids(Integer[] kids) {
        this.kids = kids;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
