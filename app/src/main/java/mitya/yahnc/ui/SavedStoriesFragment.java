package mitya.yahnc.ui;

import mitya.yahnc.db.DbHelper;
import mitya.yahnc.db.StoriesRepository;
import mitya.yahnc.domain.Story;
import rx.Observable;

/**
 * Created by Mitya on 25.08.2016.
 */
public class SavedStoriesFragment extends StoryFragment {

    @Override
    public Observable<Story> getStories(int currentPage) {
        adapter.clearData();
        DbHelper dbHelper = new DbHelper(getActivity());
        StoriesRepository storiesRepository = new StoriesRepository(dbHelper);
        endlessRecyclerOnScrollListener.setLoading(true);
        return storiesRepository.find(null, null);
    }
}
