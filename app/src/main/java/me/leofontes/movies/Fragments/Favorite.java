package me.leofontes.movies.Fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
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
import me.leofontes.movies.MainActivity;
import me.leofontes.movies.Models.Movie;
import me.leofontes.movies.MovieDetailActivity;
import me.leofontes.movies.R;
import me.leofontes.movies.Utility;

public class Favorite extends Fragment implements RecyclerViewOnClickListenerHack {
    private static final String TAG = "FAV_TAG";
    private static final String POSITION = "POSITION";

    private MovieDBAdapter dbAdapter;
    private List<Movie> mList;
    private ArrayList<Movie> mArrayList;
    private MovieAdapter mMovieAdapter;

    private RecyclerView mRecyclerView;
    private Cursor mCursor;

    private int mPosition = RecyclerView.NO_POSITION;
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

        if(MainActivity.TWO_PANES) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        }

        if(savedInstanceState != null && savedInstanceState.containsKey(POSITION)) {
            mPosition = savedInstanceState.getInt(POSITION);
        }

        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();

        mArrayList = new ArrayList<>();

        dbAdapter = new MovieDBAdapter(getContext());
        dbAdapter.open();

        mMovieAdapter = new MovieAdapter(mArrayList);
        mMovieAdapter.setmRecyclerViewOnClickListenerHack(Favorite.this);
        updateList();

        if(mArrayList.get(0) != null) {
            ((Utility.setupFirstMovie) getActivity()).setup(mArrayList.get(0), Utility.FAVORITE);
        }

        mRecyclerView.setAdapter(mMovieAdapter);
        dbAdapter.close();

        if(mPosition != RecyclerView.NO_POSITION) {
            mRecyclerView.scrollToPosition(mPosition);
        }
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
        mPosition = position;

        ((Utility.ClickCallback) getActivity())
                .onItemSelected(mArrayList.get(position));
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
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(POSITION, mPosition);
        }

        super.onSaveInstanceState(outState);
    }
}
