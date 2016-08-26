package mitya.yahnc.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import mitya.yahnc.R;
import mitya.yahnc.domain.Story;
import mitya.yahnc.network.StoryService;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Mitya on 23.08.2016.
 */
public abstract class StoryFragment extends android.support.v4.app.Fragment {
    protected static final int STORIES_PER_PAGE = 20;
    protected final StoryService.Api storyService = StoryService.getInstance().getService();

    @BindView(R.id.mainRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    protected StoriesAdapter adapter;
    private LinearLayoutManager layoutManager;
    protected EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    @Nullable
    private Subscription storiesQuerySubscription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        adapter = new StoriesAdapter(getActivity());
        setupSwipeRefreshLayout();
        setupStoriesList();
        addNewPage(endlessRecyclerOnScrollListener.getCurrentPage());

        return view;
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapter.clearData();
            endlessRecyclerOnScrollListener.setCurrentPage(1);
            addNewPage(endlessRecyclerOnScrollListener.getCurrentPage());
        });
    }

    private void setupStoriesList() {
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                if (adapter.getItemCount() >= STORIES_PER_PAGE) {
                    addNewPage(currentPage);
                }
            }
        });
    }

    public void actionRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        adapter.clearData();
        endlessRecyclerOnScrollListener.setCurrentPage(1);
        endlessRecyclerOnScrollListener.setLoading(false);
        addNewPage(endlessRecyclerOnScrollListener.getCurrentPage());
    }

    private void addNewPage(int currentPage) {
        storiesQuerySubscription = getStories(currentPage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(adapter::addStory, error -> {
                    error.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                    endlessRecyclerOnScrollListener.setLoading(false);
                }, () -> {
                    swipeRefreshLayout.setRefreshing(false);
                    endlessRecyclerOnScrollListener.setLoading(false);
                });
    }

    protected abstract Observable<Story> getStories(int currentPage);

    @Override
    public void onStop() {
        super.onStop();
        if (storiesQuerySubscription != null) {
            if (!storiesQuerySubscription.isUnsubscribed()) {
                storiesQuerySubscription.unsubscribe();
            }
        }
    }
}
