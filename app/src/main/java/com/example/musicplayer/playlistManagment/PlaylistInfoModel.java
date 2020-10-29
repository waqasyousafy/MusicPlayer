package com.example.musicplayer.playlistManagment;

import java.util.ArrayList;

public class PlaylistInfoModel {
    private int playlistId;
    private String playlistName;
    private ArrayList<Integer> songs;

    public ArrayList<Integer> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Integer> songs) {
        this.songs = songs;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }
}
