package mitya.yahnc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import mitya.yahnc.R;
import mitya.yahnc.db.CommentsRepository;
import mitya.yahnc.db.DbHelper;
import mitya.yahnc.db.StoriesRepository;
import mitya.yahnc.domain.Comment;
import mitya.yahnc.domain.Story;
import mitya.yahnc.network.CommentService;
import mitya.yahnc.utils.ChromeCustomTab;
import mitya.yahnc.utils.FormatUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class StoryActivity extends AppCompatActivity {
    public static final String EXTRA_STORY = "Story";

    private Story currentStory;
    private final CommentService.Api commentService = CommentService.getInstance().getService();
    @NonNull
    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

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
    @BindView(R.id.storyText)
    TextView storyTextView;

    private LinearLayoutManager layoutManager;
    private final CommentsAdapter adapter = new CommentsAdapter();
    private StoriesRepository storiesRepository;
    private CommentsRepository commentsRepository;

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
            compositeSubscription.add(storiesRepository.findStory("story_id=?", new String[]{Integer.toString(currentStory.id)})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(story -> commentsRepository.find("story_id=?", new String[]{Integer.toString(story.id)}))
                    .subscribe(adapter::addComment, error -> {
                        getCommentList(currentStory.kids);
                    }));
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
            storyTextView.setText(Html.fromHtml(currentStory.text));
            storyTextView.setVisibility(View.VISIBLE);
        } else {
            storyTitleView.setText(String.format("%s (%s)", currentStory.title, url));
        }
        storyByView.setText(currentStory.by);
        storyScoreView.setText(String.format("%d", currentStory.score));
        storyCommentsCount.setText(String.format("%d", currentStory.descendantsCount));
        storyTimeView.setText(FormatUtils.formatDate(currentStory.time, storyTimeView.getContext()));
    }

    private void setupCommentList() {
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private Observable<Comment> getNestedComments(Comment comment) {
        Stack<Comment> childComments = new Stack<>();
        return Observable.create(subscriber -> {
            childComments.push(comment);
            if (comment.kids != null) {
                while (!childComments.empty()) {
                    Comment currentComment = childComments.pop();
                    subscriber.onNext(currentComment);
                    if (currentComment.kids != null) {
                        List<Integer> listOfKids = new ArrayList<>();
                        listOfKids.addAll(Arrays.asList(currentComment.kids));
                        Collections.reverse(listOfKids);
                        Observable.from(listOfKids)
                                .flatMap(commentService::getComment)
                                .subscribe(childComment -> {
                                    childComment.nestingLevel = currentComment.nestingLevel + 1;
                                    childComments.push(childComment);
                                }, subscriber::onError);
                    }
                }
            } else {
                subscriber.onNext(comment);
            }
            subscriber.onCompleted();
        });
    }

    private void getCommentList(Integer[] commentIds) {
        if (commentIds != null) {
            compositeSubscription.add(Observable.from(commentIds)
                    .flatMap(commentService::getComment)
                    .subscribeOn(Schedulers.io())
                    .flatMap(comment -> {
                        comment.storyId = currentStory.id;
                        return getNestedComments(comment);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(adapter::addComment, error -> {
                        error.printStackTrace();
                        swipeRefreshLayout.setRefreshing(false);
                    }, () -> swipeRefreshLayout.setRefreshing(false)));
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

    private void actionSaveStory() {
        compositeSubscription.add(storiesRepository.saveItem(currentStory)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                }, Throwable::printStackTrace));
        compositeSubscription.add(Observable.from(adapter.getCommentList())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(comment -> commentsRepository.saveItem(comment))
                .subscribe(aLong -> {
                }, Throwable::printStackTrace, () -> Toast.makeText(this, "Story saved", Toast.LENGTH_SHORT).show()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.story_toolbar, menu);
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
                actionSaveStory();
                return true;
            case R.id.action_show_story:
                ChromeCustomTab.openChromeTab(this, currentStory);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        compositeSubscription.clear();
    }
}
