package mitya.yahnc.ui

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import mitya.yahnc.R
import mitya.yahnc.network.HnService


class MainActivity : AppCompatActivity() {
    private lateinit var fragment: StoryFragment
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = intent.data
        if (data?.getQueryParameter("id") != null) {
            val id = Integer.parseInt(data.getQueryParameter("id"))
            compositeDisposable.add(HnService.service.getStory(id).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ story -> StoryActivity.startWith(this, story) }, { it.printStackTrace() }))
        } else {
            setupToolbar()
            setupNavigationView()
            replaceCurrentFragment(NewStoriesFragment())
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(mainToolbar)
        setTitle(R.string.toolbar_title)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }
    }

    private fun setupNavigationView() {
        navigationView.setNavigationItemSelectedListener { item ->
            item.isChecked = true
            drawerLayout.closeDrawers()
            when (item.itemId) {
                R.id.newStories -> {
                    replaceCurrentFragment(NewStoriesFragment())
                    true
                }
                R.id.topStories -> {
                    replaceCurrentFragment(TopStoriesFragment())
                    true
                }
                R.id.askHnStories -> {
                    replaceCurrentFragment(AskStoriesFragment())
                    true
                }
                R.id.showHnStories -> {
                    replaceCurrentFragment(ShowStoriesFragment())
                    true
                }
                R.id.savedStories -> {
                    replaceCurrentFragment(SavedStoriesFragment())
                    true
                }
                else -> true
            }
        }
    }

    private fun replaceCurrentFragment(newFragment: StoryFragment) {
        fragment = newFragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                fragment.actionRefresh()
                true
            }
            R.id.action_clear_saved_stories -> {
                // TODO : clear story list
                Toast.makeText(this, "Database cleared", Toast.LENGTH_SHORT).show()
                super.onOptionsItemSelected(item)
            }
            R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
