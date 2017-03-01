package com.roomfinder.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.roomfinder.R;
import com.roomfinder.adapter.ChatMessagesAdapter;
import com.roomfinder.backendlessutils.Defaults;
import com.roomfinder.callbacks.FragmentInteractionListener;
import com.roomfinder.callbacks.ItemClickSupport;
import com.roomfinder.database.DataBaseHelper;
import com.roomfinder.messaging.ChatMessage;
import com.roomfinder.ui.UserChatActivity;
import com.roomfinder.utils.Logger;
import com.roomfinder.utils.SharedPreferenceManager;

/**
 * Created by Aditya on 2/20/2016.
 */
public class ChatFragment extends Fragment {

    private FragmentInteractionListener mListener;

    private RecyclerView chatList;
    private ChatMessagesAdapter messagesAdapter;
    private String localUserId;


    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //  mParam1 = getArguments().getString(ARG_PARAM1);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        initUI(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Logger.v("Chat Fragment Resumed ");
    }

    @Override
    public void onPause() {
        super.onPause();
        // Logger.v("Chat Fragment Paused ");
    }

    private void initUI(View rootView) {

        SharedPreferenceManager sm = new SharedPreferenceManager(getActivity());
        localUserId = sm.getObjectIdOFLoggedInUser();

        chatList = (RecyclerView) rootView.findViewById(R.id.chat_list);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        chatList.setLayoutManager(manager);
        ArrayList<ChatMessage> msgsAdapterList = new ArrayList<>();
        messagesAdapter = new ChatMessagesAdapter(getActivity(), msgsAdapterList, localUserId);
        chatList.setAdapter(messagesAdapter);
        setOrRefreshChatData();
        ItemClickSupport.addTo(chatList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent i = new Intent(getActivity(), UserChatActivity.class);
                ChatMessage item = messagesAdapter.getChatUsersList().get(position);
                if (item.getFromId().equals(localUserId))
                    i.putExtra(Defaults.KEY_COMPANION_ID, item.getToId());
                else
                    i.putExtra(Defaults.KEY_COMPANION_ID, item.getFromId());
                startActivity(i);
            }
        });
    }


    private void setOrRefreshChatData() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getActivity());
        dataBaseHelper.openDB();
        ArrayList<ChatMessage> recentMessagesList = dataBaseHelper.getRecentMessageFromUsers();
        dataBaseHelper.closeDB();
        messagesAdapter.getChatUsersList().clear();
        messagesAdapter.getChatUsersList().addAll(recentMessagesList);
        messagesAdapter.notifyDataSetChanged();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInteractionListener) {
            mListener = (FragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getActivity());
            // becoming visible .. change data in the adapter
            if (sharedPreferenceManager.getMessageStatus()) {
                setOrRefreshChatData();
                sharedPreferenceManager.saveMessageReceivedStatus(false);
            }
            //  Logger.v("Becoming Visible");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
