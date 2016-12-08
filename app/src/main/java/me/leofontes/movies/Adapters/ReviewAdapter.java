package me.leofontes.movies.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import me.leofontes.movies.Models.Review;
import me.leofontes.movies.R;

/**
 * Created by leo on 24/11/16.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {
    private List<Review> mList;
    private String mBaseNumber;
    private String mBaseAuthor;

    public ReviewAdapter(List<Review> l) {
        mList = l;
    }

    public void clearData() {
        int size = mList.size();

        for(int i = 0; i < size; i++) {
            mList.remove(0);
        }

        this.notifyItemRangeRemoved(0, size);
    }

    public void setList(List<Review> l) {
        this.mList = l;
        this.notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mBaseNumber = parent.getResources().getString(R.string.review_base_number);
        mBaseAuthor = parent.getResources().getString(R.string.review_base_author);

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        final MyViewHolder mvh = new MyViewHolder(v);

        mvh.ivClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mvh.ivContent.getVisibility() == View.GONE) {
                    mvh.ivContent.setVisibility(View.VISIBLE);
                } else {
                    mvh.ivContent.setVisibility(View.GONE);
                }
            }
        });

        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // Configure the text content
        holder.ivNumber.setText(mBaseNumber + " " + (position+1)); //+1 to match what users are usually accustomed to
        holder.ivAuthor.setText(mBaseAuthor + " " + mList.get(position).author);
        holder.ivContent.setText(mList.get(position).content);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView ivNumber;
        public TextView ivAuthor;
        public TextView ivContent;
        public LinearLayout ivClickable;

        public MyViewHolder(View itemView) {
            super(itemView);

            ivNumber = (TextView) itemView.findViewById(R.id.textview_review_number);
            ivAuthor = (TextView) itemView.findViewById(R.id.textview_review_author);
            ivContent = (TextView) itemView.findViewById(R.id.textview_review_content);
            ivClickable = (LinearLayout) itemView.findViewById(R.id.linearlayout_clickable_review);
        }
    }
}
