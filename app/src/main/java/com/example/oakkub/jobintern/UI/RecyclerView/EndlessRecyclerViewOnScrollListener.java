package com.example.oakkub.jobintern.UI.RecyclerView;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by OaKKuB on 8/12/2015.
 */
public abstract class EndlessRecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {

    private static final int VISIBLE_THRESHOLD = 4;

    private int previousTotal = 0, currentPage = 0, totalItemCount, lastVisibleItem;
    private boolean loading = true;

    private LinearLayoutManager linearLayoutManager;

    public EndlessRecyclerViewOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        totalItemCount = linearLayoutManager.getItemCount();
        lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();

        // new data is loaded to recycler view
        if(totalItemCount > previousTotal) {

            loading = false;
            previousTotal = totalItemCount;
        }

        // load more data
        if(!loading) {

            if(lastVisibleItem >= (totalItemCount - 1) - VISIBLE_THRESHOLD) {

                loading = true;

                onLoadMore(++currentPage);
            }
        }
    }

    public void reset() {
        previousTotal = 0;
        loading = true;
    }

    public abstract void onLoadMore(int currentPage);

}
