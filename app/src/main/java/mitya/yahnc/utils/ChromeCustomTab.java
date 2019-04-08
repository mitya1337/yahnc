package mitya.yahnc.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;

import mitya.yahnc.ConstantsKt;
import mitya.yahnc.R;
import mitya.yahnc.domain.Story;
import mitya.yahnc.ui.StoryActivity;


/**
 * Created by Mitya on 19.08.2016.
 */
public class ChromeCustomTab {

    public static void openChromeTab(Context context, Story story) {
        Intent intent = new Intent(context, StoryActivity.class);
        intent.putExtra(ConstantsKt.EXTRA_STORY, story);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext()
                , 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        CustomTabsIntent customIntent = new CustomTabsIntent.Builder()
                .setCloseButtonIcon(BitmapFactory.decodeResource(context.getResources()
                        , R.drawable.ic_back_arrow))
                .setStartAnimations(context, R.anim.slide_in_right, R.anim.slide_out_left)
                .setExitAnimations(context, R.anim.slide_in_left, R.anim.slide_out_right)
                .setActionButton(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_comment_outline),
                        context.getString(R.string.action_show_comments), pendingIntent)
                .build();
        customIntent.launchUrl(context, Uri.parse(story.url));
    }
}
