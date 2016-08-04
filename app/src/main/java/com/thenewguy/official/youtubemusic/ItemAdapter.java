package com.thenewguy.official.youtubemusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wefree.fynn.youtubemusic.R;

import java.util.ArrayList;

/**
 * Created by Fynn on 7/31/2016.
 */
public class ItemAdapter extends ArrayAdapter<SearchItem>{

    private Context mContext;

    public ItemAdapter(Context c, ArrayList<SearchItem> results) {
        super(c, 0, results);
        mContext = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_video, parent, false);
        }

        TextView itemTitle = (TextView) convertView.findViewById(R.id.item_title);
        TextView itemChannel = (TextView) convertView.findViewById(R.id.item_channel);
        ImageView itemPreview = (ImageView) convertView.findViewById(R.id.item_preview);

        itemTitle.setText(item.getTitle());
        Picasso.with(mContext).load(item.getImage()).fit().into(itemPreview);
        if (item.getType().equals(AppConstants.SearchType.CHANNEL)) {
            return convertView;
        }

        itemChannel.setText(item.getChannel());

        return convertView;
    }



}
