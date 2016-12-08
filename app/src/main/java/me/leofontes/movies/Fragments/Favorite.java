package me.leofontes.movies.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.leofontes.movies.Adapters.MovieAdapter;
import me.leofontes.movies.Databases.ContractDB;
import me.leofontes.movies.Interfaces.RecyclerViewOnClickListenerHack;
import me.leofontes.movies.MainActivity;
import me.leofontes.movies.Models.Movie;
import me.leofontes.movies.R;
import me.leofontes.movies.Utility;

public class Favorite extends Fragment implements RecyclerViewOnClickListenerHack, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "FAV_TAG";
    private static final String POSITION = "POSITION";
    private static final int MOVIE_LOADER = 0;

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

        if(mPosition != RecyclerView.NO_POSITION) {
            mRecyclerView.scrollToPosition(mPosition);
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mArrayList = new ArrayList<>();

        mMovieAdapter = new MovieAdapter(mArrayList);
        mMovieAdapter.setmRecyclerViewOnClickListenerHack(Favorite.this);

        mRecyclerView.setAdapter(mMovieAdapter);

        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getContext(),
                ContractDB.MovieContract.CONTENT_URI,
                ContractDB.MovieContract.COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mArrayList.clear();
        if(data.moveToFirst()) {
            Movie m;
            do {
                m = new Movie(
                        data.getString(data.getColumnIndexOrThrow(ContractDB.MovieContract._ID)),
                        data.getString(data.getColumnIndexOrThrow(ContractDB.MovieContract.COLUMN_NAME)),
                        data.getString(data.getColumnIndexOrThrow(ContractDB.MovieContract.COLUMN_IMAGE)),
                        data.getString(data.getColumnIndexOrThrow(ContractDB.MovieContract.COLUMN_SYNOPSIS)),
                        data.getDouble(data.getColumnIndexOrThrow(ContractDB.MovieContract.COLUMN_RATING)),
                        data.getString(data.getColumnIndexOrThrow(ContractDB.MovieContract.COLUMN_RELEASE_DATE))
                );
                mArrayList.add(m);
            } while (data.moveToNext());
        }

        mMovieAdapter.setList(mArrayList);

        if(mArrayList.size() > 0) {
            ((Utility.setupFirstMovie) getActivity()).setup(mArrayList.get(0), Utility.FAVORITE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.clearData();
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
