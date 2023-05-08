package com.txu.eventfinder.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.txu.eventfinder.R;
import com.txu.eventfinder.adapters.ArtistAdapter;
import com.txu.eventfinder.models.ArtistItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EventArtistFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ArtistAdapter mArtistAdapter;
    private ArrayList<ArtistItem> mArtistList;
    private RequestQueue mRequestQueue;
//    String BackEndUrl = "http://192.168.1.52:8080/";
    String BackEndUrl = "https://hw8-nodejs-379800.wl.r.appspot.com/";
    private SharedPreferences ArtistName;
    private SharedPreferences EventDetailLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_artist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.Artist_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                view.getContext(), LinearLayoutManager.VERTICAL, false
        );
        mRecyclerView.setLayoutManager(layoutManager);
        mArtistList = new ArrayList<>();

        ProgressBar Artist_progress = view.findViewById(R.id.Artist_progress);
        NestedScrollView Artist_parent = view.findViewById(R.id.Artist_parent);
        CardView event_artist_cardView = view.findViewById(R.id.event_artist_cardView);

        Artist_progress.setVisibility(View.VISIBLE);
        Artist_parent.setVisibility(View.GONE);

        EventDetailLink = getActivity().getSharedPreferences("EventDetailLink", Context.MODE_PRIVATE);
        String Genre = EventDetailLink.getString("Genre","");

        mRequestQueue = Volley.newRequestQueue(getActivity());
        ArtistName = getActivity().getSharedPreferences("EventDetailLink", Context.MODE_PRIVATE);
        String artists = ArtistName.getString("ArtistName","");
        String[] mArtists = artists.split(" \\| ");

        if(!Genre.equals("Music")){
            event_artist_cardView.setVisibility(View.VISIBLE);
            Artist_parent.setVisibility(View.GONE);
        }else{
            event_artist_cardView.setVisibility(View.GONE);
            Artist_parent.setVisibility(View.VISIBLE);
            for(int i = 0; i < mArtists.length; i ++){
                String spotifyQuery = BackEndUrl + "spotify?Artist=" + mArtists[i];
                System.out.println(spotifyQuery);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, spotifyQuery, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Artist_progress.setVisibility(View.GONE);
                        Artist_parent.setVisibility(View.VISIBLE);
                        try {
                            String Name = response.getString("Name");
                            String Name_img = response.getString("Name_Img");

                            String Fol = formatNumber(Integer.parseInt(response.getString("Followers").replaceAll(",","")));
                            String Name_Fol = Fol  + " Followers";
                            String Name_Spotify = response.getString("Spotify_Link");
                            String Name_Popularity = response.getString("Popularity");
                            String[] AlbumArr = response.getString("Albums").replaceAll("\\[|\\]|\"", "").split(",");
                            String Name_Album1 = "";
                            String Name_Album2 = "";
                            String Name_Album3 = "";

                            if(AlbumArr.length == 3){
                                Name_Album1 = AlbumArr[0].replaceAll("\\\\", "");
                                Name_Album2 = AlbumArr[1].replaceAll("\\\\", "");
                                Name_Album3 = AlbumArr[2].replaceAll("\\\\", "");
                            } else if (AlbumArr.length == 2) {
                                Name_Album1 = AlbumArr[0].replaceAll("\\\\", "");
                                Name_Album2 = AlbumArr[1].replaceAll("\\\\", "");
                            } else if(AlbumArr.length == 1){
                                Name_Album1 = AlbumArr[0].replaceAll("\\\\", "");
                            }

                            int Progress = Integer.parseInt(response.getString("Popularity"));

                            mArtistList.add(new ArtistItem(Name_img, Name, Name_Fol, Name_Album1, Name_Album2, Name_Album3, Name_Spotify, Name_Popularity, Progress));
                            mArtistAdapter = new ArtistAdapter(view.getContext(), mArtistList);
                            mRecyclerView.setAdapter(mArtistAdapter);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Artist_progress.setVisibility(View.GONE);
                        Artist_parent.setVisibility(View.VISIBLE);
                        Snackbar.make(view, "Spotify Details wrong", Snackbar.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                });
                mRequestQueue.add(request);
            }
        }
    }

    public static String formatNumber(int number) {
        if (number >= 1000000) {
            return String.format("%.1fM", number / 1000000.0);
        } else if (number >= 1000) {
            return String.format("%.1fK", number / 1000.0);
        } else {
            return String.valueOf(number);
        }
    }
}