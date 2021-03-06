package mitya.yahnc.ui

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import mitya.yahnc.network.HnService

/**
 * Created by Mitya on 25.08.2016.
 */
class TopStoriesFragment : RemoteStoryFragment() {
    override fun getCurrentStories(): Observable<Int> {
        return HnService.getTopStories().flatMap { stories -> Observable.fromArray(*stories).subscribeOn(Schedulers.io()) }
    }
}
