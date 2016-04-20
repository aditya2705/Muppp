package com.adityarathi.muo.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.adityarathi.muo.R;
import com.adityarathi.muo.ui.objects.Song;

import java.util.ArrayList;

/**
 * Created by SHWETHA on 24-02-2016.
 */
public class SongsAdapter extends RecyclerView.Adapter<SongCustomViewHolder> {

    private Context context;
    private ArrayList<Song> customObjectArrayList;

    public SongsAdapter(Context context, ArrayList<Song> customObjectArrayList) {
        this.context = context;
        this.customObjectArrayList = customObjectArrayList;
    }

    @Override
    public SongCustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item_layout, parent, false);
        SongCustomViewHolder customViewHolder = new SongCustomViewHolder(v);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(SongCustomViewHolder holder, int position) {
        Song customObject = customObjectArrayList.get(position);
        holder.songTitle.setText(customObject.getTitle());
        holder.songArtist.setText(customObject.getArtist());
    }

    @Override
    public int getItemCount() {
        return customObjectArrayList.size();
    }
}

class SongCustomViewHolder extends RecyclerView.ViewHolder {

    public TextView songTitle, songArtist;

    public SongCustomViewHolder(View itemView) {
        super(itemView);
        songTitle = (TextView)itemView.findViewById(R.id.song_title);
        songArtist = (TextView)itemView.findViewById(R.id.song_artist);
    }
}


