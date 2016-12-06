package me.leofontes.movies.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.leofontes.movies.Adapters.MovieAdapter;
import me.leofontes.movies.Interfaces.MovieDBService;
import me.leofontes.movies.Interfaces.RecyclerViewOnClickListenerHack;
import me.leofontes.movies.MainActivity;
import me.leofontes.movies.Models.MoviesCatalog;
import me.leofontes.movies.R;
import me.leofontes.movies.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static me.leofontes.movies.Utility.isOnline;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Upcoming.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Upcoming extends Fragment implements RecyclerViewOnClickListenerHack {
    private static final String TAG = "UPCOMING_TAG";
    private static final String POSITION = "POSITION";

    private OnFragmentInteractionListener mListener;
    private MovieDBService service;
    private MovieAdapter adapter;
    private MoviesCatalog catalog;

    private int mPosition = RecyclerView.NO_POSITION;

    //Widgets
    RecyclerView mRecyclerView;

    public Upcoming() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_upcoming, container, false);

        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerview_upcoming);

        if(savedInstanceState != null && savedInstanceState.containsKey(POSITION)) {
            mPosition = savedInstanceState.getInt(POSITION);
        }

        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(isOnline(getActivity())) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MovieDBService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service = retrofit.create(MovieDBService.class);
            fetchUpcomingMovies();
        }
    }

    private void fetchUpcomingMovies() {
        Call<MoviesCatalog> requestCatalogUpcoming = service.listCatalogUpcoming();

        requestCatalogUpcoming.enqueue(new Callback<MoviesCatalog>() {
            @Override
            public void onResponse(Call<MoviesCatalog> call, Response<MoviesCatalog> response) {
                if(!response.isSuccessful()) {
                    Log.i(TAG, "Erro: " + response.code());
                } else {
                    catalog = response.body();

                    if(MainActivity.TWO_PANES) {
                        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
                        if(catalog.results.get(0) != null) {
                            ((Utility.setupFirstMovie) getActivity()).setup(catalog.results.get(0), Utility.UPCOMING);
                        }
                    }

                    adapter = new MovieAdapter(catalog.results);
                    adapter.setmRecyclerViewOnClickListenerHack(Upcoming.this);
                    mRecyclerView.setAdapter(adapter);

                    if(mPosition != RecyclerView.NO_POSITION) {
                        mRecyclerView.scrollToPosition(mPosition);
                    }
                }
            }

            @Override
            public void onFailure(Call<MoviesCatalog> call, Throwable t) {
                Log.e(TAG, "Erro: " + t.getMessage());
            }
        });
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
                .onItemSelected(catalog.results.get(position));
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != RecyclerView.NO_POSITION) {
            outState.putInt(POSITION, mPosition);
        }

        super.onSaveInstanceState(outState);
    }
}
