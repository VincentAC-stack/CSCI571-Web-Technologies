package com.txu.eventfinder.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.txu.eventfinder.R;
import com.txu.eventfinder.models.ArtistItem;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    private Context mContext;
    private ArrayList<ArtistItem> mArtistList;

    public ArtistAdapter(Context context, ArrayList<ArtistItem> artistList){
        mContext = context;
        mArtistList = artistList;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.artists_item, parent, false);
        return new ArtistViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        ArtistItem currentItem = mArtistList.get(position);

        String artistImageUrl = currentItem.getArtistImageUrl();
        String artistName = currentItem.getArtistName();
        String artistFollower = currentItem.getArtistFollower();
        String artistSpotifyUrl = currentItem.getArtistSpotifyUrl();
        String artistPopularity = currentItem.getArtistPopularity();
        String artistAlbumUrl1 = currentItem.getArtistAlbumUrl1();
        String artistAlbumUrl2 = currentItem.getArtistAlbumUrl2();
        String artistAlbumUrl3 = currentItem.getArtistAlbumUrl3();

        int Progress = currentItem.getProgress();

        Picasso.get().load(artistImageUrl).resize(90, 90).centerCrop().into(holder.mArtistImageView);
        holder.mArtistNameText.setText(artistName);
        holder.mArtistFollowerText.setText(artistFollower);

        holder.mArtistSpotifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(artistSpotifyUrl));
                mContext.startActivity(intent);
            }
        });
        holder.mArtistSpotifyBtn.setPaintFlags(holder.mArtistSpotifyBtn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if(artistAlbumUrl1 != ""){
            Picasso.get().load(artistAlbumUrl1).fit().into(holder.mArtistAlbumImage1);
        }
        if(artistAlbumUrl2 != ""){
            Picasso.get().load(artistAlbumUrl2).fit().into(holder.mArtistAlbumImage2);
        }
        if(artistAlbumUrl3 != ""){
            Picasso.get().load(artistAlbumUrl3).fit().into(holder.mArtistAlbumImage3);
        }

        holder.mProgressBar.setProgress(Progress);
        holder.mProgressText.setText(artistPopularity);
    }

    @Override
    public int getItemCount() {
        return mArtistList.size();
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder{
        public ImageView mArtistImageView;
        public TextView mArtistNameText;
        public TextView mArtistFollowerText;
        public AppCompatButton mArtistSpotifyBtn;
        public TextView mArtistPopularityText;
        public ImageView mArtistAlbumImage1;
        public ImageView mArtistAlbumImage2;
        public ImageView mArtistAlbumImage3;
        public ProgressBar mProgressBar;
        public TextView mProgressText;

        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            mArtistImageView = itemView.findViewById(R.id.Artist_image);
            mArtistNameText = itemView.findViewById(R.id.Artist_Name);
            mArtistFollowerText = itemView.findViewById(R.id.Artist_Fol);
            mArtistSpotifyBtn = itemView.findViewById(R.id.Artist_Spotify);
            mArtistPopularityText = itemView.findViewById(R.id.Artist_Popularity);
            mArtistAlbumImage1 = itemView.findViewById(R.id.Artist_Album1);
            mArtistAlbumImage2 = itemView.findViewById(R.id.Artist_Album2);
            mArtistAlbumImage3 = itemView.findViewById(R.id.Artist_Album3);

            mProgressBar = itemView.findViewById(R.id.progress_bar);
            mProgressText = itemView.findViewById(R.id.text_view_progress);
        }
    }
}
