package mitya.yahnc.network

import io.reactivex.Observable
import mitya.yahnc.domain.Comment
import mitya.yahnc.domain.Story
import retrofit2.http.GET
import retrofit2.http.Path

object HnService : RestService<HnService.Api>(Api::class.java) {

    private const val NEW_STORIES = "newstories"
    private const val TOP_STORIES = "topstories"
    private const val ASK_STORIES = "askstories"
    private const val SHOW_STORIES = "showstories"

    fun getNewStories() = service.getStories(NEW_STORIES)

    fun getTopStories() = service.getStories(TOP_STORIES)

    fun getAskStories() = service.getStories(ASK_STORIES)

    fun getShowStories() = service.getStories(SHOW_STORIES)

    interface Api {
        @GET("v0/item/{id}.json")
        fun getComment(@Path("id") id: Int): Observable<Comment>

        @GET("v0/item/{id}.json")
        fun getStory(@Path("id") id: Int): Observable<Story>

        @GET("v0/{type}.json")
        fun getStories(@Path("type") type: String): Observable<Array<Int>>
    }
}