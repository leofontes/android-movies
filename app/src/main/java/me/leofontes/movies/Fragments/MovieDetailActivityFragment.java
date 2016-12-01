package me.leofontes.movies.Fragments;

import android.content.Intent;
import android.database.Cursor;
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
    private boolean isFavorite;
    private Button mFavoriteButton;

    private ReviewCatalog reviewCatalog;
    private ArrayList<Review> mArraylistReviews;
    private RecyclerView reviewRecyclerView;

    private VideoCatalog videoCatalog;
    private RecyclerView videoRecyclerView;
    private ArrayList<Video> mArraylistVideos;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        TextView textViewTitle = (TextView) rootview.findViewById(R.id.textview_detail_titulo);
        TextView textViewSynopsis = (TextView) rootview.findViewById(R.id.textview_detail_synopsis);
        TextView textViewUserRating = (TextView) rootview.findViewById(R.id.textview_detail_user_rating);
        TextView textViewReleaseDate = (TextView) rootview.findViewById(R.id.textview_detail_release_date);
        ImageView imageViewPoster = (ImageView) rootview.findViewById(R.id.imageview_detail_poster);

        Intent intent = getActivity().getIntent();

        if(intent != null) {
            movie = intent.getParcelableExtra("movie");
        }

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            movie = bundle.getParcelable("movie");
        }

        if(movie != null) {
            textViewTitle.setText(movie.original_title);
            textViewSynopsis.setText(movie.overview);
            textViewUserRating.setText(String.valueOf(movie.vote_average));
            textViewReleaseDate.setText(movie.release_date);
            Picasso.with(getActivity()).load(mBaseImage + movie.backdrop_path).into(imageViewPoster);
        }

        //movie = new Movie(mId, mOriginalTitle, mPoster, mSynopsis, Double.parseDouble(mUserRating), mReleaseDate);

        // Ensure it has Internet
        if(!intent.getBooleanExtra("favorite", false) && isOnline(getActivity()) && movie != null) {
            //Instantiate Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MovieDBService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MovieDBService service = retrofit.create(MovieDBService.class);

            //Fetch Reviews
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

                        //Manage the recycler view with the reviews
                        reviewRecyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerview_reviews);

                        ReviewAdapter reviewAdapter = new ReviewAdapter(reviewCatalog.results);
                        reviewRecyclerView.setAdapter(reviewAdapter);
                    }
                }

                @Override
                public void onFailure(Call<ReviewCatalog> call, Throwable t) {
                    Log.e(TAG, "Erro: " + t.getMessage());
                }
            });

            //Fetch Videos (Trailers)
            Call<VideoCatalog> requestVideos = service.listVideos(movie.id);
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

                    videoRecyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerview_videos);

                    VideoAdapter videoAdapter = new VideoAdapter(videoCatalog.results);
                    videoRecyclerView.setAdapter(videoAdapter);
                }

                @Override
                public void onFailure(Call<VideoCatalog> call, Throwable t) {

                }
            });
        } else if(intent.getBooleanExtra("favorite", true)) {
            Log.i(TAG, "inside favorite true");

            dbAdapter = new MovieDBAdapter(getContext());
            dbAdapter.open();

            // Fetch reviews from the database
            if(movie != null) {
                mCursor = dbAdapter.getReviews(movie.id);
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

                reviewRecyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerview_reviews);

                ReviewAdapter reviewAdapter = new ReviewAdapter(mArraylistReviews);
                reviewRecyclerView.setAdapter(reviewAdapter);

                // Fetch videos from the database
                mCursor = dbAdapter.getVideos(movie.id);
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

                videoRecyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerview_videos);

                VideoAdapter videoAdapter = new VideoAdapter(mArraylistVideos);
                videoRecyclerView.setAdapter(videoAdapter);

                dbAdapter.close();
            }
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
        }

        mFavoriteButton = (Button) rootview.findViewById(R.id.button_favorite);

        isFavorite = checkFavorite();
        changeButton();

        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAdapter = new MovieDBAdapter(getContext());
                dbAdapter.open();

                if(isFavorite) {
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

                dbAdapter.close();
            }
        });

        return rootview;
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
}
