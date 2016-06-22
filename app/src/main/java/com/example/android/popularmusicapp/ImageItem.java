package com.example.android.popularmusicapp;

import android.graphics.Bitmap;

public class ImageItem {
    private Bitmap image;
    private String title;
    private String popularity;
    private String voteAve;
    private String synopsis;
    private String releaseDate;


    public ImageItem(Bitmap image, String synopsis, String voteAve, String popularity, String title, String releaseDate) {
        this.image = image;
        this.synopsis = synopsis;
        this.voteAve = voteAve;
        this.popularity = popularity;
        this.title = title;
        this.releaseDate = releaseDate;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getVoteAve() {
        return voteAve;
    }

    public void setVoteAve(String voteAve) {
        this.voteAve = voteAve;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}