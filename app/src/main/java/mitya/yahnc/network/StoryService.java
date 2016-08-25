package mitya.yahnc.network;

import mitya.yahnc.domain.Story;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Mitya on 29.06.2016.
 */
public class StoryService extends RestService<StoryService.Api> {
    private static StoryService instance;

    public static final String NEW_STORIES = "newstories";
    public static final String TOP_STORIES = "topstories";
    public static final String ASK_STORIES = "askstories";
    public static final String SHOW_STORIES = "showstories";

    private StoryService() {
        super(StoryService.Api.class);
    }

    public static synchronized StoryService getInstance() {
        if (instance == null) {
            instance = new StoryService();
        }
        return instance;
    }

    public interface Api {
        @GET("v0/item/{id}.json")
        Observable<Story> getStory(@Path("id") Integer id);

        @GET("v0/" + NEW_STORIES + ".json")
        Observable<Integer[]> getNewStories();

        @GET("v0/" + TOP_STORIES + ".json")
        Observable<Integer[]> getTopStories();

        @GET("v0/" + ASK_STORIES + ".json")
        Observable<Integer[]> getAskStories();

        @GET("v0/" + SHOW_STORIES + ".json")
        Observable<Integer[]> getShowStories();
    }
}
