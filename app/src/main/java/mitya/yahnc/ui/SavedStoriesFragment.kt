package mitya.yahnc.ui

import io.reactivex.Observable
import mitya.yahnc.db.DbHelper
import mitya.yahnc.db.StoriesRepository
import mitya.yahnc.domain.Story

/**
 * Created by Mitya on 25.08.2016.
 */
class SavedStoriesFragment : StoryFragment() {
    private val storiesRepository by lazy { StoriesRepository(DbHelper(context)) }

    public override fun getStories(currentPage: Int): Observable<Story> {
        endlessRecyclerOnScrollListener.setLoading(true)
        return storiesRepository.find(null, null)
    }
}
