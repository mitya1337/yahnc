package mitya.yahnc;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Mitya on 29.06.2016.
 */
public class StoryIdsService extends RestService<StoryIdsService.Api> {
    private static StoryIdsService instance;

    protected StoryIdsService(Class serviceType) {
        super(serviceType);
    }

    public static synchronized StoryIdsService getInstance() {
        if (instance == null) {
            instance = new StoryIdsService(Api.class);
        }
        return instance;
    }

    public interface Api {
        @GET("v0/{items}.json")
        Observable<int[]> getItems(@Path("items") String items);
    }
}
