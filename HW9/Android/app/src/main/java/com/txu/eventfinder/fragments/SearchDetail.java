package com.txu.eventfinder.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.txu.eventfinder.EventDetailActivity;
import com.txu.eventfinder.R;
import com.txu.eventfinder.adapters.SearchDataAdapter;
import com.txu.eventfinder.models.SearchCard;

import java.util.ArrayList;

public class SearchDetail extends Fragment {

    private RecyclerView mRecyclerView;
    private SearchDataAdapter mSearchAdapter;
    private ArrayList<SearchCard> searchList;

    private ProgressBar mProgressBar;
    private AppCompatButton BackBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_detail, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.search_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                view.getContext(), LinearLayoutManager.VERTICAL, false
        );
        mProgressBar = view.findViewById(R.id.search_progress);
        NestedScrollView search_parent = view.findViewById(R.id.search_parent);
        mRecyclerView = view.findViewById(R.id.search_recyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        BackBtn = view.findViewById(R.id.BackButton);
        CardView search_cardView = view.findViewById(R.id.search_cardView);

        mProgressBar.setVisibility(View.VISIBLE);
        search_parent.setVisibility(View.GONE);

        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });

        Bundle bundle = getArguments();
        if(bundle != null){
            if(bundle.containsKey("SearchData")){
                mProgressBar.setVisibility(View.GONE);
                search_parent.setVisibility(View.VISIBLE);
                searchList = (ArrayList<SearchCard>) bundle.getSerializable("SearchData");
                mSearchAdapter = new SearchDataAdapter(view.getContext(), searchList);
                mRecyclerView.setAdapter(mSearchAdapter);

                mSearchAdapter.setOnItemClickListener(new SearchDataAdapter.OnItemClickListener(){
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getActivity(), EventDetailActivity.class);
                        SearchCard clickedItem = searchList.get(position);

                        intent.putExtra("EventName", clickedItem.getEvent());
                        intent.putExtra("RowNum", clickedItem.getIndex());
                        startActivity(intent);
                    }
                });
            }
            if(bundle.containsKey("NoRecord")){
                mProgressBar.setVisibility(View.GONE);
                search_parent.setVisibility(View.GONE);
                search_cardView.setVisibility(View.VISIBLE);
            }
        }else {
            mProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }
}