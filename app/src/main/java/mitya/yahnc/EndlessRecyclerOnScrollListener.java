package mitya.yahnc;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by Mitya on 07.07.2016.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    private boolean loading = false; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 0; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int currentPage = 1;

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        if (!loading
                && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)
                && totalItemCount != 0
                && totalItemCount != visibleItemCount) {
            loading = true;
            currentPage++;
            onLoadMore(currentPage);
        }
    }

    public abstract void onLoadMore(int currentPage);

}
