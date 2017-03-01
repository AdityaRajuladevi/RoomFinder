package com.roomfinder.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.roomfinder.R;
import com.roomfinder.backendlessutils.Defaults;
import com.roomfinder.model.SearchResultItem;
import com.roomfinder.utils.CircleTransform;

/**
 * Created by admin on 2/22/16.
 */
public class SearchResultsGridAdapter extends RecyclerView.Adapter<SearchResultsGridAdapter.ViewHolderStub> {

    private ArrayList<SearchResultItem> mItemsList;
    private Context mContext;

    public SearchResultsGridAdapter(Context ctx, ArrayList<SearchResultItem> mItemsList) {
        mContext = ctx;
        this.mItemsList = mItemsList;
    }

    @Override
    public ViewHolderStub onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.grid_search_item, parent, false);
        ViewHolderStub viewHolderStub = new ViewHolderStub(rootView);
        return viewHolderStub;
    }

    @Override
    public void onBindViewHolder(ViewHolderStub holder, int position) {
        if (mItemsList != null) {
            SearchResultItem item = mItemsList.get(position);
            Picasso.with(mContext).load(Defaults.FILES_PROFILE_PIC_URL + "/" + item.getUserId() + ".png").into(holder.userPic);
            holder.userName.setText(item.getUserName());
            holder.location.setText(item.getLocationName());
            holder.occupancy.setText(item.getOccupancy());
            holder.rent.setText(item.getRent() + "$");
        }
    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }

    public class ViewHolderStub extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView userPic;
        TextView userName;
        TextView location;
        TextView occupancy;
        TextView rent;


        public ViewHolderStub(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            userPic = (ImageView) itemView.findViewById(R.id.grid_item_user_profile_pic);
            userName = (TextView) itemView.findViewById(R.id.grid_item_user_name);
            location = (TextView) itemView.findViewById(R.id.grid_item_location_name);
            occupancy = (TextView) itemView.findViewById(R.id.grid_item_occupancy_value);
            rent = (TextView) itemView.findViewById(R.id.grid_item_rent_value);
        }
    }

}
