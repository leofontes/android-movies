package me.leofontes.movies.Fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.leofontes.movies.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {
    private String mOriginalTitle;
    private String mSynopsis;
    private String mUserRating;
    private String mReleaseDate;
    private String mBaseImage = "http://image.tmdb.org/t/p/w780/";
    private String mPoster;

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        TextView textViewTitle = (TextView) rootview.findViewById(R.id.textview_detail_titulo);
        TextView textViewSynopsis = (TextView) rootview.findViewById(R.id.textview_detail_synopsis);
        TextView textViewUserRating = (TextView) rootview.findViewById(R.id.textview_detail_user_rating);
        TextView textViewReleaseDate = (TextView) rootview.findViewById(R.id.textview_detail_release_date);
        ImageView imageViewPoster = (ImageView) rootview.findViewById(R.id.imageview_detail_poster);

        Intent intent = getActivity().getIntent();

        if(intent != null) {
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

        return rootview;
    }
}
