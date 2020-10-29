package com.code4rox.videoplayer.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.code4rox.videoplayer.database.daos.Dao;
import com.code4rox.videoplayer.database.tables.playlist_detail;
import com.code4rox.videoplayer.database.tables.song_detail_table;

import java.util.List;

public class Repository {
    private Dao dao;
    private com.code4rox.videoplayer.database.daos.playlistdao playlistdao;
    private LiveData<List<song_detail_table>> myallsongs;
    private LiveData<List<playlist_detail>> playlistdetail;
    private song_detail_table song_detail;
    private int id;

    public Repository(Application application) {
        songdatabase db = songdatabase.getDatabase(application);
        dao = db.songdao();
        playlistdao = db.playlistDao();
    }

    public LiveData<List<playlist_detail>> getallplaylist() {
        songdatabase.databaseWriteExecutor.execute(() -> {
            playlistdetail = playlistdao.getallplaylist();
        });
        return playlistdetail;
    }

    public LiveData<List<song_detail_table>> getallsongs() {
        songdatabase.databaseWriteExecutor.execute(() -> {
            myallsongs = dao.getallsongs();
        });
        return myallsongs;
    }

    public void insert(song_detail_table song_detail) {
        songdatabase.databaseWriteExecutor.execute(() -> {
            dao.insert(song_detail);
        });
    }

    public void insert(playlist_detail playlist_detail) {
        songdatabase.databaseWriteExecutor.execute(() -> {
            playlistdao.insert(playlist_detail);
        });
    }

    public void delete(Long id) {
        songdatabase.databaseWriteExecutor.execute(() -> {
            dao.deletesongs(id);
        });
    }

    public void deleteplaylist(int id) {
        songdatabase.databaseWriteExecutor.execute(() -> {
            playlistdao.deleteplaylist(id);
        });
    }

    public void rename(int id, String name) {
        songdatabase.databaseWriteExecutor.execute(() -> {
            playlistdao.updateplaylistname(id, name);
        });
    }

    public void updatePlaylistSongList(List<String> songlist, int id) {
        songdatabase.databaseWriteExecutor.execute(() -> {
            playlistdao.updatesongsplaylist(songlist, id);
        });
    }

    public int getlastplaylistinserted() {

        songdatabase.databaseWriteExecutor.execute(() -> {
            id = playlistdao.getlastinserted();
        });
        return id;
    }

    public song_detail_table getsong(String id) {

        songdatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                song_detail = dao.get(id);
            }
        });
        return song_detail;
    }

    public void deleteallplaylists() {
        songdatabase.databaseWriteExecutor.execute(() -> {
            playlistdao.deleteallplaylist();
        });
    }

    public song_detail_table update(String song) {
        songdatabase.databaseWriteExecutor.execute(() -> {
            song_detail = dao.get(song);
        });
        return song_detail;
    }

    public void deleteallsongs() {
        songdatabase.databaseWriteExecutor.execute(() -> {
            dao.deleteall();
        });
    }

}
