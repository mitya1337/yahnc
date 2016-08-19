package mitya.yahnc.ui;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mitya.yahnc.R;
import mitya.yahnc.domain.Story;
import mitya.yahnc.utils.ChromeCustomTab;
import mitya.yahnc.utils.FormatUtils;

/**
 * Created by Mitya on 23.06.2016.
 */
public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {
    private final List<Story> storyList = new ArrayList<>();

    private Activity mainActivity;

    public StoriesAdapter(Activity activity) {
        this.mainActivity = activity;
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
            String url = FormatUtils.formatUrl(story.url);
            if (url == null) {
                holder.titleView.setText(story.title);
            } else {
                holder.titleView.setText(String.format("%s (%s)", story.title, url));
            }
            holder.byView.setText(story.by);
            holder.scoreView.setText(String.format("%d", story.score));
            if (story.kids == null) {
                holder.commentsCount.setText(String.format("%d", 0));
            } else {
                holder.commentsCount.setText(String.format("%d", story.kids.length));
            }
            holder.timeView.setText(FormatUtils.formatDate(story.time, holder.timeView.getContext()));
            holder.cardView.setOnClickListener(view -> ChromeCustomTab.openChromeTab(mainActivity, story));
            holder.commentsCount.setOnClickListener(view -> StoryActivity.startWith(view.getContext(), story));
            holder.commentsImage.setOnClickListener(view -> StoryActivity.startWith(view.getContext(), story));
            holder.byView.setOnClickListener(view -> UserActivity.startWith(view.getContext(), story));
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
        @BindView(R.id.cardView)
        CardView cardView;
        @BindView(R.id.commentsImage)
        ImageView commentsImage;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
