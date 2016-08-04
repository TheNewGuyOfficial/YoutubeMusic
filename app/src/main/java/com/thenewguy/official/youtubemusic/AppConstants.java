package com.thenewguy.official.youtubemusic;

/**
 * Created by Fynn on 8/2/2016.
 * Stores constants for creating queries
 */
public class AppConstants {
    public static final String YOUTUBE_SEARCH_BASE_URL = "https://www.googleapis.com/youtube/v3/search?";
    public static final String YOUTUBE_API_KEY = "AIzaSyBAXomz1ECzcM6Qdp1byhnALrDzuh1LF4c";
    public static final String YOUTUBE_PLAYLIST_BASE = "https://www.googleapis.com/youtube/v3/playlists";
    public static final String YOUTUBE_PLAYLIST_ITEM_BASE = "https://www.googleapis.com/youtube/v3/playlistItems";
    public static final String YOUTUBE_VIDEO_BASE_URL = "https://www.youtube.com/watch?v=";
    public static final String PLAYLIST_ACTIVITY_URL_KEY = "urlKey";

    public class YoutubeSearch {
        public static final String QUERY = "q";
        public static final String PART = "part";
        public static final String TYPE = "type";
        public static final String API_KEY = "key";
        public static final String MAX_RESULTS = "maxResults";
        public static final String ORDER = "order";
        public static final String ID = "id";
        public static final String PLAYLIST_ID = "playlistId";
        public static final String PAGE_TOKEN = "pageToken";
    }

    public class SearchType {
        public static final String KEY = "Type";
        public static final String VIDEO = "video";
        public static final String PLAYLIST = "playlist";
        public static final String CHANNEL = "channel";
        public static final String PLAYLIST_ITEM = "playlist_item";
    }

    public class YoutubeJson {
        public static final String ARRAY_OBJECT = "items";
        public static final String ID_OBJECT = "id";
        public static final String ID_VIDEO = "videoId";
        public static final String ID_PLAYLIST = "playlistId";
        public static final String ID_CHANNEL = "channelId";
        public static final String SNIPPET = "snippet";
        public static final String TITLE = "title";
        public static final String CHANNEL = "channelTitle";
        public static final String THUMBNAILS = "thumbnails";
        public static final String THUMBNAILS_DEFAULT = "default";
        public static final String THUMBNAILS_URL = "url";
        public static final String RESOURCE_ID = "resourceId";
        public static final String NEXT_PAGE_TOKEN = "nextPageToken";
        public static final String TOTAL_RESULTS = "totalResults";
        public static final String PAGE_INFO = "pageInfo";
    }
}
