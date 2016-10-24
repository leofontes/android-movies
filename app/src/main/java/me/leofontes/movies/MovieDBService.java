package me.leofontes.movies;

import me.leofontes.movies.Models.MoviesCatalog;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by leo on 24/10/16.
 */

public interface MovieDBService {
    public static final String BASE_URL = "https://api.themoviedb.org/3/discover/";

    @GET("movie?api_key=ac6ebfcd9300aeee710aef10fe23e547&sort_by=popularity.desc")
    Call<MoviesCatalog> listCatalogPopular();
}
