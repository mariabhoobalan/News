package com.example.android.news;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<news>> {

    public static final String LOG_TAG = MainActivity.class.getName();

    /**
     * Constant value for the news loader ID. We can choose any integer.
     */
    private static final int NEWS_LOADER_ID = 1;
    private static final String SEARCH_STRING1 =
            "https://content.guardianapis.com/search?api-key=test&q=";
    private String search_String;
    private String search_string3;
    private static final String NEWS_LIST = "list_of_NewsFeed";
    String searchQuery;
    private ImageView searchButton;
    private TextView mEmptyStateTextView;
    /**
     * Adapter for the list of News feed
     */
    private newsAdaptor mAdapter;
    private EditText mEditText;
    private View loadingIndicator;
    private ListView newsListView;
    private ArrayList<news> mNewsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find a reference to the {@link ListView} in the layout
        newsListView = (ListView) findViewById(R.id.list);
        mNewsArrayList = new ArrayList<>();
        // Create a new adapter that takes an empty list of News as input
        mAdapter = new newsAdaptor(this, mNewsArrayList);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);
        mEmptyStateTextView.setText(R.string.search_string);
        loadingIndicator = findViewById(R.id.loading_spinner);
        mEditText = (EditText) findViewById(R.id.searchText);
        loadingIndicator.setVisibility(View.GONE);
        searchButton = (ImageView) findViewById(R.id.search_button);

        getLoaderManager();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchQuery = mEditText.getText().toString().replaceAll(" ", "+");
                search_string3 = SEARCH_STRING1 + searchQuery;
                mAdapter.clear();
                mAdapter.notifyDataSetChanged();
                searchNews();

            }
        });

        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news that was clicked on
                news currentNews = mAdapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri NewsUri = Uri.parse(currentNews.getmURL());
                // Create a new intent to view the News Feeds URL link
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, NewsUri);
                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public Loader<List<news>> onCreateLoader(int i, Bundle bundle) {
        loadingIndicator.setVisibility(View.VISIBLE);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
        search_String = search_string3;
        Uri baseUri = Uri.parse(search_String);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("order-by", orderBy);
        return new newsLoader(this, uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(Loader<List<news>> loader, List<news> y_news) {
        // Clear the adapter of previous News data
        loadingIndicator.setVisibility(View.GONE);
        // If there is a valid list of {@link News}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (y_news != null && !y_news.isEmpty()) {
            mAdapter.addAll(y_news);
            y_news.clear();
        } else {
            // Set empty state text to display "No News found."
            mEmptyStateTextView.setText(R.string.no_news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<news>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    private void searchNews() {
        if (searchQuery != "") {
            //Check internet connectivity
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                // Get a reference to the LoaderManager, in order to interact with loaders.
                LoaderManager loaderManager = getLoaderManager();
                loaderManager.initLoader(NEWS_LOADER_ID, null, this);
                getLoaderManager().restartLoader(0, null, this);
                mEmptyStateTextView.setText(R.string.retreive_news);
                loadingIndicator.setVisibility(View.VISIBLE);
            } else {
                loadingIndicator.setVisibility(View.GONE);
                // Update empty state with no connection error message
                mEmptyStateTextView.setText(R.string.no_internet_connection);
            }
        } else {
            mAdapter.clear();
            mEmptyStateTextView.setText(R.string.search_string);
            loadingIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putParcelableArrayList(NEWS_LIST, mNewsArrayList);
        Log.d(LOG_TAG, "onSaveInstanceState");
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            Log.d(LOG_TAG, "onViewStateRestored");
            mNewsArrayList = savedInstanceState.getParcelableArrayList(NEWS_LIST);
            mAdapter.clear();
            mAdapter.addAll(mNewsArrayList);
            newsListView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
