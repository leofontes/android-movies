package me.leofontes.movies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import me.leofontes.movies.Models.Movie;

/**
 * Created by leo on 24/11/16.
 */

public class Utility {
    public static boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String genYoutubeUrl(String key) {
        return "https://www.youtube.com/watch?v=" + key;
    }

    public interface ClickCallback {
        public void onItemSelected(Movie m);
    }
}
