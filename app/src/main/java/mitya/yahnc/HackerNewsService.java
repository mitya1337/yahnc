package mitya.yahnc;

import java.net.URL;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Mitya on 27.06.2016.
 */
public interface HackerNewsService {
    @GET("v0/{items}.json")
    Observable<int[]> getItems(@Path("items") String items);
}
