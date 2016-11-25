package me.leofontes.movies.Fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import me.leofontes.movies.Adapters.ReviewAdapter;
import me.leofontes.movies.Adapters.VideoAdapter;
import me.leofontes.movies.Interfaces.MovieDBService;
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

    private String mId;
    private String mOriginalTitle;
    private String mSynopsis;
    private String mUserRating;
    private String mReleaseDate;
    private String mBaseImage = "http://image.tmdb.org/t/p/w780/";
    private String mPoster;

    private ReviewCatalog reviewCatalog;
    private RecyclerView reviewRecyclerView;

    private VideoCatalog videoCatalog;
    private RecyclerView videoRecyclerView;

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
            mId = intent.getStringExtra("id");
            mOriginalTitle = intent.getStringExtra("originaltitle");
            textViewTitle.setText(mOriginalTitle);
            mSynopsis = intent.getStringExtra("synopsis");
            textViewSynopsis.setText(mSynopsis);
            mUserRating = intent.getStringExtra("userrating");
            textViewUserRating.setText(mUserRating);
            mReleaseDate = intent.getStringExtra("releasedate");
            textViewReleaseDate.setText(mReleaseDate);
            mPoster = intent.getStringExtra("poster");
            Picasso.with(getActivity()).load(mBaseImage + mPoster).into(imageViewPoster);
        }

        // Ensure it has Internet
        if(isOnline(getActivity())) {
            //Instantiate Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MovieDBService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MovieDBService service = retrofit.create(MovieDBService.class);

            //Fetch Reviews
            Call<ReviewCatalog> requestReviews = service.listReviews(mId);
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
            Call<VideoCatalog> requestVideos = service.listVideos(mId);
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
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.error_no_internet), Toast.LENGTH_SHORT).show();
        }

        return rootview;
    }
}
