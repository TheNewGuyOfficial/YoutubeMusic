package com.thenewguy.official.youtubemusic;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wefree.fynn.youtubemusic.R;

import java.util.ArrayList;
import java.util.Arrays;
import com.thenewguy.official.youtubemusic.AppConstants.*;


public class PlaylistActivity extends AppCompatActivity {

    private Context mContext;
    private ImageView image;
    private TextView title;
    private TextView channel;
    private TextView totalVideos;
    private String playlistUri;
    private ItemAdapter adapter;
    private ArrayList<SearchItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        Intent caller = getIntent();
        playlistUri = caller.getStringExtra(AppConstants.PLAYLIST_ACTIVITY_URL_KEY);
        String playlistTitle = caller.getStringExtra(YoutubeJson.TITLE);
        String playlistImage = caller.getStringExtra(YoutubeJson.THUMBNAILS_URL);

        image = (ImageView) findViewById(R.id.imageview_playlist_image);
        title = (TextView) findViewById(R.id.textview_playlist_title);
        channel = (TextView) findViewById(R.id.textview_playlist_channel);
        totalVideos = (TextView) findViewById(R.id.textview_playlist_num_items);

        Picasso.with(this).load(playlistImage).fit().into(image);
        title.setText(playlistTitle);

        HttpHelper helper = new HttpHelper(new PlaylistReceiver());
        helper.execute(playlistUri);
        mContext = this;

        ListView listView = (ListView) findViewById(R.id.list_view_playlist);
        adapter = new ItemAdapter(mContext, new ArrayList<SearchItem>());
        listView.setAdapter(adapter);
        items = new ArrayList<>();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DownloadManagerHelper downloader = new DownloadManagerHelper(mContext);
                downloader.execute(new String[]{items.get(position).getId()});
                String downloading = "Starting Download: " + items.get(position).getTitle();
                Toast.makeText(mContext, downloading, Toast.LENGTH_LONG).show();
            }
        });

    }

    public class PlaylistReceiver implements HttpHelper.OnCompleted {
        private String LOG_TAG = PlaylistReceiver.class.getSimpleName();

        @Override
        public void onTaskCompleted(String jsonString) {
            Log.v(LOG_TAG, jsonString);

            String numItems = ExtractFromJson.getNumResults(jsonString);
            totalVideos.setText("Playlist Items: " + numItems);

            ArrayList<SearchItem> playlistItems = ExtractFromJson.parseResponse(jsonString, SearchType.PLAYLIST_ITEM);
            channel.setText("Channel: " + playlistItems.get(0).getChannel());
            items.addAll(playlistItems);
            adapter.addAll(playlistItems);
            String nextPageToken = ExtractFromJson.nextPageToken(jsonString);
            if (!nextPageToken.equals("")) {
                String newUri = Uri.parse(playlistUri).buildUpon()
                        .appendQueryParameter(YoutubeSearch.PAGE_TOKEN, nextPageToken)
                        .build().toString();

                HttpHelper helper = new HttpHelper(new PlaylistReceiver());
                helper.execute(newUri);
            }
        }
    }
}
