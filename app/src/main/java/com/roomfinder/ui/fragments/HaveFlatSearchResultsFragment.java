package com.roomfinder.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.roomfinder.R;
import com.roomfinder.adapter.SearchResultsGridAdapter;
import com.roomfinder.callbacks.FragmentInteractionListener;
import com.roomfinder.callbacks.ItemClickSupport;
import com.roomfinder.model.SearchResultItem;
import com.roomfinder.ui.SearchResultsActivity;
import com.roomfinder.ui.SelectedProfileActivity;

/**
 * Created by Aditya on 3/6/2016.
 */
public class HaveFlatSearchResultsFragment extends Fragment {


    private FragmentInteractionListener mListener;
    private RecyclerView searchGridView;
    private ArrayList<SearchResultItem> haveFlatsList;

    public HaveFlatSearchResultsFragment() {
        // Required empty public constructor
    }

    public static HaveFlatSearchResultsFragment newInstance(ArrayList<SearchResultItem> items) {
        HaveFlatSearchResultsFragment fragment = new HaveFlatSearchResultsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setHaveFlatsList(items);
        return fragment;
    }

    public void setHaveFlatsList(ArrayList<SearchResultItem> haveFlatsList) {
        this.haveFlatsList = haveFlatsList;
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
        View rootView = inflater.inflate(R.layout.fragment_search_have_flat, container, false);
        initUI(rootView);
        return rootView;
    }

    private void initUI(View rootView) {
        TextView search_msg = (TextView) rootView.findViewById(R.id.txt_info);
        if (haveFlatsList.size() <= 0)
            search_msg.setVisibility(View.VISIBLE);

        searchGridView = (RecyclerView) rootView.findViewById(R.id.have_flat_grid_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        searchGridView.setLayoutManager(gridLayoutManager);
        searchGridView.setHasFixedSize(true);

        final SearchResultsGridAdapter searchResultsGridAdapter = new SearchResultsGridAdapter(getActivity(), haveFlatsList);
        searchGridView.setAdapter(searchResultsGridAdapter);

        ItemClickSupport.addTo(searchGridView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent = new Intent(getActivity(), SelectedProfileActivity.class);
                intent.putExtra("SelectedObjId", haveFlatsList.get(position).getObjId());
                intent.putExtra("isHaveFlatType", true);
                startActivity(intent);
            }
        });
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
