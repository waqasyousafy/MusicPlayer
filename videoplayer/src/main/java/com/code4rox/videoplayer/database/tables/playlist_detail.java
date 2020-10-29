package com.code4rox.videoplayer.database.tables;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity(tableName = "playlist_detail")
public class playlist_detail {
    @PrimaryKey(autoGenerate = true)
    int playlist_id = 0;
    String playlist_name = "";
    List<String> songslist;
    public playlist_detail() {
    }


    public int getPlaylist_id() {
        return playlist_id;
    }

    public void setPlaylist_id(int playlist_id) {
        this.playlist_id = playlist_id;
    }

    public String getPlaylist_name() {
        return playlist_name;
    }

    public void setPlaylist_name(String playlist_name) {
        this.playlist_name = playlist_name;
    }

    public List<String> getSongslist() {
        return songslist;
    }

    public void setSongslist(List<String> songslist) {
        this.songslist = songslist;
    }
}
