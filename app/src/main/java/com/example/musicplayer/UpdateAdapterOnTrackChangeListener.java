package com.example.musicplayer;

import android.content.Context;

import com.example.musicplayer.artists.ArtistInfoModel;

import java.util.ArrayList;

public interface UpdateAdapterOnTrackChangeListener {
    public void updatingAdapter(Context context,ArrayList<ArtistInfoModel> s);
}
