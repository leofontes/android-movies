package me.leofontes.movies.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.leofontes.movies.Interfaces.RecyclerViewOnClickListenerHack;
import me.leofontes.movies.Models.Movie;
import me.leofontes.movies.R;

/**
 * Created by leo on 26/10/16.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {
    private List<Movie> mList;
    private String baseImage = "http://image.tmdb.org/t/p/w185/";
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

    public MovieAdapter(List<Movie> l) {
        mList = l;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        MyViewHolder mvh = new MyViewHolder(v);

        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Picasso.with(holder.itemView.getContext()).load(baseImage + mList.get(position).backdrop_path).into(holder.ivPoster);
        holder.ivTitle.setText(mList.get(position).original_title);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setmRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r) {
        mRecyclerViewOnClickListenerHack = r;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView ivPoster;
        public TextView ivTitle;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivPoster = (ImageView) itemView.findViewById(R.id.imageview_item_poster);
            ivTitle = (TextView) itemView.findViewById(R.id.textview_item_title);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mRecyclerViewOnClickListenerHack != null) {
                mRecyclerViewOnClickListenerHack.OnClickListener(v, getAdapterPosition());
            }
        }
    }
}
