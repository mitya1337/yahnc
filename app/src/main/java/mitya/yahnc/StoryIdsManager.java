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
public class StoryIdsManager {
    public interface StoryIdsService {
        @GET("v0/{items}.json")
        Observable<int[]> getItems(@Path("items") String items);
    }

    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();

    private static final StoryIdsService storyIdsService = retrofit.create(StoryIdsService.class);

    public static StoryIdsService getService() {
            return storyIdsService;
    }
}
