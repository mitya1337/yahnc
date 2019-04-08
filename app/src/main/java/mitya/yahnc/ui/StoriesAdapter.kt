package mitya.yahnc.ui

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_layout.view.*
import mitya.yahnc.App
import mitya.yahnc.R
import mitya.yahnc.domain.Story
import mitya.yahnc.utils.ChromeCustomTab
import mitya.yahnc.utils.FormatUtils

/**
 * Created by Mitya on 23.06.2016.
 */
class StoriesAdapter : RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {
    private val storyList = ArrayList<Story>()

    fun addStory(story: Story) {
        storyList.add(story)
        this.notifyItemInserted(itemCount - 1)
    }

    fun addStories(stories: List<Story>) {
        val startPosition = itemCount
        storyList.addAll(stories)
        this.notifyItemRangeInserted(startPosition, stories.size)
    }

    fun clearData() {
        val size = storyList.size
        storyList.clear()
        this.notifyItemRangeRemoved(0, size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(storyList[position])
    }

    override fun getItemCount(): Int {
        return storyList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(story: Story) {
            with(itemView) {
                val url = FormatUtils.formatUrl(story.url)
                if (url == null) {
                    itemTitle.text = story.title
                    cardView.setOnClickListener { view -> StoryActivity.startWith(view.context, story) }
                } else {
                    cardView.setOnClickListener { ChromeCustomTab.openChromeTab(context, story) }
                    itemTitle.text = String.format("%s (%s)", story.title, url)
                }
                itemBy.apply {
                    text = story.by
                    setOnClickListener { view -> UserActivity.startWith(view.context, story) }
                }
                itemCommentsCount.apply {
                    text = String.format("%d", story.descendantsCount)
                    setOnClickListener { view -> StoryActivity.startWith(view.context, story) }
                }
                itemScore.text = String.format("%d", story.score)
                itemTime.text = FormatUtils.formatDate(story.time, itemTime.context)
                commentsImage.setOnClickListener { view -> StoryActivity.startWith(view.context, story) }
            }
        }
    }
}
