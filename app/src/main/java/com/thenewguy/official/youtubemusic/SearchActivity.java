package com.thenewguy.official.youtubemusic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.thenewguy.official.youtubemusic.AppConstants.*;
import com.wefree.fynn.youtubemusic.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private Context mContext;
    private String searchType;
    private final String LOG_TAG = SearchActivity.class.getSimpleName();
    private SearchView searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        if (intent.hasExtra(SearchType.KEY))
            searchType = intent.getStringExtra(SearchType.KEY);
        else
        searchType = SearchType.VIDEO;
        mContext = this;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.listener);

        searchBar = (SearchView) findViewById(R.id.search_bar);
        searchBar.setIconifiedByDefault(false);        searchBar.setOnQueryTextListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String basicQuery) {
        if (basicQuery.equals("")) {
            return false;
        }
        String query = null;
        try {
            query = URLEncoder.encode(basicQuery, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String urlLink = null;
        String part = "snippet";
        String maxResults = "50";
        String order = "viewCount";



        Uri uri = Uri.parse(AppConstants.YOUTUBE_SEARCH_BASE_URL).buildUpon()
                .appendQueryParameter(YoutubeSearch.QUERY, query)
                .appendQueryParameter(YoutubeSearch.PART, part)
                .appendQueryParameter(YoutubeSearch.TYPE, searchType)
                .appendQueryParameter(YoutubeSearch.API_KEY, AppConstants.YOUTUBE_API_KEY)
                .appendQueryParameter(YoutubeSearch.MAX_RESULTS, maxResults)
                .appendQueryParameter(YoutubeSearch.ORDER, order)
                .build();

        urlLink = uri.toString();

        try {
            HttpHelper helper = new HttpHelper(new SearchReceiver());
            helper.execute(urlLink);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public class SearchReceiver implements HttpHelper.OnCompleted {
        private String LOG_TAG = SearchReceiver.class.getSimpleName();

        @Override
        public void onTaskCompleted(String jsonString) {
            Log.v(LOG_TAG, jsonString);
            ListView listView = (ListView) findViewById(R.id.list_view_searchItems);

            final ArrayList<SearchItem> results = ExtractFromJson.parseResponse(jsonString, searchType);
            ItemAdapter adapter = new ItemAdapter(mContext, results);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SearchItem item = results.get(position);
                    if (item.getType().equals(SearchType.VIDEO)) {
                        DownloadManagerHelper downloader = new DownloadManagerHelper(mContext);
                        downloader.execute(new String[]{results.get(position).getId()});
                        String downloading = "Starting Download: " + item.getTitle();
                        Toast.makeText(mContext, downloading, Toast.LENGTH_LONG).show();
                    } else if (item.getType().equals(SearchType.PLAYLIST)) {

                        String part = "snippet";
                        String maxResults = "50";

                        Uri playlist = Uri.parse(AppConstants.YOUTUBE_PLAYLIST_ITEM_BASE).buildUpon()
                                .appendQueryParameter(YoutubeSearch.PART, part)
                                .appendQueryParameter(YoutubeSearch.PLAYLIST_ID, item.getId())
                                .appendQueryParameter(YoutubeSearch.API_KEY, AppConstants.YOUTUBE_API_KEY)
                                .appendQueryParameter(YoutubeSearch.MAX_RESULTS, maxResults)
                                .build();
                        Intent playlistActivity = new Intent(mContext, PlaylistActivity.class);
                        playlistActivity.putExtra(AppConstants.PLAYLIST_ACTIVITY_URL_KEY, playlist.toString());
                        playlistActivity.putExtra(YoutubeJson.TITLE, item.getTitle());
                        playlistActivity.putExtra(YoutubeJson.THUMBNAILS_URL, item.getImage());
                        startActivity(playlistActivity);
                    }
                }
            });
        }
    }
}