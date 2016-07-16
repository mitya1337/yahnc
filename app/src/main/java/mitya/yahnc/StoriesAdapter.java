package mitya.yahnc;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Mitya on 23.06.2016.
 */
public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {
    private final List<Story> storyList = new ArrayList<>();

    public StoriesAdapter() {
    }

    public void addStory(Story story) {
        storyList.add(story);
        this.notifyItemInserted(getItemCount() - 1);
    }

    public void addStories(List<Story> stories) {
        int startPosition = getItemCount();
        storyList.addAll(stories);
        this.notifyItemRangeInserted(startPosition, stories.size());
    }

    public void clearData() {
        int size = storyList.size();
        storyList.clear();
        this.notifyItemRangeRemoved(0, size);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Story story = storyList.get(position);
        if (story != null) {
            URL url = null;
            try {
                url = new URL(story.url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if (url == null) {
                holder.titleView.setText(story.title);
            } else {
                holder.titleView.setText(String.format("%s (%s)", story.title, url.getHost()));
            }
            holder.byView.setText(story.by);
            holder.scoreView.setText(String.format("%d", story.score));
            if (story.kids == null) {
                holder.commentsCount.setText(String.format("%d", 0));
            } else {
                holder.commentsCount.setText(String.format("%d", story.kids.length));
            }
            holder.timeView.setText(FormatUtils.formatDate(story.time, holder.timeView.getContext()));
        }
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemTitle)
        TextView titleView;
        @BindView(R.id.itemBy)
        TextView byView;
        @BindView(R.id.itemScore)
        TextView scoreView;
        @BindView(R.id.itemCommentsCount)
        TextView commentsCount;
        @BindView(R.id.itemTime)
        TextView timeView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.itemBy)
        public void onUserClick(View view) {
            UserActivity.startFrom(view.getContext());
        }

        @OnClick(R.id.cardView)
        public void onItemClick(View view) {
            StoryActivity.startFrom(view.getContext());
        }
    }
}
