package mitya.yahnc.network;

import mitya.yahnc.domain.Story;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Mitya on 29.06.2016.
 */
public class StoryService extends RestService<StoryService.Api> {
    protected static StoryService instance;

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

    public Observable<Integer[]> getNewStories() {
        return service.getStories(NEW_STORIES);
    }

    public Observable<Integer[]> getTopStories() {
        return service.getStories(TOP_STORIES);
    }

    public Observable<Integer[]> getAskStories() {
        return service.getStories(ASK_STORIES);
    }

    public Observable<Integer[]> getShowStories() {
        return service.getStories(SHOW_STORIES);
    }


    public interface Api {
        @GET("v0/item/{id}.json")
        Observable<Story> getStory(@Path("id") Integer id);

        @GET("v0/{type}.json")
        Observable<Integer[]> getStories(@Path("type") String type);
    }
}
