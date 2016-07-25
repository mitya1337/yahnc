package mitya.yahnc.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import mitya.yahnc.R;
import mitya.yahnc.domain.Story;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
    }

    public static void startWith(Context context, Story story) {
        Intent intent = new Intent(context, UserActivity.class);
        context.startActivity(intent);
    }
}
