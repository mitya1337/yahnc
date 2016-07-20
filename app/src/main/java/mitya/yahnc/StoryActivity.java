package mitya.yahnc;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StoryActivity extends AppCompatActivity {
    private static Story currentStory;
    private final CommentService.Api commentService = CommentService.getInstance().service;

    @BindView(R.id.storyToolbar)
    Toolbar storyToolbar;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.storyRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.storySwiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private LinearLayoutManager layoutManager;
    private CommentsAdapter adapter = new CommentsAdapter();

    @Nullable
    private Subscription commentQuerySubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        ButterKnife.bind(this);
        setupToolbar(currentStory.title);
        setupSwipeRefreshLayout();
        setupCommentList();
        getCommentList(currentStory.kids);
    }

    public static void startFrom(Context context, Story story) {
        Intent intent = new Intent(context, StoryActivity.class);
        currentStory = story;
        context.startActivity(intent);
    }

    private void setupToolbar(String title) {
        setSupportActionBar(storyToolbar);
        toolbarTitle.setText(title);
    }

    private void setupCommentList() {
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private Observable<Comment> getNestedComments(Comment comment, int nestingLevel) {
        if (comment.kids != null) {
            return Observable.just(comment).concatWith(Observable.from(comment.kids)
                    .flatMap(commentService::getComment)
                    .flatMap(childComment -> {
                        childComment.nestingLevel = nestingLevel;
                        return getNestedComments(childComment, nestingLevel + 1);
                    }));
        } else {
            return Observable.just(comment);
        }
    }

    private void getCommentList(Integer[] commentIds) {
        if (commentIds != null) {
            commentQuerySubscription = Observable.from(commentIds)
                    .flatMap(commentService::getComment)
                    .flatMap(comment -> getNestedComments(comment, 1))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(comment -> adapter.addComment(comment), error -> {
                        error.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
                    }, () -> swipeRefreshLayout.setRefreshing(false));
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "No comments", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapter.clearData();
            getCommentList(currentStory.kids);
        });
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
                getCommentList(currentStory.kids);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (commentQuerySubscription != null) {
            if (!commentQuerySubscription.isUnsubscribed()) {
                commentQuerySubscription.unsubscribe();
            }
        }
    }
}
