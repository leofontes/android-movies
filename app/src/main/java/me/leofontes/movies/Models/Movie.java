package me.leofontes.movies.Models;

/**
 * Created by leo on 24/10/16.
 */

public class Movie {
    public String id;
    public String original_title;
    public String backdrop_path;
    public String overview;
    public double vote_average;
    public String release_date;

    public Movie(){};

    public Movie(String id, String original_title, String backdrop_path, String overview, double vote_average, String release_date) {
        this.id = id;
        this.original_title = original_title;
        this.backdrop_path = backdrop_path;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }
}
