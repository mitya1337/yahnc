package mitya.yahnc.db;

import rx.Observable;

/**
 * Created by Mitya on 14.08.2016.
 */
public abstract class Repository<T> {

    protected final DbHelper dbHelper;

    protected Repository(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public abstract void saveItem(T item);

    public abstract Observable<T> find(String selection, String[] selectionArgs);

}
