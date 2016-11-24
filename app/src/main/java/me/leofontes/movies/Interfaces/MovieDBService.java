package me.leofontes.movies.Interfaces;

import me.leofontes.movies.Models.MoviesCatalog;
import me.leofontes.movies.Models.VideoCatalog;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by leo on 24/10/16.
 */

public interface MovieDBService {
    public static final String BASE_URL = "https://api.themoviedb.org/3/";

    @GET("discover/movie?api_key=ac6ebfcd9300aeee710aef10fe23e547&sort_by=popularity.desc")
    Call<MoviesCatalog> listCatalogPopular();

    @GET("discover/movie?api_key=ac6ebfcd9300aeee710aef10fe23e547&sort_by=vote_average.desc")
    Call<MoviesCatalog> listCatalogHighRated();

    @GET("movie/{id}/videos?api_key=ac6ebfcd9300aeee710aef10fe23e547")
    Call<VideoCatalog> listVideos(@Path("id") String movieId);
}
