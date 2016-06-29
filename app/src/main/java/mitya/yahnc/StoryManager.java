package mitya.yahnc;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Mitya on 29.06.2016.
 */
public class StoryManager  {
    public interface StoryService {
        @GET("v0/item/{id}.json")
        Observable<Story> getStory(@Path("id") int id);
    }
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();

    private static final StoryService storyService = retrofit.create(StoryService.class);

    public static StoryService getService() {
        return storyService;
    }
}
