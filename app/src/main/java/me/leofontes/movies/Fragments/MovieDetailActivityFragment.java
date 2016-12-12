package me.leofontes.movies.Fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import me.leofontes.movies.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static me.leofontes.movies.Utility.isOnline;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "DETAIL_TAG";
    private static final int REVIEW_LOADER = 1;
    private static final int VIDEO_LOADER = 2;

    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;

    private Movie movie;
    private String origin = Utility.HOME;

    private String mBaseImage = "http://image.tmdb.org/t/p/w780/";

    private Cursor mCursor;
    private MovieDBAdapter dbAdapter;
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
            origin = savedInstanceState.getString("origin");
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
            origin = bundle.getString("origin");
        }

        // Check whether the current movie is a Favorite, and change the button accordingly
        isFavorite = checkFavorite();
        changeButton();

        //Populate the fields that were sent with the Bundle
        if(movie != null) {
            populateFields(movie);
        }

        configureFragment();
    }

    private void populateFields(Movie m) {
        mTextViewTitle.setText(m.original_title);
        mTextViewSynopsis.setText(m.overview);
        mTextViewUserRating.setText(String.valueOf(m.vote_average));
        mTextViewReleaseDate.setText(m.release_date);
        Picasso.with(getActivity()).load(mBaseImage + m.backdrop_path).into(mImageViewPoster);
    }

    private void configureFragment() {
        //Populate the Reviews and Trailers
        if(!origin.equals(Utility.FAVORITE) && isOnline(getActivity()) && movie != null) { //Fetch info with the API

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

        } else if(origin.equals(Utility.FAVORITE) && movie != null){ //Fetch info from the favorite list

//            //Instantiate Database Helper
//            dbAdapter = new MovieDBAdapter(getContext());

            //Fetch the videos (trailers)
            requestVideosDatabase();

            //Fetch the reviews
            requestReviewsDatabase();

        } else if(!isOnline(getActivity())) { //User is offline

            //Let the user know about Internet failure
            Toast.makeText(getContext(), getResources().getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
        }

        // Set the listener for the Favorite Button
        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAdapter = new MovieDBAdapter(getContext());

                controlFavorite(isFavorite);
            }
        });
    }

    private boolean checkFavorite() {
        dbAdapter = new MovieDBAdapter(getContext());

        mCursor = getContext().getContentResolver().query(ContractDB.MovieContract.CONTENT_URI, null, null, null, null);
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
            // Remove from the list of favorites in reverse order because of foreign keys
            String[] args = new String[] {
                String.valueOf(movie.id)
            };

            //Delete Videos
            String selection = ContractDB.VideoContract.COLUMN_MOVIE + "=?";
            getContext().getContentResolver().delete(ContractDB.VideoContract.buildVideoUri(Long.parseLong(movie.id)), selection, args);

            //Delete Reviews
            selection = ContractDB.ReviewContract.COLUMN_MOVIE + "=?";
            getContext().getContentResolver().delete(ContractDB.ReviewContract.buildReviewUri(Long.parseLong(movie.id)), selection, args);

            //Delete Movies
            selection = ContractDB.MovieContract._ID + "=?";
            getContext().getContentResolver().delete(ContractDB.MovieContract.CONTENT_URI, selection, args);

            Toast.makeText(getContext(), getResources().getString(R.string.toast_removed_favorite), Toast.LENGTH_SHORT).show();
            //Update the button
            isFavorite = !isFavorite;
            changeButton();


        } else {
            // Add to the favorite list
            // Insert the movie
            getContext().getContentResolver().insert(ContractDB.MovieContract.CONTENT_URI, genCValuesMovie());
            // Insert all the reviews
            getContext().getContentResolver().bulkInsert(ContractDB.ReviewContract.buildReviewUri(Long.parseLong(movie.id)), genCValuesArrReview());
            // Insert all the videos
            getContext().getContentResolver().bulkInsert(ContractDB.VideoContract.buildVideoUri(Long.parseLong(movie.id)), genCValuesArrVideo());

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

    private void requestReviewsDatabase() {
//        long id = Long.parseLong(movieId);
//        mCursor = getContext().getContentResolver().query(ContractDB.ReviewContract.buildReviewUri(id), null, null, null, null);

        mArraylistReviews = new ArrayList<>();
        mReviewAdapter = new ReviewAdapter(mArraylistReviews);
        reviewRecyclerView.setAdapter(mReviewAdapter);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
    }

    private void requestVideosDatabase() {
//        long id = Long.parseLong(movieId);
//        mCursor = getContext().getContentResolver().query(ContractDB.VideoContract.buildVideoUri(id), null, null, null, null);
//
//        mArraylistVideos = new ArrayList<>();
//
//        if(mCursor.moveToFirst()) {
//            do {
//                Video v = new Video(
//                        mCursor.getString(mCursor.getColumnIndexOrThrow(ContractDB.VideoContract.COLUMN_KEY)),
//                        mCursor.getString(mCursor.getColumnIndexOrThrow(ContractDB.VideoContract.COLUMN_NAME))
//                );
//
//                mArraylistVideos.add(v);
//            } while (mCursor.moveToNext());
//        }
//
//        VideoAdapter videoAdapter = new VideoAdapter(mArraylistVideos);
//        videoRecyclerView.setAdapter(videoAdapter);
        mArraylistVideos = new ArrayList<>();
        mVideoAdapter = new VideoAdapter(mArraylistVideos);
        videoRecyclerView.setAdapter(mVideoAdapter);
        getLoaderManager().initLoader(VIDEO_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(movie != null) {
            outState.putParcelable("movie", movie);
            outState.putString("origin", origin);
        }

        super.onSaveInstanceState(outState);
    }

    public void setupExternal(Movie m, String listOrigin) {
        if(movie == null || !listOrigin.equals(origin)) {
            movie = m;
            origin = listOrigin;

            if(movie != null) {
                populateFields(movie);
            }

            configureFragment();
        }
    }

    public ContentValues genCValuesMovie() {
        ContentValues cvalues = new ContentValues();
        // Assign the data
        cvalues.put(ContractDB.MovieContract._ID, movie.id);
        cvalues.put(ContractDB.MovieContract.COLUMN_NAME, movie.original_title);
        cvalues.put(ContractDB.MovieContract.COLUMN_IMAGE, movie.backdrop_path);
        cvalues.put(ContractDB.MovieContract.COLUMN_RATING, movie.vote_average);
        cvalues.put(ContractDB.MovieContract.COLUMN_RELEASE_DATE, movie.release_date);
        cvalues.put(ContractDB.MovieContract.COLUMN_SYNOPSIS, movie.overview);

        return cvalues;
    }

    public ContentValues[] genCValuesArrReview() {
        if(reviewCatalog.results != null) {
            ContentValues[] cvaluesArr = new ContentValues[reviewCatalog.results.size()];
            int current = 0;

            for(Review review : reviewCatalog.results) {
                ContentValues cvalues = new ContentValues();
                //Assign the data
                cvalues.put(ContractDB.ReviewContract.COLUMN_AUTHOR, review.author);
                cvalues.put(ContractDB.ReviewContract.COLUMN_CONTENT, review.content);
                cvalues.put(ContractDB.ReviewContract.COLUMN_MOVIE, movie.id);
                //Store on array and increase current
                cvaluesArr[current] = cvalues;
                current++;
            }

            return cvaluesArr;
        } else {
            return null;
        }
    }

    public ContentValues[] genCValuesArrVideo() {
        if(videoCatalog.results != null) {
            ContentValues[] cvaluesArr = new ContentValues[videoCatalog.results.size()];
            int current = 0;

            for (Video video : videoCatalog.results) {
                // New row of values to insert
                ContentValues cvalues = new ContentValues();
                //Assign the data
                cvalues.put(ContractDB.VideoContract.COLUMN_KEY, video.key);
                cvalues.put(ContractDB.VideoContract.COLUMN_NAME, video.name);
                cvalues.put(ContractDB.VideoContract.COLUMN_MOVIE, movie.id);
                //Store on array and increase current
                cvaluesArr[current] = cvalues;
                current++;
            }

            return cvaluesArr;
        } else {
            return null;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case REVIEW_LOADER:
                return new CursorLoader(
                        getContext(),
                        ContractDB.ReviewContract.buildReviewUri(Long.parseLong(movie.id)),
                        ContractDB.ReviewContract.COLUMNS,
                        null,
                        null,
                        null
                );
            case VIDEO_LOADER:
                return new CursorLoader(
                        getContext(),
                        ContractDB.VideoContract.buildVideoUri(Long.parseLong(movie.id)),
                        ContractDB.VideoContract.COLUMNS,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == REVIEW_LOADER) {
            mArraylistReviews.clear();

            if(data.moveToFirst()) {
                do {
                    Review r = new Review(
                            data.getString(data.getColumnIndexOrThrow(ContractDB.ReviewContract.COLUMN_AUTHOR)),
                            data.getString(data.getColumnIndexOrThrow(ContractDB.ReviewContract.COLUMN_CONTENT))
                    );

                    mArraylistReviews.add(r);
                } while(mCursor.moveToNext());
            }

            mReviewAdapter.setList(mArraylistReviews);


        } else if(loader.getId() == VIDEO_LOADER) {
            mArraylistVideos.clear();

            if(data.moveToFirst()) {
                do {
                    Video v = new Video(
                            data.getString(data.getColumnIndexOrThrow(ContractDB.VideoContract.COLUMN_KEY)),
                            data.getString(data.getColumnIndexOrThrow(ContractDB.VideoContract.COLUMN_NAME))
                    );

                    mArraylistVideos.add(v);
                } while (data.moveToNext());
            }

            mVideoAdapter.setList(mArraylistVideos);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id = loader.getId();

        switch (id) {
            case REVIEW_LOADER:
                mReviewAdapter.clearData();
                break;
            case VIDEO_LOADER:
                mVideoAdapter.clearData();
                break;
            default:

        }
    }
}
