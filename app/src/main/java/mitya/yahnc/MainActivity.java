package mitya.yahnc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final int STORIES_PER_PAGE = 20;
    private final StoryService.Api storyService = StoryService.getInstance().service;

    private RecyclerView recyclerView;
    private StoriesAdapter adapter = new StoriesAdapter();
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Observable<Integer> newStories;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    @Nullable
    private Subscription newPageQuerySubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupSwipeRefreshLayout();
        setupStoriesList();
        getNewStories();
        addNewPage(endlessRecyclerOnScrollListener.getCurrentPage());
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapter.clearData();
            endlessRecyclerOnScrollListener.setCurrentPage(1);
            addNewPage(endlessRecyclerOnScrollListener.getCurrentPage());
        });
    }

    private void setupToolbar() {
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        setTitle(R.string.toolbar_title);
    }

    private void setupStoriesList() {
        recyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                Log.d("PAGES", "FROM ONLOADMORE  : " + Integer.toString(endlessRecyclerOnScrollListener.getCurrentPage()));
                addNewPage(currentPage);
            }
        });
    }

    private void addNewPage(int currentPage) {
        newPageQuerySubscription = newStories.
                skip(STORIES_PER_PAGE * (currentPage - 1)).
                take(STORIES_PER_PAGE).
                flatMap(id -> storyService.getStory(id).subscribeOn(Schedulers.io()).onErrorResumeNext(Observable.<Story>empty())).
                observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).
                subscribe(adapter::addStory, error -> {
                    error.printStackTrace();
                    swipeRefreshLayout.setRefreshing(false);
                }, () -> {
                    swipeRefreshLayout.setRefreshing(false);
                    endlessRecyclerOnScrollListener.setLoading(false);
                });
        Log.d("PAGES", Integer.toString(endlessRecyclerOnScrollListener.getCurrentPage()));
    }

    private void getNewStories() {
        newStories = StoryIdsService.getInstance().service.getItems("newstories").
                flatMap(stories -> Observable.from(stories).subscribeOn(Schedulers.io()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                swipeRefreshLayout.setRefreshing(true);
                adapter.clearData();
                endlessRecyclerOnScrollListener.setCurrentPage(1);
                addNewPage(endlessRecyclerOnScrollListener.getCurrentPage());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (newPageQuerySubscription != null) {
            if (!newPageQuerySubscription.isUnsubscribed()) {
                newPageQuerySubscription.unsubscribe();
            }
        }
    }
}
