package me.leofontes.movies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import me.leofontes.movies.Fragments.Favorite;
import me.leofontes.movies.Fragments.HighRated;
import me.leofontes.movies.Fragments.Home;
import me.leofontes.movies.Fragments.MovieDetailActivityFragment;
import me.leofontes.movies.Models.Movie;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HighRated.OnFragmentInteractionListener, Home.OnFragmentInteractionListener, Favorite.OnFragmentInteractionListener,
        Utility.ClickCallback, Utility.setupFirstMovie {

    public static final String DETAIL_FRAG_TAG = "DETAIL_FRAG_TAG";

    private FragmentManager fragmentManager;
    private Fragment fragment;
    private View detailContainerView;

    public static boolean TWO_PANES;
    public static String ORIGIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        detailContainerView = (View) findViewById(R.id.detail_container);

        fragmentManager = getSupportFragmentManager();

        if(detailContainerView != null) {
            TWO_PANES = true;

            if(fragmentManager.findFragmentById(R.id.detail_container) != null) {
                fragmentManager.beginTransaction().replace(R.id.detail_container, fragmentManager.findFragmentById(R.id.detail_container), DETAIL_FRAG_TAG).commit();
            } else {
                fragmentManager.beginTransaction().replace(R.id.detail_container, new MovieDetailActivityFragment(), DETAIL_FRAG_TAG).commit();
            }
        } else {
            TWO_PANES = false;
        }

        if(savedInstanceState == null) {
            fragment = new Home();
            fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
        }

        Log.i("MAINACTIVITY", "inside mainactivity");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_popular) {
            fragment = new Home();
            ORIGIN = Utility.HOME;
            fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
        } else if (id == R.id.nav_rating) {
            fragment = new HighRated();
            ORIGIN = Utility.HIGHRATED;
            fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_favorite) {
            fragment = new Favorite();
            ORIGIN = Utility.FAVORITE;
            fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onItemSelected(Movie m) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("movie", m);
        bundle.putString("origin", ORIGIN);

        if(TWO_PANES) {
            detailContainerView.setVisibility(View.VISIBLE);
            fragment = new MovieDetailActivityFragment();

            fragment.setArguments(bundle);

            fragmentManager.beginTransaction().replace(R.id.detail_container, fragment, DETAIL_FRAG_TAG).commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        }
    }

    @Override
    public void setup(Movie m, String listOrigin) {
        MovieDetailActivityFragment movieDetailActivityFragment = (MovieDetailActivityFragment) fragmentManager.findFragmentByTag(DETAIL_FRAG_TAG);
        movieDetailActivityFragment.setupExternal(m, listOrigin);
    }
}
