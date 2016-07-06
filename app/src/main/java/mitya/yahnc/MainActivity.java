package mitya.yahnc;

import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StoriesAdapter adapter = new StoriesAdapter();
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Nullable
    private Subscription storiesQuerySubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupSwipeRefreshLayout();
        setupStoriesList();
        getNewStories();
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this::getNewStories);
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
    }

    private void getNewStories() {
        if (storiesQuerySubscription != null) {
            if (!storiesQuerySubscription.isUnsubscribed()) storiesQuerySubscription.unsubscribe();
        }
        adapter.clearData();
        storiesQuerySubscription = StoryIdsService.getInstance().service.getItems("newstories").
                subscribeOn(Schedulers.io()).
                flatMap(integers -> Observable.from(integers).subscribeOn(Schedulers.io())).
                flatMap(id -> StoryService.getInstance().service.getStory(id).subscribeOn(Schedulers.io()).onErrorResumeNext(Observable.<Story>empty())).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(adapter::addStory, error -> {
                    swipeRefreshLayout.setRefreshing(false);
                    error.printStackTrace();
                }, () -> swipeRefreshLayout.setRefreshing(false));
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
                getNewStories();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (storiesQuerySubscription != null) {
            if (!storiesQuerySubscription.isUnsubscribed()) {
                storiesQuerySubscription.unsubscribe();
            }
        }
    }
}
