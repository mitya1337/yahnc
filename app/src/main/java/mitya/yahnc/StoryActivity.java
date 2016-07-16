package mitya.yahnc;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class StoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
    }

    public static void startFrom(Context context) {
        Intent intent = new Intent(context, StoryActivity.class);
        context.startActivity(intent);
    }
}
