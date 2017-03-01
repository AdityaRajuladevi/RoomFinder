package com.roomfinder.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import com.roomfinder.R;
import com.roomfinder.model.AmenitiesItem;
import com.roomfinder.utils.KeyUtils;

/**
 * Created by admin on 4/7/16.
 */
public class AmenitiesGridAdapter extends RecyclerView.Adapter<AmenitiesGridAdapter.ViewHolderStub> {

    private ArrayList<AmenitiesItem> amenitiesItems;
    private Context mContext;

    public AmenitiesGridAdapter(Context ctx, ArrayList<AmenitiesItem> amenitiesList) {
        mContext = ctx;
        amenitiesItems = amenitiesList;

    }


    @Override
    public ViewHolderStub onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rootView = inflater.inflate(R.layout.amenities_layout, parent, false);
        ViewHolderStub viewHolderStub = new ViewHolderStub(rootView);
        return viewHolderStub;
    }

    @Override
    public void onBindViewHolder(ViewHolderStub holder, int position) {
        AmenitiesItem eachItem = amenitiesItems.get(position);
        holder.textView.setText(eachItem.getAmenitiesName());
        holder.imageView.setImageResource(eachItem.getAmenitiesResourceId());
    }

    @Override
    public int getItemCount() {
        return amenitiesItems.size();
    }

    public class ViewHolderStub extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolderStub(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.amenities_layout_image);
            textView = (TextView) itemView.findViewById(R.id.amenities_layout_txt);
        }
    }
}
