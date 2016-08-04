package com.thenewguy.official.youtubemusic;

/**
 * Created by Fynn on 7/30/2016.
 */
public class SearchItem {

    private final String id;
    private final String title;
    private final String channel;
    private final String image;
    private final String type;
    private final String channelId; 


    public SearchItem(String itemId, String itemTitle, String itemChannel, String itemImage, String itemChannelId, String itemType) {
        id = itemId;
        title = itemTitle;
        channel = itemChannel;
        image = itemImage;
        channelId = itemChannelId;
        type = itemType;
    }


    public String getTitle() {
        return title;
    }

    public String getChannel() {
        return channel;
    }

    public String getImage() {
        return image;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getChannelId() {
        return channelId;
    }
}
