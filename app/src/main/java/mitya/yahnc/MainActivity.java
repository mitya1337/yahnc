package mitya.yahnc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final List<Story> storyList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StoriesAdapter adapter = new StoriesAdapter();
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        try {
            setupStoriesList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setupToolbar() {
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(R.string.toolbar_title);
    }

    public void setupStoriesList() throws IOException {
        recyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        recyclerView.setHasFixedSize(true);
        JsonStoryParser parser = new JsonStoryParser();
        Story story = parser.parse(getResources().openRawResource(R.raw.json));
        for (int i = 0; i < 20; i++) {
            storyList.add(story);
        }
        adapter.addStories(storyList);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
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
                // TODO: code for refreshing the page
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
