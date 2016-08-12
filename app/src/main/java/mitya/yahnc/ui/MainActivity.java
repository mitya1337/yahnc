package mitya.yahnc.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import mitya.yahnc.R;
import mitya.yahnc.db.DbHelper;
import mitya.yahnc.db.StoriesRepository;
import mitya.yahnc.network.StoryIdsService;
import mitya.yahnc.network.StoryService;
import mitya.yahnc.domain.Story;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final int STORIES_PER_PAGE = 20;
    private final StoryService.Api storyService = StoryService.getInstance().getService();

    @BindView(R.id.mainRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.mainToolbar)
    Toolbar mainToolbar;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private StoriesAdapter adapter = new StoriesAdapter();
    private LinearLayoutManager layoutManager;
    private Observable<Integer> newStories;
    private EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    @Nullable
    private Subscription newPageQuerySubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Uri data = getIntent().getData();
        if (data != null && data.getQueryParameter("id") != null) {
            Integer id = Integer.parseInt(data.getQueryParameter("id"));
            storyService.getStory(id).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(story -> StoryActivity.startWith(this, story), Throwable::printStackTrace);
        } else {
            setupToolbar();
            setupSwipeRefreshLayout();
            setupStoriesList();
            getNewStories();
            addNewPage(endlessRecyclerOnScrollListener.getCurrentPage());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (newStories == null) {
            setupToolbar();
            setupSwipeRefreshLayout();
            setupStoriesList();
            getNewStories();
            addNewPage(endlessRecyclerOnScrollListener.getCurrentPage());
        }
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapter.clearData();
            endlessRecyclerOnScrollListener.setCurrentPage(1);
            addNewPage(endlessRecyclerOnScrollListener.getCurrentPage());
        });
    }

    private void setupToolbar() {
        setSupportActionBar(mainToolbar);
        setTitle(R.string.toolbar_title);
    }

    private void setupStoriesList() {
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
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
                    endlessRecyclerOnScrollListener.setLoading(false);
                }, () -> {
                    swipeRefreshLayout.setRefreshing(false);
                    endlessRecyclerOnScrollListener.setLoading(false);
                });
    }

    private void getNewStories() {
        newStories = StoryIdsService.getInstance().getService().getItems("newstories").
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
                endlessRecyclerOnScrollListener.setLoading(false);
                addNewPage(endlessRecyclerOnScrollListener.getCurrentPage());
                return true;
            case R.id.action_show_saved_stories:
                adapter.clearData();
                DbHelper dbHelper = new DbHelper(this);
                StoriesRepository storiesRepository = new StoriesRepository(dbHelper);
                adapter.addStories(storiesRepository.readAllStories());
                endlessRecyclerOnScrollListener.setLoading(true);
                return true;
            case R.id.action_clear_saved_stories:
                // TODO : clear story list
                Toast.makeText(this, "Database cleared", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        endlessRecyclerOnScrollListener.setLoading(false);
        if (newPageQuerySubscription != null) {
            if (!newPageQuerySubscription.isUnsubscribed()) {
                newPageQuerySubscription.unsubscribe();
            }
        }
    }

}