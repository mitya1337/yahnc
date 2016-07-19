package mitya.yahnc;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Mitya on 17.07.2016.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private static final int MARGIN_PER_CHILD = 10;

    private List<Comment> commentList = new ArrayList<>();
    private Subscription commentQuerySubscription;

    public CommentsAdapter() {
    }

    public void addComment(Comment comment) {
        if (comment.text != null) {
            commentList.add(comment);
            if (comment.nestingLevel == 0) {
                addChildComments(comment, 1);
            }
            this.notifyItemInserted(getItemCount() - 1);
        }
    }

    public void addComments(List<Comment> comments) {
        int startPosition = getItemCount();
        commentList.addAll(comments);
        this.notifyItemRangeInserted(startPosition, comments.size());
    }

    public void addChildComments(Comment comment, int childLevel) {
        if (comment.kids != null) {
            commentQuerySubscription = Observable.from(comment.kids)
                    .subscribeOn(Schedulers.io())
                    .flatMap(id -> CommentService.getInstance().service.getComment(id))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(childComment -> {
                        childComment.nestingLevel = childLevel;
                        addChildComments(childComment, childLevel + 1);
                        addComment(childComment);
                    }, error -> {
                        error.printStackTrace();
                    });
        }
    }

    public void clearData() {
        int size = commentList.size();
        commentList.clear();
        this.notifyItemRangeRemoved(0, size);
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
