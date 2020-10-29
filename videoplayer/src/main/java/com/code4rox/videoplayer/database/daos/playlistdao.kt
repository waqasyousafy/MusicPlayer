package com.code4rox.videoplayer.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.code4rox.videoplayer.database.tables.playlist_detail

@Dao
interface playlistdao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(playlistDetail: playlist_detail)

    @Query("UPDATE playlist_detail set playlist_name=:name where playlist_id=:key")//k+1 is because we get the list of objects that start from the zero and primary key always start from the 1
    fun updateplaylistname(key: Int, name: String)

    @Query("UPDATE playlist_detail SET songslist=:newsonglist where playlist_id=:key")
    fun updatesongsplaylist(newsonglist: List<String>, key: Int)

    @Query("SELECT * FROM playlist_detail")
    fun getallplaylist(): LiveData<List<playlist_detail>>

    @Query("SELECT * FROM playlist_detail where playlist_name=:givenname")
    fun get(givenname: String): playlist_detail

    @Query("SELECT * from playlist_detail where playlist_id=:key")
    fun getplaylist(key: Int):playlist_detail

    @Query("Delete FROM  playlist_detail where playlist_id=:key") //k+1 is because we get the list of objects that start from the zero and primary key always start from the 1
    fun deleteplaylist(key: Int)

    @Query("SELECT playlist_id FROM playlist_detail ORDER BY playlist_id DESC LIMIT 1")
    fun getlastinserted(): Int;

    @Query("Delete FROM playlist_detail")
    fun deleteallplaylist();
}