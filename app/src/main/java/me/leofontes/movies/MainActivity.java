package me.leofontes.movies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        Utility.ClickCallback {

    private FragmentManager fragmentManager;
    private Fragment fragment;
    private View detailContainerView;

    public static boolean TWO_PANES;

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
            detailContainerView.setVisibility(View.INVISIBLE);
            fragmentManager.beginTransaction().replace(R.id.detail_container, new MovieDetailActivityFragment()).commit();
        } else {
            TWO_PANES = false;
        }

        fragment = new Home();
        fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_popular) {
            fragment = new Home();
            detailContainerView.setVisibility(View.VISIBLE);
            fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
        } else if (id == R.id.nav_rating) {
            fragment = new HighRated();
            detailContainerView.setVisibility(View.VISIBLE);
            fragmentManager.beginTransaction().replace(R.id.content_main, fragment).commit();
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_favorite) {
            fragment = new Favorite();
            detailContainerView.setVisibility(View.VISIBLE);
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
        if(TWO_PANES) {
            detailContainerView.setVisibility(View.VISIBLE);
            fragment = new MovieDetailActivityFragment();

            Bundle bundle = new Bundle();
            bundle.putParcelable("movie", m);

            fragment.setArguments(bundle);

            fragmentManager.beginTransaction().replace(R.id.detail_container, fragment).commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra("movie", m);
            intent.putExtra("favorite", false);
            startActivity(intent);
        }
    }
}
