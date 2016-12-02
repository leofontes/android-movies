package me.leofontes.movies.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import me.leofontes.movies.Adapters.ReviewAdapter;
import me.leofontes.movies.Adapters.VideoAdapter;
import me.leofontes.movies.Databases.ContractDB;
import me.leofontes.movies.Databases.MovieDBAdapter;
import me.leofontes.movies.Interfaces.MovieDBService;
import me.leofontes.movies.Models.Movie;
import me.leofontes.movies.Models.Review;
import me.leofontes.movies.Models.ReviewCatalog;
import me.leofontes.movies.Models.Video;
import me.leofontes.movies.Models.VideoCatalog;
import me.leofontes.movies.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static me.leofontes.movies.Utility.isOnline;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {
    private static final String TAG = "DETAIL_TAG";

    private Movie movie;

    private String mBaseImage = "http://image.tmdb.org/t/p/w780/";

    private Cursor mCursor;
    private MovieDBAdapter dbAdapter;
    private boolean fromFavoriteList;
    private boolean isFavorite;
    private Button mFavoriteButton;

    private ReviewCatalog reviewCatalog;
    private ArrayList<Review> mArraylistReviews;
    private RecyclerView reviewRecyclerView;

    private VideoCatalog videoCatalog;
    private RecyclerView videoRecyclerView;
    private ArrayList<Video> mArraylistVideos;

    private TextView mTextViewTitle;
    private TextView mTextViewSynopsis;
    private TextView mTextViewUserRating;
    private TextView mTextViewReleaseDate;
    private ImageView mImageViewPoster;

    private MovieDBService service;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        mTextViewTitle = (TextView) rootview.findViewById(R.id.textview_detail_titulo);
        mTextViewSynopsis = (TextView) rootview.findViewById(R.id.textview_detail_synopsis);
        mTextViewUserRating = (TextView) rootview.findViewById(R.id.textview_detail_user_rating);
        mTextViewReleaseDate = (TextView) rootview.findViewById(R.id.textview_detail_release_date);
        mImageViewPoster = (ImageView) rootview.findViewById(R.id.imageview_detail_poster);

        videoRecyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerview_videos);
        reviewRecyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerview_reviews);

        mFavoriteButton = (Button) rootview.findViewById(R.id.button_favorite);

        if(savedInstanceState != null && savedInstanceState.containsKey("movie")) {
            movie = savedInstanceState.getParcelable("movie");
            fromFavoriteList = savedInstanceState.getBoolean("favorite");
        }

        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();

        //  Read from the bundle (whether from Intent or within Fragment)
        //instantiate Movie and know wheter to fetch other data from Retrofit or Database
        Bundle bundle;
        Intent intent = getActivity().getIntent();
        if(intent != null && intent.getBundleExtra("bundle") != null) {
            bundle = intent.getBundleExtra("bundle");
        } else {
            bundle = this.getArguments();
        }

        if(movie == null && bundle != null) {
            movie = bundle.getParcelable("movie");
            fromFavoriteList = bundle.getBoolean("favorite");
        }

        // Check whether the current movie is a Favorite, and change the button accordingly
        isFavorite = checkFavorite();
        changeButton();

        //Populate the fields that were sent with the Bundle
        if(movie != null) {
            populateFields(movie);
        }

        //Populate the Reviews and Trailers
        if(!fromFavoriteList && isOnline(getActivity()) && movie != null) {//Fetch info with the API

            //Instantiate Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MovieDBService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(MovieDBService.class);

            //Fetch the videos (trailers)
            requestVideosRetrofit(movie.id);

            //Fetch the reviews
            requestReviewsRetrofit(movie.id);

        } else if(fromFavoriteList && movie != null){ //Fetch info from the favorite list

            //Instantiate Database Helper
            dbAdapter = new MovieDBAdapter(getContext());
            dbAdapter.open();

            //Fetch the videos (trailers)
            requestVideosDatabase(movie.id);

            //Fetch the reviews
            requestReviewsDatabase(movie.id);

            dbAdapter.close();
        } else if(!isOnline(getActivity())) { //User is offline

            //Let the user know about Internet failure
            Toast.makeText(getContext(), getResources().getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
        }

        // Set the listener for the Favorite Button
        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAdapter = new MovieDBAdapter(getContext());
                dbAdapter.open();

                controlFavorite(isFavorite);

                dbAdapter.close();
            }
        });
    }

    private void populateFields(Movie m) {
        mTextViewTitle.setText(m.original_title);
        mTextViewSynopsis.setText(m.overview);
        mTextViewUserRating.setText(String.valueOf(m.vote_average));
        mTextViewReleaseDate.setText(m.release_date);
        Picasso.with(getActivity()).load(mBaseImage + m.backdrop_path).into(mImageViewPoster);
    }

    private boolean checkFavorite() {
        dbAdapter = new MovieDBAdapter(getContext());
        dbAdapter.open();

        mCursor = dbAdapter.getAllMovies();
        String favId;

        if(mCursor.moveToFirst() && movie != null) {
            do {
                favId = mCursor.getString(mCursor.getColumnIndexOrThrow(ContractDB.MovieContract._ID));

                //Found the movie in the favorites list
                if(favId.equals(movie.id)) {
                    return true;
                }
            } while (mCursor.moveToNext());
        }

        dbAdapter.close();

        return false;
    }

    private void changeButton() {
        if(isFavorite) {
            mFavoriteButton.setBackgroundColor(getResources().getColor(R.color.white));
            mFavoriteButton.setText(R.string.detail_button_remove_favorite);
            mFavoriteButton.setTextColor(getResources().getColor(R.color.defaultRed));
        } else {
            mFavoriteButton.setBackgroundColor(getResources().getColor(R.color.defaultRed));
            mFavoriteButton.setText(R.string.detail_button_add_favorite);
            mFavoriteButton.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void controlFavorite(boolean favoriteStatus) {
        if(favoriteStatus) {
            // Remove from the list of favorites
            boolean wasRemoved = dbAdapter.removeFavorite(movie.id);
            if(wasRemoved) {
                Toast.makeText(getContext(), getResources().getString(R.string.toast_removed_favorite), Toast.LENGTH_SHORT).show();
                //Update the button
                isFavorite = !isFavorite;
                changeButton();
            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.error_removed_favorite), Toast.LENGTH_SHORT).show();
            }

        } else {
            // Add to the favorite list
            // Insert the movie
            dbAdapter.insertMovie(movie);
            // Insert all the reviews
            for(int i = 0; i < reviewCatalog.results.size(); i++) {
                dbAdapter.insertReview(reviewCatalog.results.get(i), Integer.parseInt(movie.id));
            }
            // Insert all the videos
            for(int i = 0; i < videoCatalog.results.size(); i++) {
                dbAdapter.insertVideo(videoCatalog.results.get(i), Integer.parseInt(movie.id));
            }

            Toast.makeText(getContext(), getResources().getString(R.string.toast_added_favorite), Toast.LENGTH_SHORT).show();

            //Update the button
            isFavorite = !isFavorite;
            changeButton();
        }
    }

    private void requestVideosRetrofit(String movieId) {
        Call<VideoCatalog> requestVideos = service.listVideos(movieId);
        requestVideos.enqueue(new Callback<VideoCatalog>() {
            @Override
            public void onResponse(Call<VideoCatalog> call, Response<VideoCatalog> response) {
                videoCatalog = response.body();

//                    for(Video v : videoCatalog.results) {
//                        Log.i(TAG, "Site: " + v.site);
//                        Log.i(TAG, "Name: " + v.name);
//                        Log.i(TAG, "Key: " + v.key);
//                        Log.i(TAG, "Type: " + v.type);
//                    }

                VideoAdapter videoAdapter = new VideoAdapter(videoCatalog.results);
                videoRecyclerView.setAdapter(videoAdapter);
            }

            @Override
            public void onFailure(Call<VideoCatalog> call, Throwable t) {

            }
        });
    }

    private void requestReviewsRetrofit(String movieId) {
        Call<ReviewCatalog> requestReviews = service.listReviews(movie.id);
        requestReviews.enqueue(new Callback<ReviewCatalog>() {
            @Override
            public void onResponse(Call<ReviewCatalog> call, Response<ReviewCatalog> response) {
                if(!response.isSuccessful()) {
                    Log.i(TAG, "Erro: " + response.code());
                } else {
                    reviewCatalog = response.body();

//                        for(Review r : reviewCatalog.results) {
//                            Log.i(TAG, r.author);
//                            Log.i(TAG, r.content);
//                        }

                    ReviewAdapter reviewAdapter = new ReviewAdapter(reviewCatalog.results);
                    reviewRecyclerView.setAdapter(reviewAdapter);
                }
            }

            @Override
            public void onFailure(Call<ReviewCatalog> call, Throwable t) {
                Log.e(TAG, "Erro: " + t.getMessage());
            }
        });
    }

    private void requestReviewsDatabase(String movieId) {
        mCursor = dbAdapter.getReviews(movieId);
        Review r;
        mArraylistReviews = new ArrayList<>();

        if(mCursor.moveToFirst()) {
            do {
                r = new Review(
                        mCursor.getString(mCursor.getColumnIndexOrThrow(ContractDB.ReviewContract.COLUMN_AUTHOR)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(ContractDB.ReviewContract.COLUMN_CONTENT))
                );

                mArraylistReviews.add(r);
            } while(mCursor.moveToNext());
        }

        ReviewAdapter reviewAdapter = new ReviewAdapter(mArraylistReviews);
        reviewRecyclerView.setAdapter(reviewAdapter);
    }

    private void requestVideosDatabase(String movieId) {
        mCursor = dbAdapter.getVideos(movieId);
        Video v;
        mArraylistVideos = new ArrayList<>();

        if(mCursor.moveToFirst()) {
            do {
                v = new Video(
                        mCursor.getString(mCursor.getColumnIndexOrThrow(ContractDB.VideoContract.COLUMN_KEY)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(ContractDB.VideoContract.COLUMN_NAME))
                );

                mArraylistVideos.add(v);
            } while (mCursor.moveToNext());
        }

        VideoAdapter videoAdapter = new VideoAdapter(mArraylistVideos);
        videoRecyclerView.setAdapter(videoAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(movie != null) {
            outState.putParcelable("movie", movie);
            outState.putBoolean("favorite", fromFavoriteList);

            Log.i(TAG, "inside on Save instance");
            Log.i(TAG, "onSaveinstance state movie: " + movie.original_title);
        }
        super.onSaveInstanceState(outState);
    }
}
