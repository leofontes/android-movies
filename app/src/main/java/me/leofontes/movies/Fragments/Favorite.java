package me.leofontes.movies.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.leofontes.movies.Adapters.MovieAdapter;
import me.leofontes.movies.Databases.ContractDB;
import me.leofontes.movies.Databases.MovieDBAdapter;
import me.leofontes.movies.Interfaces.RecyclerViewOnClickListenerHack;
import me.leofontes.movies.Models.Movie;
import me.leofontes.movies.MovieDetailActivity;
import me.leofontes.movies.R;

public class Favorite extends Fragment implements RecyclerViewOnClickListenerHack {
    private static final String TAG = "FAV_TAG";

    private MovieDBAdapter dbAdapter;
    private List<Movie> mList;
    private ArrayList<Movie> mArrayList;
    private MovieAdapter mMovieAdapter;

    private RecyclerView mRecyclerView;
    private Cursor mCursor;

    private OnFragmentInteractionListener mListener;

    public Favorite() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_favorite, container, false);

        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerview_favorite);
        mArrayList = new ArrayList<>();

        dbAdapter = new MovieDBAdapter(getContext());
        dbAdapter.open();

        mMovieAdapter = new MovieAdapter(mArrayList);
        mMovieAdapter.setmRecyclerViewOnClickListenerHack(Favorite.this);
        updateList();

        mRecyclerView.setAdapter(mMovieAdapter);

        dbAdapter.close();

        return rootview;
    }

    public void updateList() {
        mCursor = dbAdapter.getAllMovies();
        mArrayList.clear();

        Movie m;

        if(mCursor.moveToFirst()) {
            do {
                m = new Movie(
                        mCursor.getString(mCursor.getColumnIndexOrThrow(ContractDB.MovieContract._ID)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(ContractDB.MovieContract.COLUMN_NAME)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(ContractDB.MovieContract.COLUMN_IMAGE)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(ContractDB.MovieContract.COLUMN_SYNOPSIS)),
                        mCursor.getDouble(mCursor.getColumnIndexOrThrow(ContractDB.MovieContract.COLUMN_RATING)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(ContractDB.MovieContract.COLUMN_RELEASE_DATE))
                );
                mArrayList.add(m);
            } while (mCursor.moveToNext());
        }

        mMovieAdapter.notifyDataSetChanged();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void OnClickListener(View view, int position) {
        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);

        intent.putExtra("id", mArrayList.get(position).id);
        intent.putExtra("originaltitle", mArrayList.get(position).original_title);
        intent.putExtra("synopsis", mArrayList.get(position).overview);
        intent.putExtra("userrating", "" + mArrayList.get(position).vote_average);
        intent.putExtra("releasedate", mArrayList.get(position).release_date);
        intent.putExtra("poster", mArrayList.get(position).backdrop_path);

        intent.putExtra("favorite", true);

        startActivity(intent);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
