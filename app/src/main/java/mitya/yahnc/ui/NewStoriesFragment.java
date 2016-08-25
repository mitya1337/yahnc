package mitya.yahnc.ui;

import mitya.yahnc.network.StoryService;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Mitya on 25.08.2016.
 */
public class NewStoriesFragment extends RemoteStoryFragment {
    public NewStoriesFragment() {
        super(StoryService.getInstance().getNewStories().
                flatMap(stories -> Observable.from(stories).subscribeOn(Schedulers.io())));
    }
}
