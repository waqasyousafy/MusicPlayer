package com.code4rox.videoplayer.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_detail_table")
data class song_detail_table(
        @PrimaryKey(autoGenerate = true)
        var song_id: Long = 0L,
        @ColumnInfo(name = "songurl")
        var song_url: String = "",
        @ColumnInfo(name = "song_duration")
        var song_duration: Long = 0,
        @ColumnInfo(name = "song_size")
        var song_size: Int = 0,
        @ColumnInfo(name = "song_played_duration")
        var song_played_duration: Long = 0
)