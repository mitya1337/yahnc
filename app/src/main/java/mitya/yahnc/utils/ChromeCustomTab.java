package mitya.yahnc.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

import mitya.yahnc.R;
import mitya.yahnc.domain.Story;
import mitya.yahnc.ui.StoryActivity;


/**
 * Created by Mitya on 19.08.2016.
 */
public class ChromeCustomTab {
    private static final String EXTRA_STORY = "Story";

    public static void openChromeTab(Activity activity, Story story) {
        Intent intent = new Intent(activity, StoryActivity.class);
        intent.putExtra(EXTRA_STORY, story);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext()
                , 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        CustomTabsIntent customIntent = new CustomTabsIntent.Builder()
                .setCloseButtonIcon(BitmapFactory.decodeResource(activity.getResources()
                        , R.drawable.ic_back_arrow))
                .setStartAnimations(activity, R.anim.slide_in_right, R.anim.slide_out_left)
                .setExitAnimations(activity, R.anim.slide_in_left, R.anim.slide_out_right)
                .setActionButton(BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_comment_outline),
                        activity.getString(R.string.action_show_comments), pendingIntent)
                .build();
        customIntent.launchUrl(activity, Uri.parse(story.url));
    }
}
