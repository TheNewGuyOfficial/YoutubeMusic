package com.thenewguy.official.youtubemusic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.thenewguy.official.youtubemusic.AppConstants.*;

import java.util.ArrayList;

/**
 * Created by Fynn on 8/2/2016.
 */
public class ExtractFromJson {

    public static ArrayList<SearchItem> parseResponse(String response, String searchType) {
        try {
            JSONObject json = new JSONObject(response);
            JSONArray itemArray = json.getJSONArray(YoutubeJson.ARRAY_OBJECT);
            ArrayList<SearchItem> results = new ArrayList<>();

            for (int x = 0; x < itemArray.length(); x++) {
                JSONObject item = itemArray.getJSONObject(x);
                JSONObject itemId = null;
                if (!searchType.equals(SearchType.PLAYLIST_ITEM)) {
                    itemId = item.getJSONObject(YoutubeJson.ID_OBJECT);
                }
                JSONObject itemSnippet = item.getJSONObject(YoutubeJson.SNIPPET);


                String id = "";
                if (searchType.equals(SearchType.VIDEO)) {
                    id = itemId.getString(YoutubeJson.ID_VIDEO);
                } else if (searchType.equals(SearchType.PLAYLIST)) {
                    id = itemId.getString(YoutubeJson.ID_PLAYLIST);
                } else if (searchType.equals(SearchType.CHANNEL)) {
                    id = itemId.getString(YoutubeJson.ID_CHANNEL);
                } else if (searchType.equals(SearchType.PLAYLIST_ITEM)) {
                    JSONObject itemResourceId = itemSnippet.getJSONObject(YoutubeJson.RESOURCE_ID);
                    id = itemResourceId.getString(YoutubeJson.ID_VIDEO);
                }
                String title = itemSnippet.getString(YoutubeJson.TITLE);
                String channelTitle = itemSnippet.getString(YoutubeJson.CHANNEL);
                String channelId = itemSnippet.getString(YoutubeJson.ID_CHANNEL);

                JSONObject thumbnails = itemSnippet.optJSONObject(YoutubeJson.THUMBNAILS);
                if (thumbnails != null) {
                    JSONObject defaultSize = thumbnails.optJSONObject(YoutubeJson.THUMBNAILS_DEFAULT);
                    String imageUrl = defaultSize.optString(YoutubeJson.THUMBNAILS_URL);
                    results.add(new SearchItem(id, title, channelTitle, imageUrl, channelId, searchType));
                }
            }
            return results;



        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String nextPageToken(String jsonString) {
        String nextPageToken = "";
        try {
            JSONObject json = new JSONObject(jsonString);
            nextPageToken = json.optString(YoutubeJson.NEXT_PAGE_TOKEN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nextPageToken;
    }

    public static String getNumResults(String jsonString) {
        String results = "";

        try {
            JSONObject json = new JSONObject(jsonString);
            JSONObject pageInfo = json.optJSONObject(YoutubeJson.PAGE_INFO);
            if (pageInfo != null) {
                results = pageInfo.optString(YoutubeJson.TOTAL_RESULTS);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }
}
