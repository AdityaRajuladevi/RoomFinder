package com.roomfinder.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.roomfinder.R;

/**
 * Created by admin on 4/7/16.
 */

public class ImagesGridAdapter extends RecyclerView.Adapter<ImagesGridAdapter.ViewHolderStub> {

    private boolean mIsNoImageSelected;
    private Context mContext;
    private boolean isFullScreenImagesView;
    private ArrayList<String> mImagesList;

    public ImagesGridAdapter(Context ctx, ArrayList<String> imagesList, boolean isFullScreenImagesView) {
        mContext = ctx;
        mImagesList = imagesList;
        this.isFullScreenImagesView = isFullScreenImagesView;
        if (mImagesList.size() <= 0)
            mIsNoImageSelected = true;
        else
            mIsNoImageSelected = false;
    }

    public ArrayList<String> getmImagesList() {
        return mImagesList;
    }

    @Override
    public ViewHolderStub onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = null;
        if (isFullScreenImagesView)
            rootView = inflater.inflate(R.layout.fullscreen_image_item, parent, false);
        else
            rootView = inflater.inflate(R.layout.image_item, parent, false);


        ViewHolderStub viewHolderStub = new ViewHolderStub(rootView);
        return viewHolderStub;
    }

    @Override
    public void onBindViewHolder(ViewHolderStub holder, int position) {
        if (!mIsNoImageSelected) {
            holder.notAvailableTextView.setVisibility(View.INVISIBLE);
            String imagePath = mImagesList.get(position);
            // Logger.v("Image Path of "+position+" = "+imagePath);
            Glide.with(mContext)
                    .load(imagePath)
                    .fitCenter()
                    .into(holder.imageView);

        } else {
            holder.notAvailableTextView.setVisibility(View.INVISIBLE);
            Picasso.with(mContext).load("dummy to invoke error").placeholder(R.color.image).error(R.color.image).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        int size = mImagesList.size();
        if (size == 0)
            size = 3;
        return size;
    }


    public void refreshAdapter(ArrayList<String> newDataList) {
        mImagesList.clear();
        mImagesList.addAll(newDataList);
        if (mImagesList.size() <= 0) {
            mIsNoImageSelected = true;
            mImagesList.add("");
            mImagesList.add("");
            mImagesList.add("");
        } else
            mIsNoImageSelected = false;
        notifyDataSetChanged();
    }

    public class ViewHolderStub extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView imageView;
        TextView notAvailableTextView;

        public ViewHolderStub(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            imageView = (ImageView) itemView.findViewById(R.id.grid_image_item);
            notAvailableTextView = (TextView) itemView.findViewById(R.id.not_available);
        }
    }
}