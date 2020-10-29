package com.example.musicplayer.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.musicplayer.R;
import com.example.musicplayer.albums.AlbumModel;
import com.example.musicplayer.artists.ArtistInfoModel;
import com.example.musicplayer.geners.GenresModel;
import com.example.musicplayer.models.AudioBuckets;
import com.example.musicplayer.playlistManagment.PlaylistInfoModel;
import com.example.musicplayer.tracks.TrackInfoModel;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static java.util.Collections.max;

public class Utils {
    private static int maxid;
    private static String playlistname;

    //Album Working
    public static ArrayList<AlbumModel> getAllAudioAlbum(Context context) {
        Boolean firstEntery = true;
        ArrayList<AlbumModel> albumList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri uri2 = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ALBUM,
        };
        Cursor c = context.getContentResolver().query(uri, projection, null, null, MediaStore.Audio.Artists.ARTIST + " ASC");

        if (c != null) {
            while (c.moveToNext()) {
                // Create a model object.
                int albumId = c.getInt(0);
                String albumName = c.getString(1);
                AlbumModel albumModel = new AlbumModel();
                albumModel.setAlbumId(albumId);
                albumModel.setAlbumName(albumName);
                if (firstEntery) {
                    albumList.add(albumModel);
                    firstEntery = false;
                }
                try {
                    int counterMatch = 0;
                    for (AlbumModel albumModel1 : albumList) {
                        if (albumModel1.getAlbumId() == albumModel.getAlbumId()) {
                            counterMatch++;
                        }
                    }
                    if (counterMatch == 0) {
                        albumList.add(albumModel);
                    }
                } catch (Exception ex) {
                }
                // Set data to the model object.
            }
            Log.d("albumsget", "" + albumList);
            c.close();
            for (AlbumModel albumModel1 : albumList) {
                String selection = MediaStore.Audio.AudioColumns.ALBUM_ID + "='" + albumModel1.getAlbumId() + "'";
                Cursor c2 = context.getContentResolver().query(uri, new String[]{MediaStore.Audio.AudioColumns.DATA}, selection, null, "");
                if (c2 != null) {
                    albumModel1.setNumberOfTracks(c2.getCount());
                }

                c2.close();

            }
        }
        // Return the list.
        return albumList;
    }

    public static ArrayList<TrackInfoModel> getAllSongsOfAlbum(Context context, int albumId) {
        ArrayList<TrackInfoModel> tracklistWithArtist = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {

                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ARTIST_ID,
                MediaStore.Audio.AudioColumns.COMPOSER,
                MediaStore.Audio.AudioColumns._ID
        };
        String selection = MediaStore.Audio.AudioColumns.ALBUM_ID + "='" + albumId + "'";
        Cursor c = context.getContentResolver().query(uri, projection, selection, null, MediaStore.Audio.AudioColumns.TITLE + " DESC");
        if (c != null) {
            while (c.moveToNext()) {
                TrackInfoModel trackInfoModel = new TrackInfoModel();
                trackInfoModel.setPath(c.getString(0));
                trackInfoModel.setName(c.getString(1));
                trackInfoModel.setDuration(c.getLong(5));
                trackInfoModel.setArtist(c.getString(3));
                trackInfoModel.setAlbum(c.getString(2));
                trackInfoModel.setTrackId(c.getInt(10));
                tracklistWithArtist.add(trackInfoModel);
            }
        }
        return tracklistWithArtist;
    }

    //Genres Working
    public static ArrayList<GenresModel> getAllAudioGenres(Context context) {
        String NEW_GENRES = "newGenres";
        ArrayList<GenresModel> genresList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        Uri uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME};
        String[] projection2 = {
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ARTIST
        };
        HashSet<Integer> songsWithGenres = new HashSet<>();
        HashSet<Integer> songsWithoutGenres = new HashSet<>();
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                // Create a model object.
                GenresModel genresModel = new GenresModel();
                int genresId = c.getInt(0);
                Cursor songsWithGenresCursor = context.getContentResolver().query(MediaStore.Audio.Genres.Members.
                        getContentUri("external", genresId), projection2, null, null, null);
                String genresName = c.getString(1);
                if (songsWithGenresCursor != null) {
                    while (songsWithGenresCursor.moveToNext()) {
                        songsWithGenres.add(songsWithGenresCursor.getInt(0));
//                        Log.d("genresSongs", "" + songsWithGenresCursor.getInt(0));
//                        Log.d("genresSongs", "" + songsWithGenresCursor.getString(1));
//                        Log.d("genresSongs", "" + songsWithGenresCursor.getString(2));
                    }

                }
                // Set data to the model object.
                genresModel.setGenresId(genresId);
                genresModel.setGenresName(genresName);
                genresModel.setNumberOfSongs(songsWithGenresCursor.getCount());
                if (songsWithGenresCursor != null) {
                    songsWithGenresCursor.close();
                }
                if (genresModel.getNumberOfSongs() > 0)
                    genresList.add(genresModel);
            }
            c.close();
            Cursor allsongsCursor = context.getContentResolver().query(uri2, projection2, null, null, null);
            if (allsongsCursor != null) {
                while (allsongsCursor.moveToNext()) {
                    for (int songWithGenre : songsWithGenres) {
                        if (songWithGenre == allsongsCursor.getInt(0)) {//allsongsCursor.getInt(0) -> SongId
                            songsWithoutGenres.add(allsongsCursor.getInt(0));
                        }
                    }
                }
                int numberofSongsWithoutGenres = (allsongsCursor.getCount() - songsWithGenres.size());
                Log.d("songswithoutgenres", "" + songsWithoutGenres + " remaining songs" + (allsongsCursor.getCount() - songsWithGenres.size()));
                GenresModel genresModel = new GenresModel();
                genresModel.setGenresId(1234);
                genresModel.setGenresName("unknown");
                genresModel.setNumberOfSongs(numberofSongsWithoutGenres);
                genresList.add(genresModel);
                allsongsCursor.close();
            }
        }
        SharedPreferences sharedPref = ((Activity) context).getPreferences(Context.MODE_PRIVATE);
        if (sharedPref.getInt(NEW_GENRES, 0) == 0) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(NEW_GENRES, 1);
            editor.commit();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Genres._ID, 1234);
            values.put(MediaStore.Audio.Genres.NAME, "unknown");
            context.getContentResolver().insert(uri2, values);
        }
        // Return the list.
        return genresList;
    }

    public static ArrayList<TrackInfoModel> getAllSongsOfGenres(Context context, int genresId) {
        ArrayList<TrackInfoModel> trackList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
        Uri uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection2 = {
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ARTIST
        };
        if (genresId != 1234) {
            Cursor songsWithGenresCursor = context.getContentResolver().query(MediaStore.Audio.Genres.Members.
                    getContentUri("external", genresId), projection2, null, null, null);
            if (songsWithGenresCursor != null) {
                while (songsWithGenresCursor.moveToNext()) {
                    TrackInfoModel trackInfoModel = new TrackInfoModel();
                    trackInfoModel.setPath(songsWithGenresCursor.getString(1));
                    trackInfoModel.setName(songsWithGenresCursor.getString(2));
                    trackInfoModel.setArtist(songsWithGenresCursor.getString(3));
                    trackInfoModel.setTrackId(songsWithGenresCursor.getInt(0));
                    trackList.add(trackInfoModel);
                }

            }
            if (songsWithGenresCursor != null) {
                songsWithGenresCursor.close();
            }
        } else if (genresId == 1234) {
            trackList.clear();
            String[] projection3 = {
                    MediaStore.Audio.Genres._ID,
                    MediaStore.Audio.Genres.NAME};
            HashSet<Integer> songsWithGenres = new HashSet<>();
            HashSet<Integer> songsWithoutGenres = new HashSet<>();
            Cursor c = context.getContentResolver().query(uri, projection3, null, null, null);
            if (c != null) {
                while (c.moveToNext()) {
                    int genresId1 = c.getInt(0);
                    Cursor songsWithGenresCursor = context.getContentResolver().query(MediaStore.Audio.Genres.Members.
                            getContentUri("external", genresId1), projection2, null, null, null);
                    if (songsWithGenresCursor != null) {
                        while (songsWithGenresCursor.moveToNext()) {
                            songsWithGenres.add(songsWithGenresCursor.getInt(0));
                        }

                    }
                    if (songsWithGenresCursor != null) {
                        songsWithGenresCursor.close();
                    }
                }
                c.close();
                Cursor allsongsCursor = context.getContentResolver().query(uri2, projection2, null, null, null);
                if (allsongsCursor != null) {
                    while (allsongsCursor.moveToNext()) {
                        int counter = 0;
                        int songid = allsongsCursor.getInt(0);
                        for (int songWithGenre : songsWithGenres) {
                            if (songWithGenre == songid) {//allsongsCursor.getInt(0) -> SongId
                                counter++;

                            }
                        }
                        if (counter == 0) {
                            songsWithoutGenres.add(allsongsCursor.getInt(0));
                        }
                    }
                    for (int songsWithOutGenresItem : songsWithoutGenres) {
                        Cursor songsWithoutGenresCursor = context.getContentResolver().query(uri2, projection2, MediaStore.Audio.Media._ID + "=" + songsWithOutGenresItem, null, null);
                        if (songsWithoutGenresCursor != null) {
                            while (songsWithoutGenresCursor.moveToNext()) {
                                TrackInfoModel trackInfoModel = new TrackInfoModel();
                                trackInfoModel.setPath(songsWithoutGenresCursor.getString(1));
                                trackInfoModel.setName(songsWithoutGenresCursor.getString(2));
                                trackInfoModel.setArtist(songsWithoutGenresCursor.getString(3));
                                trackInfoModel.setTrackId(songsWithoutGenresCursor.getInt(0));
                                trackList.add(trackInfoModel);
                            }
                        }
                        songsWithoutGenresCursor.close();
                    }
                    int numberofSongsWithoutGenres = (allsongsCursor.getCount() - songsWithGenres.size());
                    allsongsCursor.close();
                }
            }
        }
        return trackList;
    }


    ///////////////////////////////Bitmap Loading //////////////////////////////////////////
