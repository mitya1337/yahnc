package mitya.yahnc;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Mitya on 29.06.2016.
 */
public class StoryService extends RestService<StoryService.Api> {
    private static StoryService instance;

    protected StoryService(Class serviceType) {
        super(serviceType);
    }

    public static synchronized StoryService getInstance() {
        if (instance == null) {
            instance = new StoryService(Api.class);
        }
        return instance;
    }

    public interface Api {
        @GET("v0/item/{id}.json")
        Observable<Story> getStory(@Path("id") Integer id);
    }
}