package mitya.yahnc.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mitya.yahnc.db.DbHelper;
import mitya.yahnc.db.StoriesRepository;
import mitya.yahnc.domain.Story;
import rx.Observable;

/**
 * Created by Mitya on 25.08.2016.
 */
public class SavedStoriesFragment extends StoryFragment {
    protected StoriesRepository storiesRepository;

    @Override
    public Observable<Story> getStories(int currentPage) {
        endlessRecyclerOnScrollListener.setLoading(true);
        return storiesRepository.find(null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DbHelper dbHelper = new DbHelper(getActivity());
        storiesRepository = new StoriesRepository(dbHelper);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
