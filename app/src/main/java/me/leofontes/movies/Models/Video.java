package me.leofontes.movies.Models;

/**
 * Created by leo on 23/11/16.
 */

public class Video {
    public String id;
    public String key;
    public String name;
    public String site;
    public String type;

    public Video() {};

    public Video(String key, String name) {
        this.key = key;
        this.name = name;
    }
}
