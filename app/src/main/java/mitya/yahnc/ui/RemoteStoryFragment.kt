package mitya.yahnc.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import mitya.yahnc.domain.Story
import mitya.yahnc.network.HnService

/**
 * Created by Mitya on 25.08.2016.
 */
abstract class RemoteStoryFragment : StoryFragment() {

    private lateinit var currentStoriesObservable: Observable<Int>

    public override fun getStories(currentPage: Int): Observable<Story> {
        return currentStoriesObservable.skip(StoryFragment.STORIES_PER_PAGE * (currentPage - 1))
                .take(StoryFragment.STORIES_PER_PAGE)
                .flatMap { id ->
                    HnService.service.getStory(id).subscribeOn(Schedulers.io()).onErrorResumeNext(Observable.empty<Story>())
                }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        currentStoriesObservable = getCurrentStories()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    protected abstract fun getCurrentStories(): Observable<Int>
}
