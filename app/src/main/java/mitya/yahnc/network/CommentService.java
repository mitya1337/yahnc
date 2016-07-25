package mitya.yahnc.network;

import mitya.yahnc.domain.Comment;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Mitya on 16.07.2016.
 */
public class CommentService extends RestService<CommentService.Api> {
    private static CommentService instance;

    protected CommentService(Class serviceType) {
        super(serviceType);
    }

    public static synchronized CommentService getInstance() {
        if (instance == null) {
            instance = new CommentService(Api.class);
        }
        return instance;
    }

    public interface Api {
        @GET("v0/item/{id}.json")
        Observable<Comment> getComment(@Path("id") Integer id);
    }
}
