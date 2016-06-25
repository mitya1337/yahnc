package mitya.yahnc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by Mitya on 23.06.2016.
 */
public class JsonStoryParser {
    public Story parse(InputStream inputStream) throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Reader reader = new InputStreamReader(inputStream);
        return gson.fromJson(reader, Story.class);
    }
}
