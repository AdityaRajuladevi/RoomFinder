package com.roomfinder.adapter;

import android.content.Context;
import android.net.Uri;
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
 * Created by Aditya on 2/27/2016.
 */
public class SelectedImagesGridAdapter extends RecyclerView.Adapter<SelectedImagesGridAdapter.ViewHolderStub> {

    boolean mIsNoImageSelected;
    Context mContext;
    private ArrayList<Uri> mImagesList;

    public SelectedImagesGridAdapter(Context ctx, ArrayList<Uri> imagesList) {
        mContext = ctx;
        mImagesList = imagesList;
        if (mImagesList.size() <= 0)
            mIsNoImageSelected = true;
        else
            mIsNoImageSelected = false;
    }

    @Override
    public ViewHolderStub onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.image_item, parent, false);
        ViewHolderStub viewHolderStub = new ViewHolderStub(rootView);
        return viewHolderStub;
    }

    @Override
    public void onBindViewHolder(ViewHolderStub holder, int position) {
        if (!mIsNoImageSelected) {
            holder.notAvailableMessage.setVisibility(View.GONE);
            String imagePath = mImagesList.get(position).toString();
            // Logger.v("Image Path of "+position+" = "+imagePath);
            Glide.with(mContext)
                    .load(imagePath)
                    .fitCenter()
                    .into(holder.imageView);
        } else {
            holder.notAvailableMessage.setVisibility(View.VISIBLE);
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

    public void refreshAdapter(ArrayList<Uri> newDataList) {
        mImagesList.clear();
        mImagesList.addAll(newDataList);
        if (mImagesList.size() <= 0) {
            mIsNoImageSelected = true;
            mImagesList.add(null);
            mImagesList.add(null);
            mImagesList.add(null);
        } else
            mIsNoImageSelected = false;
        notifyDataSetChanged();
    }

    public class ViewHolderStub extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView imageView;
        TextView notAvailableMessage;

        public ViewHolderStub(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            imageView = (ImageView) itemView.findViewById(R.id.grid_image_item);
            notAvailableMessage = (TextView) itemView.findViewById(R.id.not_available);
        }
    }
}
