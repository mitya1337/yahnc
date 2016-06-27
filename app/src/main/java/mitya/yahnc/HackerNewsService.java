package mitya.yahnc;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Mitya on 27.06.2016.
 */
public interface HackerNewsService {
    String SERVICE_BASEPOINT = "https://hacker-news.firebaseio.com/";

    @GET("v0/{items}")
    Observable<int[]> getItems(@Path("items") String items);
}
