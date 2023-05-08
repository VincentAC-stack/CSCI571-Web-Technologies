package com.txu.eventfinder.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.txu.eventfinder.R;
import com.txu.eventfinder.adapters.FavoriteSearchAdapter;
import com.txu.eventfinder.adapters.SearchDataAdapter;
import com.txu.eventfinder.models.SearchCard;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{
    private SharedPreferences FavoriteOutItem;
    private RecyclerView mRecyclerView;
    private FavoriteSearchAdapter mFavoriteAdapter;
    private ArrayList<SearchCard> searchList = new ArrayList<SearchCard>();

    private ProgressBar mProgressBar;
    private boolean mUpdateInProgress = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProgressBar favorite_progress = view.findViewById(R.id.favorite_progress);
        CardView favorite_cardView = view.findViewById(R.id.favorite_cardView);
        NestedScrollView favorite_parent = view.findViewById(R.id.favorite_parent);
        mRecyclerView = view.findViewById(R.id.favorite_recycler_view);

        FavoriteOutItem = getActivity().getSharedPreferences("FavoriteItem", Context.MODE_PRIVATE);
        String index = FavoriteOutItem.getString("SearchIndex","");
        String SearchImg = FavoriteOutItem.getString("SearchImg","");
        String SearchName = FavoriteOutItem.getString("SearchName","");
        String VenueName = FavoriteOutItem.getString("VenueName","");
        String Genre = FavoriteOutItem.getString("Genre","");
        String Date = FavoriteOutItem.getString("Date","");
        String Time = FavoriteOutItem.getString("Time","");

        if(index != ""){
            favorite_cardView.setVisibility(View.GONE);
            favorite_parent.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);

            searchList.add(new SearchCard(SearchImg, SearchName, VenueName, Genre, Date, Time, index));
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                    view.getContext(), LinearLayoutManager.VERTICAL, false
            );
            mRecyclerView.setLayoutManager(layoutManager);
            mFavoriteAdapter = new FavoriteSearchAdapter(view.getContext(), searchList);
            mRecyclerView.setAdapter(mFavoriteAdapter);

            FavoriteOutItem.registerOnSharedPreferenceChangeListener(this);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        FavoriteOutItem = getActivity().getSharedPreferences("FavoriteItem", Context.MODE_PRIVATE);
        String index = FavoriteOutItem.getString("SearchIndex","");
        String SearchImg = FavoriteOutItem.getString("SearchImg","");
        String SearchName = FavoriteOutItem.getString("SearchName","");
        String VenueName = FavoriteOutItem.getString("VenueName","");
        String Genre = FavoriteOutItem.getString("Genre","");
        String Date = FavoriteOutItem.getString("Date","");
        String Time = FavoriteOutItem.getString("Time","");

        searchList.add(new SearchCard(SearchImg, SearchName, VenueName, Genre, Date, Time, index));
        mFavoriteAdapter.setData(searchList);
        mFavoriteAdapter.notifyDataSetChanged();
    }

    public void onDestroyView() {
        super.onDestroyView();

        // Unregister the shared preferences change listener to prevent memory leaks
        FavoriteOutItem.unregisterOnSharedPreferenceChangeListener(this);
    }
}