package mitya.yahnc.ui;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mitya.yahnc.utils.FormatUtils;
import mitya.yahnc.R;
import mitya.yahnc.domain.Comment;

/**
 * Created by Mitya on 17.07.2016.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private static final int MARGIN_PER_CHILD = 10;

    private List<Comment> commentList = new ArrayList<>();

    public CommentsAdapter() {
    }

    public void addComment(Comment comment) {
        if (comment.text != null) {
            commentList.add(comment);
            this.notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addComments(List<Comment> comments) {
        int startPosition = getItemCount();
        commentList.addAll(comments);
        this.notifyItemRangeInserted(startPosition, comments.size());
    }

    public void clearData() {
        int size = commentList.size();
        commentList.clear();
        this.notifyItemRangeRemoved(0, size);
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    private void setMargin(CardView view, Context context, int childLevel) {
        CardView.LayoutParams layoutParams = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(FormatUtils.convertFromDpToPx(MARGIN_PER_CHILD * childLevel, context),
                0,
                0,
                FormatUtils.convertFromDpToPx(3, context));
        view.setLayoutParams(layoutParams);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        if (comment.nestingLevel == 0) {
            setMargin(holder.cardView, holder.cardView.getContext(), comment.nestingLevel);
            holder.commentTextView.setText(Html.fromHtml(comment.text));
            holder.commentByView.setText(comment.by);
            holder.commentTimeView.setText(FormatUtils.formatDate(comment.time, holder.commentTimeView.getContext()));
        } else {
            setMargin(holder.cardView, holder.cardView.getContext(), comment.nestingLevel);
            holder.commentTextView.setText(Html.fromHtml(comment.text));
            holder.commentByView.setText(comment.by);
            holder.commentTimeView.setText(FormatUtils.formatDate(comment.time, holder.commentTimeView.getContext()));
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.commentText)
        TextView commentTextView;
        @BindView(R.id.commentBy)
        TextView commentByView;
        @BindView(R.id.commentTime)
        TextView commentTimeView;
        @BindView(R.id.commentsCardView)
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
