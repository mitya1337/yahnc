package mitya.yahnc.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_story.*
import mitya.yahnc.EXTRA_STORY
import mitya.yahnc.R
import mitya.yahnc.db.CommentsRepository
import mitya.yahnc.db.DbHelper
import mitya.yahnc.db.StoriesRepository
import mitya.yahnc.domain.Comment
import mitya.yahnc.domain.Story
import mitya.yahnc.network.HnService
import mitya.yahnc.utils.ChromeCustomTab
import mitya.yahnc.utils.FormatUtils
import java.util.*

class StoryActivity : AppCompatActivity() {

    private val currentStory: Story by lazy { (intent.getParcelableExtra(EXTRA_STORY) as Story) }
    private val compositeDisposable = CompositeDisposable()

    private val adapter = CommentsAdapter()
    private val storiesRepository by lazy { StoriesRepository(DbHelper(this)) }
    private val commentsRepository by lazy { CommentsRepository(DbHelper(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)
        setupStoryInfo()
        setupToolbar()
        setupSwipeRefreshLayout()
        setupCommentList()
        compositeDisposable.add(storiesRepository.findStory("story_id=?", arrayOf(Integer.toString(currentStory.id)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { story -> commentsRepository.find("story_id=?", arrayOf(Integer.toString(story.id))) }
                .subscribe({ adapter.addComment(it) }, { getCommentList(currentStory.kids) }))
    }

    private fun setupToolbar() {
        setSupportActionBar(storyToolbar)
        setTitle(R.string.story_toolbar_title)
    }

    private fun setupStoryInfo() {
        val url = FormatUtils.formatUrl(currentStory.url)
        if (url == null) {
            storyTitle.text = currentStory.title
            storyText.text = Html.fromHtml(currentStory.text ?: "")
            storyText.visibility = View.VISIBLE
        } else {
            storyTitle.text = String.format("%s (%s)", currentStory.title, url)
        }
        storyBy.text = currentStory.by
        storyScore.text = String.format("%d", currentStory.score)
        storyCommentsCount.text = String.format("%d", currentStory.descendantsCount)
        storyTime.text = FormatUtils.formatDate(currentStory.time, storyTime.context)
    }

    private fun setupCommentList() {
        storyRecyclerView.adapter = adapter
        storyRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
    }

    private fun getNestedComments(comment: Comment): Observable<Comment> {
        val childComments = Stack<Comment>()
        return Observable.create { subscriber ->
            childComments.push(comment)
            if (comment.kids != null) {
                while (!childComments.empty()) {
                    val currentComment = childComments.pop()
                    subscriber.onNext(currentComment)
                    if (currentComment.kids != null) {
                        val listOfKids = ArrayList<Int>()
                        listOfKids.addAll(Arrays.asList(*currentComment.kids))
                        listOfKids.reverse()
                        Observable.fromIterable(listOfKids)
                                .flatMap { HnService.service.getComment(it) }
                                .subscribe({ childComment ->
                                    childComment.nestingLevel = currentComment.nestingLevel + 1
                                    childComments.push(childComment)
                                }, { subscriber.onError(it) })
                    }
                }
            } else {
                subscriber.onNext(comment)
            }
            subscriber.onComplete()
        }
    }

    private fun getCommentList(commentIds: Array<Int>?) {
        if (commentIds != null) {
            compositeDisposable.add(Observable.fromArray(*commentIds)
                    .flatMap { HnService.service.getComment(it) }
                    .subscribeOn(Schedulers.io())
                    .flatMap { comment ->
                        comment.storyId = currentStory.id
                        getNestedComments(comment)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        adapter.addComment(it)
                    }, { error ->
                        error.printStackTrace()
                        storySwiperefresh.isRefreshing = false
                    }, {
                        storySwiperefresh.isRefreshing = false
                    }))
        } else {
            storySwiperefresh.isRefreshing = false
            Toast.makeText(this, "No comments", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSwipeRefreshLayout() {
        storySwiperefresh.setOnRefreshListener {
            refreshComments()
        }
    }

    private fun actionSaveStory() {
        compositeDisposable.add(storiesRepository.saveItem(currentStory)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ }, { it.printStackTrace() }))
        compositeDisposable.add(Observable.fromIterable(adapter.commentList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { comment -> commentsRepository.saveItem(comment) }
                .subscribe({ }, { it.printStackTrace() }, { Toast.makeText(this, "Story saved", Toast.LENGTH_SHORT).show() }))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.story_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                storySwiperefresh.isRefreshing = true
                refreshComments()
                return true
            }
            R.id.action_save_story -> {
                actionSaveStory()
                return true
            }
            R.id.action_show_story -> {
                ChromeCustomTab.openChromeTab(this, currentStory)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun refreshComments() {
        compositeDisposable.clear()
        adapter.clearData()
        getCommentList(currentStory.kids)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    companion object {
        fun startWith(context: Context, story: Story) {
            val intent = Intent(context, StoryActivity::class.java)
            intent.putExtra(EXTRA_STORY, story)
            context.startActivity(intent)
        }
    }
}
