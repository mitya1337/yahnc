package mitya.yahnc;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Mitya on 28.06.2016.
 */
public interface StoryInterface {
    String SERVICE_BASEPOINT = "https://hacker-news.firebaseio.com/";

    @GET("v0/item/{id}.json")
    Observable<Story> getStory(@Path("id") int id);
}
