package mitya.yahnc;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mitya on 17.07.2016.
 */
public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private List<Comment> commentList = new ArrayList<>();

    public CommentsAdapter() {
    }

    public void addComment(Comment comment) {
        commentList.add(comment);
        this.notifyItemInserted(getItemCount() - 1);
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.commentTextView.setText(comment.text);
        holder.commentByView.setText(comment.by);
        holder.commentTimeView.setText(FormatUtils.formatDate(comment.time, holder.commentTimeView.getContext()));
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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
