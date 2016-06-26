package mitya.yahnc;


import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Mitya on 26.06.2016.
 */
public class StoriesService {
    private Context context;

    public StoriesService(Context context) {
        this.context = context;
    }

    public Observable createObservable() {
        return Observable.create(new Observable.OnSubscribe<List<Story>>() {
            @Override
            public void call(Subscriber<? super List<Story>> subscriber) {
                JsonStoryParser parser = new JsonStoryParser();
                Story story ;
                List<Story> stories = new ArrayList<Story>();
                try {
                    story = parser.parse(context.getResources().openRawResource(R.raw.json));
                    for (int i = 0; i < 20; i++) {
                        stories.add(story);
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(stories);
                        subscriber.onCompleted();
                    }
                } catch (IOException e) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        });
    }
}
