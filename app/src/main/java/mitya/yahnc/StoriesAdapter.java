package mitya.yahnc;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitya on 23.06.2016.
 */
public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {
    private final List<Story> storyList = new ArrayList<>();

    public StoriesAdapter() {
    }

    public void addStory(Story story) {
        storyList.add(story);
    }

    public void addStories(List<Story> stories) {
        storyList.addAll(stories);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Story story = storyList.get(position);
        holder.title.setText(story.title);
        holder.by.setText(story.by);
        holder.score.setText(String.format("%d", story.score));
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, by, score;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.itemTitle);
            by = (TextView) view.findViewById(R.id.itemBy);
            score = (TextView) view.findViewById(R.id.itemScore);
        }
    }
}
