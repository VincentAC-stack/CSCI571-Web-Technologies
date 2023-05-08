package com.txu.eventfinder.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.txu.eventfinder.R;
import com.txu.eventfinder.models.SearchCard;

import java.util.ArrayList;

public class SearchDataAdapter extends RecyclerView.Adapter<SearchDataAdapter.SearchDataViewHolder> {
    private Context mContext;
    private ArrayList<SearchCard> SearchCardList;
    private OnItemClickListener mListener;
    private SharedPreferences FavoriteItem;
    private SharedPreferences EventDetailLink;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public SearchDataAdapter(Context context, ArrayList<SearchCard> searchList){
        this.mContext = context;
        this.SearchCardList = searchList;
    }

    @NonNull
    @Override
    public SearchDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_searchdata, parent, false);
        return new SearchDataViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchDataViewHolder holder, int position) {
        SearchCard currentData = SearchCardList.get(position);

        String imageUrl = currentData.getmImageUrl();
        String event = currentData.getEvent();
        String venue = currentData.getVenue();
        String genre = currentData.getGenre();
        String date = currentData.getDate();
        String time = currentData.getTime();
        String index = currentData.getIndex();

        holder.mTextEvent.setText(event);
        holder.mTextEvent.setSelected(true);

        holder.mTextVenue.setText(venue);
        holder.mTextVenue.setSelected(true);

        holder.mTextGenre.setText(genre);
        EventDetailLink = mContext.getSharedPreferences("EventDetailLink", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = EventDetailLink.edit();
        editor.putString("Genre", genre);
        editor.apply();

        holder.mTextDate.setText(date);
        holder.mTextTime.setText(time);
        Picasso.get().load(imageUrl).resize(90, 90).centerCrop().into(holder.mImageView);

        holder.mHeartFillBtn.setVisibility(View.GONE);
        holder.mHeartBtn.setVisibility(View.VISIBLE);

        holder.mHeartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mHeartFillBtn.setVisibility(View.VISIBLE);
                holder.mHeartBtn.setVisibility(View.GONE);

                FavoriteItem = mContext.getSharedPreferences("FavoriteItem", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = FavoriteItem.edit();
                editor.putString("SearchIndex", index);
                editor.putString("SearchImg", imageUrl);
                editor.putString("SearchName", event);
                editor.putString("VenueName", venue);
                editor.putString("Genre", genre);
                editor.putString("Date", date);
                editor.putString("Time", time);
                editor.apply();

                String showText = event + " added to favorites";

                Snackbar.make(view, showText, Snackbar.LENGTH_SHORT).show();
            }
        });

        holder.mHeartFillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.mHeartFillBtn.setVisibility(View.GONE);
                holder.mHeartBtn.setVisibility(View.VISIBLE);

                String showText = event + " removed from favorites";

                Snackbar.make(view, showText, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return SearchCardList.size();
    }

    public class SearchDataViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTextEvent;
        public TextView mTextVenue;
        public TextView mTextGenre;
        public TextView mTextDate;
        public TextView mTextTime;
        public AppCompatButton mHeartBtn;
        public AppCompatButton mHeartFillBtn;

        public SearchDataViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.search_image);
            mTextEvent = itemView.findViewById(R.id.event_name);
            mTextVenue = itemView.findViewById(R.id.venue_name);
            mTextGenre = itemView.findViewById(R.id.cat_name);
            mTextDate = itemView.findViewById(R.id.search_date);
            mTextTime = itemView.findViewById(R.id.search_time);
            mHeartBtn = itemView.findViewById(R.id.heart);
            mHeartFillBtn = itemView.findViewById(R.id.heart_fill);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
