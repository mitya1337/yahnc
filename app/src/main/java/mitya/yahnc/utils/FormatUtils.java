package mitya.yahnc.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.TypedValue;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mitya.yahnc.R;

/**
 * Created by Mitya on 16.07.2016.
 */
public class FormatUtils {
    public static String formatDate(long time, Context context) {
        Date dateTime = new Date();
        dateTime.setTime(time * 1000);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        DateFormat timeFormatter = new SimpleDateFormat("HH:mm");

        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            return context.getString(R.string.today) + " " + timeFormatter.format(dateTime);
        } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
            return context.getString(R.string.yesterday) + " " + timeFormatter.format(dateTime);
        } else {
            return dateTime.toString();
        }
    }

    @Nullable
    public static String formatUrl(String fullUrl) {
        URL url = null;
        try {
            url = new URL(fullUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url != null) {
            return url.getHost();
        } else {
            return null;
        }
    }

    public static int convertFromDpToPx(int dp, Context context) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
    }
}
