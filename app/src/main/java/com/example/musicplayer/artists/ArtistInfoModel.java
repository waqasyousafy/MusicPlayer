package com.example.musicplayer.artists;

public class ArtistInfoModel {
    private int artistId;
    private String artistName;
    private int numberOfAlbums;
    private int numberofTracks;

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getNumberOfAlbums() {
        return numberOfAlbums;
    }

    public void setNumberOfAlbums(int numberOfAlbums) {
        this.numberOfAlbums = numberOfAlbums;
    }

    public int getNumberofTracks() {
        return numberofTracks;
    }

    public void setNumberofTracks(int numberofTracks) {
        this.numberofTracks = numberofTracks;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }
}
