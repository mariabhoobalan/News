package com.example.android.news;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

public class newsLoader extends AsyncTaskLoader<List<news>> {
    private String mUrl;

    public newsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<news> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of News.
        List<news> News = QueryUtil.fetchnewsData(mUrl);
        return News;
    }
}
