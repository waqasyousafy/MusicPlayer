package com.code4rox.videoplayer.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.code4rox.videoplayer.database.daos.Dao;
import com.code4rox.videoplayer.database.daos.playlistdao;
import com.code4rox.videoplayer.database.tables.playlist_detail;
import com.code4rox.videoplayer.database.tables.song_detail_table;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {song_detail_table.class, playlist_detail.class}, version = 1, exportSchema = false)
@TypeConverters(DataConverter.class)
public abstract class songdatabase extends RoomDatabase {

    public abstract Dao songdao();

    public abstract playlistdao playlistDao();

    private static volatile songdatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static songdatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (songdatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            songdatabase.class, "songplaylistdatabase")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                Dao dao = INSTANCE.songdao();
                playlistdao playlistdao = INSTANCE.playlistDao();

            });
        }
    };
}