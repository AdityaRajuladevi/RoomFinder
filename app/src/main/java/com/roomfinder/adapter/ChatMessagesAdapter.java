package com.roomfinder.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.roomfinder.R;
import com.roomfinder.backendlessutils.DefaultCallback;
import com.roomfinder.backendlessutils.Defaults;
import com.roomfinder.messaging.ChatMessage;
import com.roomfinder.messaging.ChatUser;
import com.roomfinder.model.UserPreferences;
import com.roomfinder.ui.HomeActivity;
import com.roomfinder.utils.Logger;
import com.roomfinder.utils.SharedPreferenceManager;

/**
 * Created by admin on 4/21/16.
 */
public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder> {

    private final Context mContext;
    private ArrayList<ChatMessage> chatUsersList;
    private String localUserId;

    public ChatMessagesAdapter(Context ctx, ArrayList<ChatMessage> msgsList, String localId) {
        chatUsersList = msgsList;
        mContext = ctx;
        localUserId = localId;

    }

    public ArrayList<ChatMessage> getChatUsersList() {
        return chatUsersList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v1 = inflater.inflate(R.layout.chat_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v1);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatMessage message = chatUsersList.get(position);

        if (message.getFromId().equals(localUserId)) {
            Picasso.with(mContext).load(Defaults.FILES_PROFILE_PIC_URL + "/" + message.getToId() + ".png").placeholder(R.drawable.profile).error(R.drawable.profile).into(holder.userProfilePic);
        } else {
            Picasso.with(mContext).load(Defaults.FILES_PROFILE_PIC_URL + "/" + message.getFromId() + ".png").placeholder(R.drawable.profile).error(R.drawable.profile).into(holder.userProfilePic);
        }
        holder.userName.setText(message.getCompanionName());
        holder.userRecentMessage.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return chatUsersList.size();
    }

    private void fetchChatUserName(String objId) {
        String whereClause = "ownerId =" + "'" + objId + "'";
        Logger.v(" Fetch where " + whereClause);
        BackendlessDataQuery dataQuery = new BackendlessDataQuery();
        dataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(ChatMessage.class).find(dataQuery, new AsyncCallback<BackendlessCollection<ChatMessage>>() {
            @Override
            public void handleResponse(BackendlessCollection<ChatMessage> chatMessageBackendlessCollection) {

            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView userProfilePic;
        private TextView userName;
        private TextView userRecentMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            userProfilePic = (ImageView) itemView.findViewById(R.id.msg_user_profile);
            userName = (TextView) itemView.findViewById(R.id.msg_user_name);
            userRecentMessage = (TextView) itemView.findViewById(R.id.msg_user_message);
        }
    }
}