package me.leofontes.movies.Models;

/**
 * Created by leo on 24/11/16.
 */

public class Review {
    public String id;
    public String author;
    public String content;
    public String url;

    public Review(){};

    public Review(String author, String content) {
        this.author = author;
        this.content = content;
    }
}
