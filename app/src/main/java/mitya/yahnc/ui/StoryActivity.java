package mitya.yahnc.ui;

import android.content.Context;
import android.content.Intent;
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
import mitya.yahnc.db.CommentsRepository;
import mitya.yahnc.db.DbHelper;
import mitya.yahnc.db.StoriesRepository;
import mitya.yahnc.network.CommentService;
import mitya.yahnc.utils.FormatUtils;
import mitya.yahnc.R;
import mitya.yahnc.domain.Comment;
import mitya.yahnc.domain.Story;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class StoryActivity extends AppCompatActivity {
    public static final String EXTRA_STORY = "Story";

    private Story currentStory;
    private final CommentService.Api commentService = CommentService.getInstance().getService();

    @BindView(R.id.storyToolbar)
    Toolbar storyToolbar;
    @BindView(R.id.storyRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.storySwiperefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.storyBy)
    TextView storyByView;
    @BindView(R.id.storyTitle)
    TextView storyTitleView;
    @BindView(R.id.storyTime)
    TextView storyTimeView;
    @BindView(R.id.storyScore)
    TextView storyScoreView;
    @BindView(R.id.storyCommentsCount)
    TextView storyCommentsCount;

    private LinearLayoutManager layoutManager;
    private final CommentsAdapter adapter = new CommentsAdapter();
    private StoriesRepository storiesRepository;
    private CommentsRepository commentsRepository;

    @Nullable
    private Subscription commentQuerySubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        ButterKnife.bind(this);
        setupDatabase();
        currentStory = getIntent().getParcelableExtra(EXTRA_STORY);
        if (currentStory != null) {
            setupStoryInfo();
            setupToolbar();
            setupSwipeRefreshLayout();
            setupCommentList();
            if (storiesRepository.readStory(currentStory.id) != null) {
                adapter.addComments(commentsRepository.readAllComments(currentStory.id));
            } else {
                getCommentList(currentStory.kids);
            }
        } else {
            Toast.makeText(this, "Story is null", Toast.LENGTH_SHORT).show();
        }
    }

    public static void startWith(Context context, Story story) {
        Intent intent = new Intent(context, StoryActivity.class);
        intent.putExtra(EXTRA_STORY, story);
        context.startActivity(intent);
    }

    private void setupToolbar() {
        setSupportActionBar(storyToolbar);
        setTitle(R.string.story_toolbar_title);
    }

    private void setupStoryInfo() {
        String url = FormatUtils.formatUrl(currentStory.url);
        if (url == null) {
            storyTitleView.setText(currentStory.title);
        } else {
            storyTitleView.setText(String.format("%s (%s)", currentStory.title, url));
        }
        storyByView.setText(currentStory.by);
        storyScoreView.setText(String.format("%d", currentStory.score));
        if (currentStory.kids == null) {
            storyCommentsCount.setText(String.format("%d", 0));
        } else {
            storyCommentsCount.setText(String.format("%d", currentStory.kids.length));
        }
        storyTimeView.setText(FormatUtils.formatDate(currentStory.time, storyTimeView.getContext()));
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
                    .subscribeOn(Schedulers.io())
                    .flatMap(comment -> getNestedComments(comment, 1))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(adapter::addComment, error -> {
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

    private void setupDatabase() {
        DbHelper dbHelper = new DbHelper(this);
        storiesRepository = new StoriesRepository(dbHelper);
        commentsRepository = new CommentsRepository(dbHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.story_toolbar, menu);
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
            case R.id.action_save_story:
                storiesRepository.createStory(currentStory);
                for (Comment comment : adapter.getCommentList()) {
                    commentsRepository.createComment(comment, currentStory.id);
                }
                Toast.makeText(this, "Story saved", Toast.LENGTH_SHORT).show();
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
