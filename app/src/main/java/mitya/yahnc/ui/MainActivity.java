package mitya.yahnc.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {
    private final StoryService.Api storyService = StoryService.getInstance().getService();
    @NonNull
    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    @BindView(R.id.mainToolbar)
    Toolbar mainToolbar;
    @BindView(R.id.navigationView)
    NavigationView navigationView;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    private FragmentManager fragmentManager;

    private StoryFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Uri data = getIntent().getData();
        if (data != null && data.getQueryParameter("id") != null) {
            Integer id = Integer.parseInt(data.getQueryParameter("id"));
            compositeSubscription.add(storyService.getStory(id).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(story -> StoryActivity.startWith(this, story), Throwable::printStackTrace));
        } else {
            setupToolbar();
            setupNavigationView();
            setupDrawerLayout();
            fragmentManager = getSupportFragmentManager();
            fragment = new NewStoriesFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(mainToolbar);
        setTitle(R.string.toolbar_title);
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.isChecked()) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
            }
            drawerLayout.closeDrawers();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.newStories:
                    fragmentTransaction.replace(R.id.fragmentContainer, new NewStoriesFragment())
                            .commit();
                    return true;
                case R.id.topStories:
                    fragmentTransaction.replace(R.id.fragmentContainer, new TopStoriesFragment())
                            .commit();
                    return true;
                case R.id.askHnStories:
                    fragmentTransaction.replace(R.id.fragmentContainer, new AskStoriesFragment())
                            .commit();
                    return true;
                case R.id.showHnStories:
                    fragmentTransaction.replace(R.id.fragmentContainer, new ShowStoriesFragment())
                            .commit();
                    return true;
                case R.id.savedStories:
                    fragmentTransaction.replace(R.id.fragmentContainer, new SavedStoriesFragment())
                            .commit();
                    return true;
                default:
                    return true;
            }
        });

    }

    private void setupDrawerLayout() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout
                , mainToolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
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
        compositeSubscription.clear();
    }
}
