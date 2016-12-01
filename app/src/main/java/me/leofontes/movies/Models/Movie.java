package me.leofontes.movies.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by leo on 24/10/16.
 */

public class Movie  implements Parcelable{
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

    protected Movie(Parcel in) {
        id = in.readString();
        original_title = in.readString();
        backdrop_path = in.readString();
        overview = in.readString();
        vote_average = in.readDouble();
        release_date = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(original_title);
        dest.writeString(backdrop_path);
        dest.writeString(overview);
        dest.writeDouble(vote_average);
        dest.writeString(release_date);
    }
}