//    Bitmap thumbnail =
//            getApplicationContext().getContentResolver().loadThumbnail(
//                    content-uri, new Size(640, 480), null);
    public static Uri getArtUriFromMusicFile(Context context, File file) {
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = {MediaStore.Audio.Media.ALBUM_ID};

        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1 AND " + MediaStore.Audio.Media.DATA + " = '"
                + file.getAbsolutePath() + "'";
        final Cursor cursor = context.getApplicationContext().getContentResolver().query(uri, cursor_cols, where, null, null);
        /*
         * If the cusor count is greater than 0 then parse the data and get the art id.
         */
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
            cursor.close();
            return albumArtUri;
        }

        return Uri.EMPTY;
    }

    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    private static Bitmap mCachedBit = null;

    public static Bitmap getArtworkFromFile(Context context, int songid, int albumid) {

        Bitmap bm = null;
        byte[] art = null;
        String path = null;
        if (albumid < 0 && songid < 0) {
            throw new IllegalArgumentException("Must specify an album or a song id");
        }
        try {
            if (albumid < 0) {
                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            } else {
                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    bm = BitmapFactory.decodeFileDescriptor(fd);
                }
            }
        } catch (FileNotFoundException ex) {
            //
        }
        return bm;
    }

    @SuppressLint("ResourceType")
    private static Bitmap getDefaultArtwork(Context context) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeStream(
                context.getResources().openRawResource(R.drawable.ic_album), null, opts);
    }

    public static int getIntPref(Context context, String name, int def) {
        SharedPreferences prefs =
                context.getSharedPreferences("com.android.music", Context.MODE_PRIVATE);
        return prefs.getInt(name, def);
    }

    static void setIntPref(Context context, String name, int value) {
        SharedPreferences prefs =
                context.getSharedPreferences("com.android.music", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putInt(name, value);
        ed.commit();
    }

    public static Bitmap getArtwork(Context context, int song_id, int album_id) {
        if (album_id < 0) {
            // This is something that is not in the database, so get the album art directly
            // from the file.
            if (song_id >= 0) {
                Bitmap bm = getArtworkFromFile(context, song_id, -1);
                if (bm != null) {
                    return bm;
                }
            }
            return getDefaultArtwork(context);
        }
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            InputStream in = null;
            try {
                in = ((ContentResolver) res).openInputStream(uri);
                return BitmapFactory.decodeStream(in, null, sBitmapOptions);
            } catch (FileNotFoundException ex) {
                // The album art thumbnail does not actually exist. Maybe the user deleted it, or
                // maybe it never existed to begin with.
                Bitmap bm = getArtworkFromFile(context, song_id, album_id);
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                        if (bm == null) {
                            return getDefaultArtwork(context);
                        }
                    }
                } else {
                    bm = getDefaultArtwork(context);
                }
                return bm;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                }
            }
        }

        return null;
    }

    //Artist Working
    public static ArrayList<ArtistInfoModel> getAllArtistsFromAudio(Context context) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.AudioColumns.ARTIST_ID,
                MediaStore.Audio.AudioColumns.ARTIST,
        };
        Uri uri2 = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        String[] projection2 = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST
        };
        Cursor c = context.getContentResolver().query(uri2, projection2, null, null, MediaStore.Audio.Artists.ARTIST + " ASC");
        ArrayList<ArtistInfoModel> artistList = new ArrayList<>();
        if (c != null) {
            while (c.moveToNext()) {
                // Create a model object.
                ArtistInfoModel artistInfoModel = new ArtistInfoModel();
                int artistId = c.getInt(0);
                String artist = c.getString(1);
                Log.d("albums", "ARTIST" + artist);// Retrieve artist.
                // Set data to the model object.
                artistInfoModel.setArtistId(artistId);
                artistInfoModel.setArtistName(artist);
                // Add the model object to the list .
                artistList.add(artistInfoModel);
            }
        }
        if (c != null) {
            c.close();
        }
        HashSet<Integer> albumIds = new HashSet<>();
        for (ArtistInfoModel artist : artistList) {//foreach loop
            String selection = MediaStore.Audio.AudioColumns.ARTIST_ID + "='" + artist.getArtistId() + "'";
            Cursor c1 = context.getContentResolver().query(uri, new String[]{MediaStore.Audio.AudioColumns.ALBUM_ID}, selection, null, null);
            if (c1 != null) {
                while (c1.moveToNext()) {
                    albumIds.add(c1.getInt(0));
                }
            }
            Log.d("albums", "inside:C" + c1.getCount());
            Log.d("albums", "inside:alb" + albumIds);
            artist.setNumberOfAlbums(albumIds.size());
            artist.setNumberofTracks(c1.getCount());
            c1.close();
            Log.d("albums", "" + artist.getArtistId() + ":albms:" + albumIds);
            albumIds.clear();

//            artistList.get(i).setNumberOfAlbums(album.size());
        }

        // Return the list.
        return artistList;
    }

    public static ArrayList<TrackInfoModel> getAllSongsOfArtist(Context context, int artistId) {
        ArrayList<TrackInfoModel> tracklistWithArtist = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ARTIST_ID,
                MediaStore.Audio.AudioColumns.COMPOSER,
                MediaStore.Audio.AudioColumns._ID
        };
        String selection = MediaStore.Audio.AudioColumns.ARTIST_ID + "='" + artistId + "'";
        Cursor c = context.getContentResolver().query(uri, projection, selection, null, MediaStore.Audio.AudioColumns.TITLE + " DESC");
        if (c != null) {
            while (c.moveToNext()) {
                TrackInfoModel trackInfoModel = new TrackInfoModel();
                trackInfoModel.setPath(c.getString(0));
                trackInfoModel.setName(c.getString(1));
                trackInfoModel.setDuration(c.getLong(5));
                trackInfoModel.setArtist(c.getString(3));
                trackInfoModel.setTrackId(c.getInt(10));
                trackInfoModel.setAlbum(c.getString(2));
                trackInfoModel.setAlbumId(c.getInt(7));
                tracklistWithArtist.add(trackInfoModel);
            }
        }
        return tracklistWithArtist;
    }

    public static ArrayList<AlbumModel> getallAlbumsOfArtist(Context context, int artistId) {
        Boolean firstEntery = true;
        ArrayList<AlbumModel> albumList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri uri2 = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ALBUM,
        };
        String selection1 = MediaStore.Audio.AudioColumns.ARTIST_ID + "='" + artistId + "'";
        Cursor c = context.getContentResolver().query(uri, projection, selection1, null, MediaStore.Audio.Artists.ARTIST + " ASC");

        if (c != null) {
            while (c.moveToNext()) {
                // Create a model object.
                int albumId = c.getInt(0);
                String albumName = c.getString(1);
                AlbumModel albumModel = new AlbumModel();
                albumModel.setAlbumId(albumId);
                albumModel.setAlbumName(albumName);
                if (firstEntery) {
                    albumList.add(albumModel);
                    firstEntery = false;
                }
                try {
                    int counterMatch = 0;
                    for (AlbumModel albumModel1 : albumList) {
                        if (albumModel1.getAlbumId() == albumModel.getAlbumId()) {
                            counterMatch++;
                        }
                    }
                    if (counterMatch == 0) {
                        albumList.add(albumModel);
                    }
                } catch (Exception ex) {
                }
                // Set data to the model object.
            }
            Log.d("albumsget", "" + albumList);
            c.close();
            for (AlbumModel albumModel1 : albumList) {
                String selection = MediaStore.Audio.AudioColumns.ALBUM_ID + "='" + albumModel1.getAlbumId() + "'";
                Cursor c2 = context.getContentResolver().query(uri, new String[]{MediaStore.Audio.AudioColumns.DATA}, selection, null, "");
                if (c2 != null) {
                    albumModel1.setNumberOfTracks(c2.getCount());
                }

                c2.close();

            }
        }
        // Return the list.
        return albumList;
    }

    //All Songs Getting
    public static ArrayList<TrackInfoModel> getAllAudioTracks(Context context) {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {

                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ARTIST_ID,
                MediaStore.Audio.AudioColumns.COMPOSER,
                MediaStore.Audio.AudioColumns.SIZE,
                MediaStore.Audio.AudioColumns._ID
        };
        Cursor c = context.getContentResolver().query(uri, projection, null, null, MediaStore.Audio.AudioColumns.TITLE + " DESC");
        ArrayList<Integer> artistlist = new ArrayList<>();
        ArrayList<Integer> albumlist = new ArrayList<>();
        ArrayList<TrackInfoModel> tracksList = new ArrayList<>();
        HashSet<Integer> ids = new HashSet<>();
        if (c != null) {
            while (c.moveToNext()) {
                // Create a model object.
                TrackInfoModel TrackInfoModel = new TrackInfoModel();
                String path = c.getString(0);   // Retrieve path.
                String name = c.getString(1);   // Retrieve name.
                String album = c.getString(2);  // Retrieve album name.
                String artist = c.getString(3); // Retrieve artist name.
                long duration = c.getLong(5);
                String date = c.getString(6);
                int artisitId = c.getInt(8);
                int albumid = c.getInt(7);
                int trackId = c.getInt(11);
                long size = c.getLong(10);
                // Set data to the model object.
                TrackInfoModel.setName(name);
                TrackInfoModel.setAlbum(album);
                TrackInfoModel.setArtist(artist);
                TrackInfoModel.setPath(path);
                TrackInfoModel.setTrackId(trackId);
                TrackInfoModel.setAlbumId(albumid);
                TrackInfoModel.setSize(size);
                TrackInfoModel.setDuration(duration);
                ids.add(artisitId);
                albumlist.add(albumid);
                Log.e("Name :" + name, " Album :" + album);
                Log.d("artiste", "" + artistlist);
                // Add the model object to the list .
                tracksList.add(TrackInfoModel);

            }
            c.close();
        }
        artistlist.addAll(ids);
        HashSet<String> album = new HashSet<>();
        for (int i = 0; i < artistlist.size(); i++) {
            String selection = MediaStore.Audio.AudioColumns.ARTIST_ID + "='" + artistlist.get(i) + "'";
            Cursor c1 = context.getContentResolver().query(uri, new String[]{MediaStore.Audio.AudioColumns.ALBUM}, selection, null, null);
            if (c1 != null) {
                while (c1.moveToNext()) {
                    album.add(c1.getString(0));
                }
            }
            c1.close();
            Log.d("album123", "" + album);
        }

        // Return the list.
        return tracksList;
    }

    public static String getInTimeFormate(long millis) {
        String timeconverted;
        if (millis < 3599999) {
            timeconverted =
                    String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millis),
                            TimeUnit.MILLISECONDS.toSeconds(millis) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
                    );
        } else {
            timeconverted = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        }
        return timeconverted;

    }

    public static int updateAudioSongsInformation(Context context, int id, String title, String artist, String album) {
        ContentValues values = new ContentValues();
        if (title != null)
            values.put(MediaStore.Audio.AudioColumns.TITLE, title);
        if (artist != null)
            values.put(MediaStore.Audio.AudioColumns.ARTIST, artist);
        if (album != null)
            values.put(MediaStore.Audio.AudioColumns.ALBUM, album);
        Uri uri2 = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        int res = context.getContentResolver().update(uri2, values, null, null);
        return res;
    }

    public static final boolean createPlaylist(final Context context, final String name) {
        boolean response = false;
        if (name != null && name.length() > 0) {
            final ContentResolver resolver = context.getContentResolver();
            final String[] projection = {

                    MediaStore.Audio.Playlists.NAME,
                    MediaStore.Audio.Playlists._ID
            };
            final String selection = MediaStore.Audio.Playlists.NAME + " = '" + name + "'";
            Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    projection, selection, null, null);
            if (cursor.getCount() <= 0) {
                final ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.PlaylistsColumns.NAME, name);
                resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                        values);
                response = true;
