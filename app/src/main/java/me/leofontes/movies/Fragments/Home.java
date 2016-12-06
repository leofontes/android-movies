package me.leofontes.movies.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
import me.leofontes.movies.Models.Movie;
import me.leofontes.movies.Models.MoviesCatalog;
import me.leofontes.movies.MovieDetailActivity;
import me.leofontes.movies.R;
import me.leofontes.movies.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static me.leofontes.movies.Utility.isOnline;


public class Home extends Fragment implements RecyclerViewOnClickListenerHack {

    private static final String TAG = "HOME_TAG";
    private static final String POSITION = "POSITION";

    private OnFragmentInteractionListener mListener;

    private RecyclerView mRecyclerView;
    MovieAdapter adapter;
    private MoviesCatalog catalog;
    private View rootview;

    private MovieDBService service;

    private int mPosition = RecyclerView.NO_POSITION;

    public Home() {
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
        rootview = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView = (RecyclerView) rootview.findViewById(R.id.recyclerview_home);

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
            fetchPopularMovies();
        }
    }

    private void fetchPopularMovies() {
        Call<MoviesCatalog> requestCatalogPopular = service.listCatalogPopular();

        requestCatalogPopular.enqueue(new Callback<MoviesCatalog>() {
            @Override
            public void onResponse(Call<MoviesCatalog> call, Response<MoviesCatalog> response) {
                if(!response.isSuccessful()) {
                    Log.i(TAG, "Erro: " + response.code());
                } else {
                    catalog = response.body();

                    if(MainActivity.TWO_PANES) {
                        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
                        if(catalog.results.get(0) != null) {
                            ((Utility.setupFirstMovie) getActivity()).setup(catalog.results.get(0), Utility.HOME);
                        }
                    }

                    adapter = new MovieAdapter(catalog.results);
                    adapter.setmRecyclerViewOnClickListenerHack(Home.this);
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
