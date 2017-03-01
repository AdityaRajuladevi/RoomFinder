package com.roomfinder.messaging;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.roomfinder.R;


/**
 * Created by admin on 4/19/16.
 */
public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final int CURRENT_USER = 1, COMPANION_USER = 0;
    ArrayList<ChatMessage> messagesList;

    public MessagesAdapter(ArrayList<ChatMessage> messagesList) {
        this.messagesList = messagesList;
    }

    public ArrayList<ChatMessage> getMessagesList() {
        return messagesList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case CURRENT_USER:
                View v1 = inflater.inflate(R.layout.list_item_message_right, parent, false);
                viewHolder = new ViewHolderToMessages(v1);
                break;
            case COMPANION_USER:
                View v2 = inflater.inflate(R.layout.list_item_message_left, parent, false);
                viewHolder = new ViewHolderFromMessages(v2);
                break;
            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder = new ViewHolderDefaultMessages(v);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case CURRENT_USER:
                ViewHolderToMessages vh1 = (ViewHolderToMessages) viewHolder;
                configureToMessages(vh1, position);
                break;
            case COMPANION_USER:
                ViewHolderFromMessages vh2 = (ViewHolderFromMessages) viewHolder;
                configureFromMessages(vh2, position);
                break;
            default:
                ViewHolderDefaultMessages vh = (ViewHolderDefaultMessages) viewHolder;
                configureDefaultMessages(vh, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }


    //Returns the view type of the item at position for the purposes of view recycling.
    // here in this case 0 represents companion and 1 represents self...
    @Override
    public int getItemViewType(int position) {
        return messagesList.get(position).isSelf();
    }

    private void configureFromMessages(ViewHolderFromMessages vh, int position) {
        ChatMessage message = messagesList.get(position);
        vh.getTxt_from_msg().setText(message.getMessage());
    }

    private void configureToMessages(ViewHolderToMessages vh, int position) {
        ChatMessage message = messagesList.get(position);
        vh.getTxt_to_msg().setText(message.getMessage());
    }

    private void configureDefaultMessages(ViewHolderDefaultMessages vh, int position) {
        ChatMessage message = messagesList.get(position);
        vh.getTxt_default_msg().setText(message.getMessage());
    }

}
