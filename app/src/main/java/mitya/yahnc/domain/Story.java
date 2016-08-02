package mitya.yahnc.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mitya on 23.06.2016.
 */
public class Story implements Parcelable {
    public final String by;
    public final String title;
    public final String url;
    public final String type;
    public final int id;
    public final int descendants;
    public final int score;
    public final long time;
    public final Integer[] kids;

    public Story(Integer id, String by, String title, String url, Integer descendants, Integer score, long time, Integer[] kids, String type) {
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

    protected Story(Parcel in) {
        by = in.readString();
        title = in.readString();
        url = in.readString();
        type = in.readString();
        id = in.readInt();
        descendants = in.readInt();
        score = in.readInt();
        time = in.readLong();
        int kidsLength = in.readInt();
        if (kidsLength != 0) {
            int[] intKids = new int[kidsLength];
            in.readIntArray(intKids);
            kids = new Integer[intKids.length];
            for (int i = 0; i < intKids.length; i++) {
                kids[i] = intKids[i];
            }
        } else kids = null;
    }

    public static final Creator<Story> CREATOR = new Creator<Story>() {
        @Override
        public Story createFromParcel(Parcel in) {
            return new Story(in);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(by);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(type);
        dest.writeInt(id);
        dest.writeInt(descendants);
        dest.writeInt(score);
        dest.writeLong(time);
        if (kids != null) {
            dest.writeInt(kids.length);
            int[] intKids = new int[kids.length];
            for (int i = 0; i < kids.length; i++) {
                intKids[i] = kids[i];
            }
            dest.writeIntArray(intKids);
        } else {
            dest.writeInt(0);
        }
    }
}
