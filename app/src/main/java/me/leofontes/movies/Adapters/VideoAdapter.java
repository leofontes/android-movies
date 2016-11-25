package me.leofontes.movies.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.leofontes.movies.Interfaces.RecyclerViewOnClickListenerHack;
import me.leofontes.movies.Models.Video;
import me.leofontes.movies.R;
import me.leofontes.movies.Utility;

/**
 * Created by leo on 24/11/16.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    private List<Video> mList;
    private String mBaseNumber;

    public VideoAdapter(List<Video> l) {
        mList = l;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mBaseNumber = parent.getResources().getString(R.string.video_base_number);

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.ivNumber.setText(mBaseNumber + " " + (position + 1));
        holder.ivDesc.setText(mList.get(position).name);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView ivImageView;
        public TextView ivNumber;
        public TextView ivDesc;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivImageView = (ImageView) itemView.findViewById(R.id.imageview_video);
            ivNumber = (TextView) itemView.findViewById(R.id.textview_video_number);
            ivDesc = (TextView) itemView.findViewById(R.id.textview_video_desc);

            ivImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Create two intents, one for the Youtube App and the other for regular browser
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + mList.get(getAdapterPosition()).key));
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Utility.genYoutubeUrl(mList.get(getAdapterPosition()).key)));

            //If user has Youtube App, it opens it, if not, fallsback to Browser
            try {
                v.getContext().startActivity(appIntent);
            } catch(ActivityNotFoundException ex) {
                v.getContext().startActivity(webIntent);
            }
        }
    }
}
