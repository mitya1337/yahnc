package mitya.yahnc.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import mitya.yahnc.R;
import mitya.yahnc.network.StoryService;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private final StoryService.Api storyService = StoryService.getInstance().getService();

    @BindView(R.id.mainToolbar)
    Toolbar mainToolbar;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    private FragmentManager fragmentManager;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private StoryFragment fragment;
    @Nullable
    private Subscription storyQuerySubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Uri data = getIntent().getData();
        if (data != null && data.getQueryParameter("id") != null) {
            Integer id = Integer.parseInt(data.getQueryParameter("id"));
            storyQuerySubscription = storyService.getStory(id).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(story -> {
                        this.finish();
                        StoryActivity.startWith(this, story);
                    }, Throwable::printStackTrace);
        } else {
            setupToolbar();
            setupNavigationView();
            setupDrawerLayout();
            fragmentManager = getSupportFragmentManager();
            replaceCurrentFragment(new NewStoriesFragment());
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (actionBarDrawerToggle != null) {
            actionBarDrawerToggle.syncState();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(mainToolbar);
        setTitle(R.string.toolbar_title);
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawers();
            switch (item.getItemId()) {
                case R.id.newStories:
                    replaceCurrentFragment(new NewStoriesFragment());
                    return true;
                case R.id.topStories:
                    replaceCurrentFragment(new TopStoriesFragment());
                    return true;
                case R.id.askHnStories:
                    replaceCurrentFragment(new AskStoriesFragment());
                    return true;
                case R.id.showHnStories:
                    replaceCurrentFragment(new ShowStoriesFragment());
                    return true;
                case R.id.savedStories:
                    replaceCurrentFragment(new SavedStoriesFragment());
                    return true;
                default:
                    return true;
            }
        });
    }

    private void replaceCurrentFragment(StoryFragment newFragment) {
        fragment = newFragment;
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void setupDrawerLayout() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout
                , mainToolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
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
                fragment.actionRefresh();
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
        if (storyQuerySubscription != null) {
            if (!storyQuerySubscription.isUnsubscribed()) {
                storyQuerySubscription.unsubscribe();
            }
        }
    }
}
