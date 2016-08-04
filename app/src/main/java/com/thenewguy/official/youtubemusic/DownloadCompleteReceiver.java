package com.thenewguy.official.youtubemusic;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.ArraySet;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Fynn on 8/4/2016.
 */
public class DownloadCompleteReceiver extends BroadcastReceiver {

    private int downloadInstance = 0;
    private String LOG_TAG = DownloadCompleteReceiver.class.getSimpleName();
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);

        Log.v(LOG_TAG, "Received: " + Long.toString(id));

        boolean match = false;

        SharedPreferences sharedPreferences = context.getSharedPreferences("downloads", 0);

        int current = 0;
        int location;

        long downloadedId;

        while ((downloadedId = sharedPreferences.getLong(Integer.toString(current++), -1)) != -1) {
            Log.v(LOG_TAG, "Values: " + Long.toString(downloadedId));
            if (downloadedId == id) {
                match = true;
                location = current - 1;
                Log.v(LOG_TAG, "Removing: " + sharedPreferences.getLong(Integer.toString(location), -1));
                sharedPreferences.edit().remove(Integer.toString(2402))
                .remove(Integer.toString(location)).apply();
            }
        }

        if (!match)
            return;

        mContext = context;
        handleDownloadedFile(id);
    }

    private void handleDownloadedFile(long id) {
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = downloadManager.query(query);

        if (!cursor.moveToFirst()) return;

        int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        if (DownloadManager.STATUS_SUCCESSFUL == cursor.getInt(statusIndex)) {
            int localUriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
            int nameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE);
            String name = cursor.getString(nameIndex);
            String localUri = cursor.getString(localUriIndex);
            localUri = localUri.substring(7);
            int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_URI);
            String downloadUri = cursor.getString(uriIndex);

            MediaMetadataRetriever metaAccessor = createMediaMetaDataReceiver(localUri);
            if (metaAccessor == null) {
                if (downloadInstance < 3) {
                    deleteFailedDownload(localUri);
                    downloadInstance++;
                    Log.w(LOG_TAG, "Download Failed, Retrying");
                    DownloadManagerHelper.downloadFileFromLink(downloadUri, "Retrying", mContext);
                    return;
                } else {
                    Log.v(LOG_TAG, "3 Attempts to download failed: " + downloadUri);
                    return;
                }
                //restart search using downloadUri
            }

            String title = metaAccessor.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            File rootDir = Environment.getExternalStorageDirectory();
            File musicFolder = new File(rootDir, "Music");
            if (!musicFolder.exists()) {
                musicFolder.mkdir();
            }

            final File storageLocation = new File(musicFolder, "YouTubeMusic");
            storageLocation.mkdir();

            File oldFile = new File(storageLocation, name);
            File newFile = new File(storageLocation, title + ".mp3");

            Log.v(LOG_TAG, "Old File: " + oldFile.toString() + " New File: " + newFile.toString());
            oldFile.renameTo(newFile);

            metaAccessor.release();
        }
    }

    private MediaMetadataRetriever createMediaMetaDataReceiver(String localUri) {
        MediaMetadataRetriever metaRetriever;
        try {
            metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(localUri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return metaRetriever;
    }

    private void deleteFailedDownload(String fileName) {
        File toBeDeleted = new File(fileName);
        Boolean success = toBeDeleted.delete();
        Log.i(LOG_TAG, "Tried to delete: " + fileName + " Deleted: " + success.toString());
    }
}
