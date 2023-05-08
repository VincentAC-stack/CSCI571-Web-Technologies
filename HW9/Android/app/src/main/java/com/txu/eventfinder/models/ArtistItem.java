package com.txu.eventfinder.models;

import android.widget.ProgressBar;

public class ArtistItem {
    private String ArtistImageUrl;
    private String ArtistName;
    private String ArtistFollower;
    private String ArtistSpotifyUrl;
    private String ArtistPopularity;
    private String ArtistAlbumUrl1;
    private String ArtistAlbumUrl2;
    private String ArtistAlbumUrl3;

    private int Progress;

    public ArtistItem(String artistImageUrl, String artistName, String artistFollower, String artistAlbumUrl1,
                      String artistAlbumUrl2, String artistAlbumUrl3, String artistSpotifyUrl, String artistPopularity,
                      int progress) {
        ArtistImageUrl = artistImageUrl;
        ArtistName = artistName;
        ArtistFollower = artistFollower;
        ArtistAlbumUrl1 = artistAlbumUrl1;
        ArtistAlbumUrl2 = artistAlbumUrl2;
        ArtistAlbumUrl3 = artistAlbumUrl3;
        ArtistSpotifyUrl = artistSpotifyUrl;
        ArtistPopularity = artistPopularity;
        Progress = progress;
    }

    public String getArtistImageUrl() {
        return ArtistImageUrl;
    }

    public String getArtistName() {
        return ArtistName;
    }

    public String getArtistFollower() {
        return ArtistFollower;
    }

    public String getArtistSpotifyUrl() {
        return ArtistSpotifyUrl;
    }

    public String getArtistPopularity() {
        return ArtistPopularity;
    }

    public String getArtistAlbumUrl1() {
        return ArtistAlbumUrl1;
    }

    public String getArtistAlbumUrl2() {
        return ArtistAlbumUrl2;
    }

    public String getArtistAlbumUrl3() {
        return ArtistAlbumUrl3;
    }

    public int getProgress() {
        return Progress;
    }
}
