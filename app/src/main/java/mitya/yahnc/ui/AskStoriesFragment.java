package mitya.yahnc.ui;

import mitya.yahnc.network.StoryService;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Mitya on 25.08.2016.
 */
public class AskStoriesFragment extends RemoteStoryFragment {
    public AskStoriesFragment() {
        super(StoryService.getInstance().getService().getAskStories().
                flatMap(stories -> Observable.from(stories).subscribeOn(Schedulers.io())));
    }
}
