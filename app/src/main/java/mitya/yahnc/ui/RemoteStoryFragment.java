package mitya.yahnc.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mitya.yahnc.domain.Story;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Mitya on 25.08.2016.
 */
public abstract class RemoteStoryFragment extends StoryFragment {

    protected Observable<Integer> currentStoriesObservable;

    @Override
    public Observable<Story> getStories(int currentPage) {
        return currentStoriesObservable.
                skip(STORIES_PER_PAGE * (currentPage - 1)).
                take(STORIES_PER_PAGE).
                flatMap(id -> storyService.getStory(id).subscribeOn(Schedulers.io()).onErrorResumeNext(Observable.<Story>empty()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentStoriesObservable = getCurrentStories();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected abstract Observable<Integer> getCurrentStories();
}
