package mitya.yahnc.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
import mitya.yahnc.R
import mitya.yahnc.domain.Story

/**
 * Created by Mitya on 23.08.2016.
 */
abstract class StoryFragment : Fragment() {

    protected val adapter by lazy { StoriesAdapter() }
    protected val layoutManager by lazy { LinearLayoutManager(context) }
    protected val endlessRecyclerOnScrollListener by lazy {
        object : EndlessRecyclerOnScrollListener(layoutManager) {
            override fun onLoadMore(currentPage: Int) {
                if (adapter.itemCount >= STORIES_PER_PAGE) {
                    addNewPage(currentPage)
                }
            }
        }
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupSwipeRefreshLayout()
        setupStoriesList()
        addNewPage(endlessRecyclerOnScrollListener.currentPage)
    }

    private fun setupSwipeRefreshLayout() {
        swiperefresh.setOnRefreshListener {
            adapter.clearData()
            endlessRecyclerOnScrollListener.currentPage = 1
            addNewPage(endlessRecyclerOnScrollListener.currentPage)
        }
    }

    private fun setupStoriesList() {
        mainRecyclerView.adapter = adapter
        mainRecyclerView.layoutManager = layoutManager
        mainRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener)
    }

    fun actionRefresh() {
        swiperefresh.isRefreshing = true
        adapter.clearData()
        endlessRecyclerOnScrollListener.currentPage = 1
        endlessRecyclerOnScrollListener.setLoading(false)
        addNewPage(endlessRecyclerOnScrollListener.currentPage)
    }

    private fun addNewPage(currentPage: Int) {
        compositeDisposable.add(getStories(currentPage)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    adapter.addStory(it)
                }, { error ->
                    error.printStackTrace()
                    swiperefresh.isRefreshing = false
                    endlessRecyclerOnScrollListener.setLoading(false)
                }, {
                    swiperefresh.isRefreshing = false
                    endlessRecyclerOnScrollListener.setLoading(false)
                }))
    }

    protected abstract fun getStories(currentPage: Int): Observable<Story>

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    companion object {
        const val STORIES_PER_PAGE: Long = 20
    }
}
