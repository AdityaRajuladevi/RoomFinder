package com.roomfinder.messaging;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by admin on 4/19/16.
 */
public class ViewHolderDefaultMessages extends RecyclerView.ViewHolder {

    private TextView txt_default_msg;

    public ViewHolderDefaultMessages(View itemView) {
        super(itemView);
        txt_default_msg = (TextView) itemView.findViewById(android.R.id.text1);
    }

    public TextView getTxt_default_msg() {
        return txt_default_msg;
    }
}
