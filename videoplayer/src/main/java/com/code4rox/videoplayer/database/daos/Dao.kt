package com.code4rox.videoplayer.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.code4rox.videoplayer.database.tables.song_detail_table

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(songDetailTable: song_detail_table)

    @Query("UPDATE song_detail_table set song_played_duration=:duration where song_id=:key")
    fun update(key: Long, duration: Long)

    @Query("SELECT * FROM song_detail_table")
    fun getallsongs(): LiveData<List<song_detail_table>>;

    @Query("SELECT * from song_detail_table where songurl=:key")
    fun get(key: String): song_detail_table

    @Query("Delete FROM  song_detail_table where song_id=:key")
    fun deletesongs(key: Long)

//    @Query("UPDATE song_detail_table set playlist_reference=:playlistid where song_id=:key")
//    fun updateplaylist(key: Long, playlistid: Int)

    @Query("Delete FROM song_detail_table")
    fun deleteall();
}