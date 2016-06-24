package mitya.yahnc;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Mitya on 23.06.2016.
 */
public class JsonStoryParser {
    public Story parse(InputStream inputStream) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(inputStream, Story.class);
    }
}