//                return Long.parseLong(uri.getLastPathSegment());
                Toast.makeText(context, "playlist created" + name, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context, "playlist already created", Toast.LENGTH_SHORT).show();
                response = false;
            }
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }

        }
        return response;
    }

    public static ArrayList<PlaylistInfoModel> getAllPlaylists(Context context) {
        ArrayList<PlaylistInfoModel> playlistList = new ArrayList<>();
        int playlistId;
        final String[] projection2 = {
                MediaStore.Audio.Playlists.NAME,
                MediaStore.Audio.Playlists._ID
        };
        final ContentResolver resolver = context.getContentResolver();
        Cursor cursor2 = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                projection2, null, null, null);
        if (cursor2 != null) {
            while (cursor2.moveToNext()) {
                playlistId = cursor2.getInt(1);
                playlistname = cursor2.getString(0);
                PlaylistInfoModel playlistInfoModel = new PlaylistInfoModel();
                playlistInfoModel.setPlaylistId(playlistId);
                playlistInfoModel.setPlaylistName(playlistname);
                playlistList.add(playlistInfoModel);
                ;
                Log.d("playlists", "========playlist name======" + cursor2.getString(0) + "=====playlist id:====== " + cursor2.getInt(1) + "========Total Playlists=========" + cursor2.getCount());
            }
            cursor2.close();
        }
        cursor2.close();

        return playlistList;
    }

    public static void shareAudio(String path, Context context) {
        File f = new File(path);
        Uri uri = Uri.parse("file://" + f.getAbsolutePath());
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setType("audio/*");
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(share, "Share audio File"));

    }

    public static void shareAudios(ArrayList<TrackInfoModel> tracks, Context context) {
        ArrayList<Uri> uris = new ArrayList<>();
        File f = null;
        for (TrackInfoModel trackInfoModel : tracks) {
            f = new File(trackInfoModel.getPath());
            Uri uri = Uri.parse("file://" + f.getAbsolutePath());
            uris.add(uri);
        }
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        share.setType("audio/*");
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(share, "Share audio File"));

    }

    public static int getLastPlaylistid(Context context) {

        int playlistId = 0;
        final String[] projection2 = {
                MediaStore.Audio.Playlists.NAME,
                MediaStore.Audio.Playlists._ID
        };
        final ContentResolver resolver = context.getContentResolver();
        Cursor cursor2 = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                projection2, null, null, null);
        if (cursor2 != null) {
            while (cursor2.moveToNext()) {
                playlistId = cursor2.getInt(1);
                Log.d("playlists", "========playlist name======" + cursor2.getString(0) + "=====playlist id:====== " + cursor2.getInt(1) + "========Total Playlists=========" + cursor2.getCount());
            }
            cursor2.close();
        }
        cursor2.close();

        return playlistId;
    }

    public static Boolean editTextValidation(EditText editText) {
        final Boolean[] isValid = {true};
        editText.setFilters(new InputFilter[]{new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String regex = "a-z~@#$%^&*:;<>.,/}{+";
                if (!dest.toString().contains(" ") || !dest.toString().matches("[" + regex + "]+")) {
                    isValid[0] = false;
                    return null;
                }

                return source;
            }
        }
        });
        return isValid[0];
    }

    public static void addTOPlaylistByID(Context context, int playlistId, ArrayList<Integer> AIDs) {
        String[] cols = new String[]{"count(*)"};
        final ContentResolver Cn = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        Cursor cur = Cn.query(uri, cols, null, null, null);
        final int base;
        cur.moveToNext();
        base = cur.getInt(0);
        cur.close();
        ArrayList<TrackInfoModel> trackInfoModelArrayList = new ArrayList<>();
        trackInfoModelArrayList = getAllSongsFromPlaylist(context, playlistId);
        int count = 0;
        for (int j = 0; j < AIDs.size(); j++) {
            int match = 0;
            for (int i = 0; i < trackInfoModelArrayList.size(); i++) {
                if (AIDs.get(j) == trackInfoModelArrayList.get(i).getTrackId()) {
                    match++;
                    count++;
                }
            }
            if (match == 0) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, AIDs.get(j));
                values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + AIDs.get(j)));
                Cn.insert(uri, values);
            }

        }
        if (count == AIDs.size()) {
            Toast.makeText(context, "Already added in playlist", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully added", Toast.LENGTH_SHORT).show();
        }

    }

    public static void addTOPlaylistByID(Context context, int playlistId, int id) {
        String[] cols = new String[]{"count(*)"};
        final ContentResolver Cn = context.getContentResolver();
        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        Cursor cur = Cn.query(uri, cols, null, null, null);
        final int base;
        cur.moveToNext();
        base = cur.getInt(0);
        cur.close();
        ArrayList<TrackInfoModel> trackInfoModelArrayList = new ArrayList<>();
        trackInfoModelArrayList = getAllSongsFromPlaylist(context, playlistId);
        int match = 0;
        for (int i = 0; i < trackInfoModelArrayList.size(); i++) {
            if (id == trackInfoModelArrayList.get(i).getTrackId()) {
                match++;
            }
        }
        if (match == 0) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, id);
            values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Integer.valueOf(base + id));
            Cn.insert(uri, values);
            Toast.makeText(context, "Song has added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Song has already added", Toast.LENGTH_LONG).show();
        }
    }

    public static void deleteSong(Context context, int songId) {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + "=" + songId, null);
    }

    public static void deleteArtist(Context context, int artistId) {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media.ARTIST_ID + "=" + artistId, null);
    }

    public static void deleteAlbum(Context context, int albumId) {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media.ALBUM_ID + "=" + albumId, null);
    }

    public static void deleteGenres(Context context, int genresId) {
        ArrayList<TrackInfoModel> trackInfoModels = getAllSongsOfGenres(context, genresId);
        ContentResolver contentResolver = context.getContentResolver();
        for (int i = 0; i < trackInfoModels.size(); i++)
            contentResolver.delete(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MediaStore.Audio.Media._ID + "=" + trackInfoModels.get(i).getTrackId(), null);
    }

    public static final int getSongCountForPlaylist(final Context context, final long playlistId) {
        ArrayList<Integer> playlistSongsIds = new ArrayList<>();
        Cursor c = context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                new String[]{BaseColumns._ID}, null, null, null);
        if (c != null) {
            int count = 0;
            if (c.moveToFirst()) {
                count = c.getCount();
                playlistSongsIds.add(c.getInt(0));
            }
            c.close();
            c = null;
            return count;
        }
        return 0;
    }

    public static ArrayList<TrackInfoModel> getAllSongsFromPlaylist(Context context, int playlistId) {
        ArrayList<TrackInfoModel> tracksList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Uri playListUri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId); //playlistID is the _ID of the given playlist
        Cursor cursor = contentResolver.query(playListUri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                do {
                    String track_id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID));
                    Uri mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    String[] trackProjection = new String[]{
                            MediaStore.Audio.AudioColumns.DATA,
                            MediaStore.Audio.AudioColumns.TITLE,
                            MediaStore.Audio.AudioColumns.ALBUM,
                            MediaStore.Audio.AudioColumns.ARTIST,
                            MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                            MediaStore.Audio.AudioColumns.DURATION,
                            MediaStore.Audio.AudioColumns.YEAR,
                            MediaStore.Audio.AudioColumns.ALBUM_ID,
                            MediaStore.Audio.AudioColumns.ARTIST_ID,
                            MediaStore.Audio.AudioColumns.COMPOSER,
                            MediaStore.Audio.AudioColumns.SIZE,
                            MediaStore.Audio.AudioColumns._ID
                    };
                    String selection = MediaStore.Audio.Media._ID + "=?";
                    String[] selectionArgs = new String[]{"" + track_id};
                    Cursor mediaCursor = contentResolver.query(mediaContentUri, trackProjection, selection, selectionArgs, null);
                    if (mediaCursor != null) {
                        if (mediaCursor.getCount() >= 0) {
                            mediaCursor.moveToPosition(0);
                            TrackInfoModel TrackInfoModel = new TrackInfoModel();
                            String path = mediaCursor.getString(0);   // Retrieve path.
                            String name = mediaCursor.getString(1);   // Retrieve name.
                            String album = mediaCursor.getString(2);  // Retrieve album name.
                            String artist = mediaCursor.getString(3); // Retrieve artist name.
                            long duration = mediaCursor.getLong(5);
                            String date = mediaCursor.getString(6);
                            int artisitId = mediaCursor.getInt(8);
                            int albumid = mediaCursor.getInt(7);
                            int trackId = mediaCursor.getInt(11);
                            long size = mediaCursor.getLong(10);
                            // Set data to the model object.
                            TrackInfoModel.setName(name);
                            TrackInfoModel.setAlbum(album);
                            TrackInfoModel.setArtist(artist);
                            TrackInfoModel.setPath(path);
                            TrackInfoModel.setTrackId(trackId);
                            TrackInfoModel.setAlbumId(albumid);
                            TrackInfoModel.setSize(size);
                            TrackInfoModel.setDuration(duration);
//                        // Add the model object to the list .
                            tracksList.add(TrackInfoModel);
                        }
                    }
                } while (cursor.moveToNext());
            }

        }
        return tracksList;
    }


    public static void setRingtone(Context context, int id) {
        ContentResolver resolver = context.getContentResolver();
        ContentResolver contentResolver = context.getContentResolver();
        // Set the flag in the database to mark this as a ringtone
        Uri ringUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
        try {
            ContentValues values = new ContentValues(2);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, "1");
            values.put(MediaStore.Audio.Media.IS_ALARM, "1");
            resolver.update(ringUri, values, null, null);
        } catch (UnsupportedOperationException ex) {
            // most likely the card just got unmounted
            Log.e(TAG, "couldn't set ringtone flag for id " + id);
            return;
        }
        String[] cols = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE
        };
        String where = MediaStore.Audio.Media._ID + "=" + id;
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                cols, where, null, null);
        try {
            if (cursor != null && cursor.getCount() == 1) {
                // Set the system setting to make this the current ringtone
                cursor.moveToFirst();
                Settings.System.putString(resolver, Settings.System.RINGTONE, ringUri.toString());
//                String message = context.getString(R.string.ringtone_set, cursor.getString(2));
                Toast.makeText(context, "Ringing Tone Set", Toast.LENGTH_SHORT).show();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

//    public static ArrayList<AudioBuckets> getAllAudioBuckets(Context context) {
//        int size;
//        ArrayList<AudioBuckets> audioBuckets = new ArrayList<>();
//        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        ContentResolver cR = context.getContentResolver();
//        String[] projection = {
//                MediaStore.Audio.Media.BUCKET_DISPLAY_NAME,
//                MediaStore.Audio.Media.BUCKET_ID,
//                MediaStore.Audio.Media.DATA
//
//        };
//        HashSet<String> bucketNameSet = new HashSet<>();
//        HashSet<String> bucketPathSet = new HashSet<>();
//        HashSet<Integer> bucketIdSet = new HashSet<>();
//        ArrayList<Integer> bucketIdArray = new ArrayList<>();
//        ArrayList<String> bucketNameArray = new ArrayList<>();
//        ArrayList<String> bucketPathArray = new ArrayList<>();
//
//        Cursor cursor = cR.query(uri, projection, null, null, null);
//        if (cursor != null) {
//            while (cursor.moveToNext()) {
//                Log.d("audiofolders", "name:" + cursor.getString(0));
//                Log.d("audiofolders", "id:" + cursor.getInt(1));
//                Log.d("audiofolders", "path:" + new File(cursor.getString(2)).getParent());
//                bucketNameSet.add(cursor.getString(0));
//                bucketIdSet.add(cursor.getInt(1));
//                bucketPathSet.add(new File(cursor.getString(2)).getParent());
//            }
//            cursor.close();
//        }
//        bucketIdArray.addAll(bucketIdSet);
//        bucketNameArray.addAll(bucketNameSet);
//        bucketPathArray.addAll(bucketPathSet);
//        for (int i = 0; i < bucketIdArray.size()-1; i++) {
//            AudioBuckets audioBuckets1 = new AudioBuckets();
//            audioBuckets1.setBucketId(bucketIdArray.get(i));
//            audioBuckets1.setBucketName(bucketNameArray.get(i));
//            audioBuckets1.setPath(bucketPathArray.get(i));
//            audioBuckets.add(audioBuckets1);
//        }
//        return audioBuckets;
//    }

    public static ArrayList<TrackInfoModel> getAllSongsFromBucket(Context context, int bucketId) {
        int numberOfTracks = 0;
        ArrayList<TrackInfoModel> tracksList = new ArrayList<>();
//        ArrayList<AudioBuckets> audioBuckets = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cR = context.getContentResolver();
        String[] projection = {
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ARTIST_ID,
                MediaStore.Audio.AudioColumns.COMPOSER,
                MediaStore.Audio.AudioColumns.SIZE,
                MediaStore.Audio.AudioColumns._ID

        };
        Cursor mediaCursor = cR.query(uri, projection, MediaStore.Audio.Media.BUCKET_ID + "=" + bucketId, null, null);
        if (mediaCursor != null) {
            while (mediaCursor.moveToNext()) {
                TrackInfoModel TrackInfoModel = new TrackInfoModel();
                String path = mediaCursor.getString(0);   // Retrieve path.
                String name = mediaCursor.getString(1);   // Retrieve name.
                String album = mediaCursor.getString(2);  // Retrieve album name.
                String artist = mediaCursor.getString(3); // Retrieve artist name.
                long duration = mediaCursor.getLong(5);
                String date = mediaCursor.getString(6);
                int artisitId = mediaCursor.getInt(8);
                int albumid = mediaCursor.getInt(7);
                int trackId = mediaCursor.getInt(11);
                long size = mediaCursor.getLong(10);
                // Set data to the model object.
                TrackInfoModel.setName(name);
                TrackInfoModel.setAlbum(album);
                TrackInfoModel.setArtist(artist);
                TrackInfoModel.setPath(path);
                TrackInfoModel.setTrackId(trackId);
                TrackInfoModel.setAlbumId(albumid);
                TrackInfoModel.setSize(size);
                TrackInfoModel.setDuration(duration);
//                        // Add the model object to the list .
                tracksList.add(TrackInfoModel);
            }
            mediaCursor.close();
        }
        numberOfTracks = tracksList.size();
        Log.d("bucketsongs", "" + numberOfTracks);
        return tracksList;

    }

    public static ArrayList<TrackInfoModel> recentlyAddedTracks(Context context) {
        // do a query for all songs added in the last X weeks
        ArrayList<TrackInfoModel> tracksList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        int X = getIntPref(context, "numweeks", 2) * (3600 * 24 * 7);
        final String[] ccols = new String[]{
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ARTIST_ID,
                MediaStore.Audio.AudioColumns.COMPOSER,
                MediaStore.Audio.AudioColumns.SIZE,
                MediaStore.Audio.AudioColumns._ID
        };
        String where = MediaStore.MediaColumns.DATE_ADDED + ">" + (System.currentTimeMillis() / 1000 - X);
        Cursor mediaCursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                ccols, where, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        try {
            if (mediaCursor != null) {
                while (mediaCursor.moveToNext()) {
                    TrackInfoModel TrackInfoModel = new TrackInfoModel();
                    String path = mediaCursor.getString(0);   // Retrieve path.
                    String name = mediaCursor.getString(1);   // Retrieve name.
                    String album = mediaCursor.getString(2);  // Retrieve album name.
                    String artist = mediaCursor.getString(3); // Retrieve artist name.
                    long duration = mediaCursor.getLong(5);
                    String date = mediaCursor.getString(6);
                    int artisitId = mediaCursor.getInt(8);
                    int albumid = mediaCursor.getInt(7);
                    int trackId = mediaCursor.getInt(11);
                    long size = mediaCursor.getLong(10);
                    // Set data to the model object.
                    TrackInfoModel.setName(name);
                    TrackInfoModel.setAlbum(album);
                    TrackInfoModel.setArtist(artist);
                    TrackInfoModel.setPath(path);
                    TrackInfoModel.setTrackId(trackId);
                    TrackInfoModel.setAlbumId(albumid);
                    TrackInfoModel.setSize(size);
                    TrackInfoModel.setDuration(duration);
//                        // Add the model object to the list .
                    tracksList.add(TrackInfoModel);
                }
            }
//            int len = mediaCursor.getCount();
//            long [] list = new long[len];
//            for (int i = 0; i < len; i++) {
//                mediaCursor.moveToNext();
//                list[i] = mediaCursor.getLong(0);
//            }
        } catch (SQLiteException ex) {
        } finally {
            mediaCursor.close();
        }
        return tracksList;
    }

    public static String getRootDirNameFromUrl(String path) {
        String[] pathArray = path.split("/");
        return pathArray[pathArray.length - 2];
    }
}