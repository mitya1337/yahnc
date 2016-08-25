package mitya.yahnc.ui;

import mitya.yahnc.domain.Story;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Mitya on 25.08.2016.
 */
public abstract class RemoteStoryFragment extends StoryFragment {

    protected final Observable<Integer> currentStoriesObservable;

    protected RemoteStoryFragment(Observable<Integer> currentStoriesObservable) {
        this.currentStoriesObservable = currentStoriesObservable;
    }

    @Override
    public Observable<Story> getStories(int currentPage) {
        return currentStoriesObservable.
                skip(STORIES_PER_PAGE * (currentPage - 1)).
                take(STORIES_PER_PAGE).
                flatMap(id -> storyService.getStory(id).subscribeOn(Schedulers.io()).onErrorResumeNext(Observable.<Story>empty()));
    }
}
