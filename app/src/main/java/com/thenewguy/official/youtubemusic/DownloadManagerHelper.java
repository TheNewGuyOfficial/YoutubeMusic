package com.thenewguy.official.youtubemusic;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Fynn on 7/31/2016.
 * Helper AsyncTask for Downloading songs and setting their titles
 * Requires list of videoIds
 */
public class DownloadManagerHelper extends AsyncTask<String[], Void, Void> {

    private final static String LOG_TAG = DownloadManagerHelper.class.getSimpleName();
    private static int currentLocation;
    private final Context mContext;

    public DownloadManagerHelper(Context context) {
        mContext = context;
    }

    public static void downloadFileFromLink(String link, String title, Context c) {
        DownloadManager downloadManager = (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link));
        request.setDescription(title);

        File musicFolder = new File("Music");
        if (!musicFolder.exists()) {
            musicFolder.mkdir();
        }

        final File storageLocation = new File(musicFolder, "YouTubeMusic");
        storageLocation.mkdir();

        request.setDestinationInExternalPublicDir(storageLocation.toString(), "partial.mp3");

        request.setVisibleInDownloadsUi(true);
        long downloadId = downloadManager.enqueue(request);
        Log.v(LOG_TAG, "Starting download with id: " + Long.toString(downloadId));
        SharedPreferences.Editor editor = c.getSharedPreferences("downloads", 0).edit();
        editor.putLong(Integer.toString(currentLocation++), downloadId).apply();


    }

    @Override
    protected Void doInBackground(String[]... params) {

        if (params.length == 0) {
            return null;
        }

        String[] queries = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        currentLocation = 0;

        try {
            for (int x = 0; x < queries.length; x++) {
                URL url = new URL("http://www.youtubeinmp3.com/fetch/?format=JSON&video=http://www.youtube.com/watch?v=" + queries[x]);

                Log.v("JSON Url", url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                String jsonStr = buffer.toString();

                //deal with data
                Log.v(LOG_TAG, "Json String: " + jsonStr);

                final String KEY_TITLE = "title";
                final String KEY_LINK = "link";

                JSONObject YouTubeJson = new JSONObject(jsonStr);
                String title = YouTubeJson.getString(KEY_TITLE);
                String link = YouTubeJson.getString(KEY_LINK);

                Log.v(LOG_TAG, "Title: " + title);
                Log.v(LOG_TAG, "Link: " + link);

                downloadFileFromLink(link, title, mContext);
            }


        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }
        catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }
}
