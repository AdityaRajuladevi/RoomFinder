package com.roomfinder.messaging;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.roomfinder.R;

/**
 * Created by admin on 4/19/16.
 */
public class ViewHolderFromMessages extends RecyclerView.ViewHolder {

    private TextView txt_from_msg;

    public ViewHolderFromMessages(View itemView) {
        super(itemView);
        txt_from_msg = (TextView) itemView.findViewById(R.id.txtFromMsg);
    }

    public TextView getTxt_from_msg() {
        return txt_from_msg;
    }
}
