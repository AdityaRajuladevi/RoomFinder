package com.roomfinder.messaging;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.roomfinder.R;

/**
 * Created by admin on 4/19/16.
 */
public class ViewHolderToMessages extends RecyclerView.ViewHolder {

    private TextView txt_to_msg;

    public ViewHolderToMessages(View itemView) {
        super(itemView);
        txt_to_msg = (TextView) itemView.findViewById(R.id.txtToMsg);
    }

    public TextView getTxt_to_msg() {
        return txt_to_msg;
    }
}
