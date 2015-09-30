package com.blackboxstudios.rafael.reclutamento_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blackboxstudios.rafael.reclutamento_android.R;
import com.blackboxstudios.rafael.reclutamento_android.objects.Episode;

import java.util.List;

/**
 * Created by Rafael on 30/09/2015.
 */
public class EpisodeAdapter extends BaseAdapter {
    List<Episode> mEpisodeList;
    LayoutInflater mInflater;

    public EpisodeAdapter(Context context, List<Episode> mEpisodeList) {
        this.mEpisodeList = mEpisodeList;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mEpisodeList.size();
    }

    @Override
    public Object getItem(int position) {
        return mEpisodeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if(convertView == null) {
            view = mInflater.inflate(R.layout.episode_item, parent, false);
            holder = new ViewHolder();
            holder.episodeName = (TextView)view.findViewById(R.id.episode_name);
            holder.episodeNumber = (TextView)view.findViewById(R.id.episode_number);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }

        Episode episode = mEpisodeList.get(position);
        holder.episodeName.setText(episode.getTitle());
        holder.episodeNumber.setText(episode.getNumber());

        return view;
    }

    private class ViewHolder {
        public TextView episodeName;
        public TextView episodeNumber;
    }
}