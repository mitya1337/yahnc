package mitya.yahnc.ui

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.comment_layout.view.*
import mitya.yahnc.R
import mitya.yahnc.domain.Comment
import mitya.yahnc.utils.FormatUtils
import java.util.*

/**
 * Created by Mitya on 17.07.2016.
 */
class CommentsAdapter : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    companion object {
        private const val MARGIN_PER_CHILD = 10
    }

    val commentList = ArrayList<Comment>()

    fun addComment(comment: Comment) {
        if (comment.text != null) {
            commentList.add(comment)
            this.notifyItemInserted(itemCount - 1)
        }
    }

    fun addComments(comments: List<Comment>) {
        val startPosition = itemCount
        commentList.addAll(comments)
        this.notifyItemRangeInserted(startPosition, comments.size)
    }

    fun clearData() {
        val size = commentList.size
        commentList.clear()
        this.notifyItemRangeRemoved(0, size)
    }

    fun getCommentList(): List<Comment> {
        return commentList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.comment_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(commentList[position])
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(comment: Comment) {
            with(itemView) {
                setMargin(commentsCardView, commentsCardView.context, comment.nestingLevel)
                commentText.text = Html.fromHtml(comment.text)
                commentBy.text = comment.by
                commentTime.text = FormatUtils.formatDate(comment.time, commentTime.context)
            }
        }

        private fun setMargin(view: CardView, context: Context, childLevel: Int) {
            val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(FormatUtils.convertFromDpToPx(MARGIN_PER_CHILD * childLevel, context),
                    0,
                    0,
                    FormatUtils.convertFromDpToPx(3, context))
            view.layoutParams = layoutParams
        }
    }

}
