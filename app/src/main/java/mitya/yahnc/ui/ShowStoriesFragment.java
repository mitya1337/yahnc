package mitya.yahnc.ui;

import mitya.yahnc.network.StoryService;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Mitya on 25.08.2016.
 */
public class ShowStoriesFragment extends RemoteStoryFragment {
    @Override
    protected Observable<Integer> getCurrentStories() {
        return StoryService.getInstance().getShowStories().
                flatMap(stories -> Observable.from(stories).subscribeOn(Schedulers.io()));
    }
}
