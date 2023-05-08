package com.txu.eventfinder.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.txu.eventfinder.EventDetailActivity;
import com.txu.eventfinder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventDetailFragment extends Fragment {
    private SharedPreferences UserInput;
//    String BackEndUrl = "http://192.168.1.52:8080/";
    String BackEndUrl = "https://hw8-nodejs-379800.wl.r.appspot.com/";
    private String ArtistName = "";
    private String VenueName = "";
    private String DateName = "";
    private String TimeName = "";
    private String Genres = "";
    private String Price = "";
    private String TicketStatus = "";
    private String TicketUrl = "";
    private String SeatMapUrl = "";
    private String EventName = "";
    private SharedPreferences EventDetailLink;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout EventData_parent = view.findViewById(R.id.EventData_parent);
        TextView Detail_Artist = view.findViewById(R.id.Detail_Artist);
        TextView Detail_Venue = view.findViewById(R.id.Detail_Venue);
        TextView Detail_Date = view.findViewById(R.id.Detail_Date);
        TextView Detail_Time = view.findViewById(R.id.Detail_Time);
        TextView Detail_Genre = view.findViewById(R.id.Detail_Genre);
        TextView Detail_Price = view.findViewById(R.id.Detail_Price);
        TextView Detail_Ticket_Status = view.findViewById(R.id.Detail_Ticket_Status);
        AppCompatButton Detail_Ticket_URL = view.findViewById(R.id.Detail_Ticket_URL);
        ImageView SeatMap = view.findViewById(R.id.SeatMap);

        LinearLayout EventName_parent = view.findViewById(R.id.EventName_parent);
        TextView Name_Price = view.findViewById(R.id.Name_Price);
        NestedScrollView Detail_Parent = view.findViewById(R.id.Detail_Parent);
        ProgressBar Detail_Bar = view.findViewById(R.id.Detail_Bar);

        UserInput = getActivity().getSharedPreferences("FormInput", Context.MODE_PRIVATE);
        String keyword = UserInput.getString("keyword","");
        String dist = UserInput.getString("distance","");
        String cat = UserInput.getString("cat","");
        String loc = UserInput.getString("loc","");

        EventDetailActivity EventActivity = (EventDetailActivity)getActivity();
        String eventRow = EventActivity.getEventRow();

        String EventQuery = BackEndUrl + "event?input_kw=" + keyword + "&input_dist=" + dist + "&input_cat=" + cat + "&input_location=" + loc + "&Row=" + eventRow;
        System.out.println(EventQuery);

        Detail_Bar.setVisibility(View.VISIBLE);
        Detail_Parent.setVisibility(View.GONE);

        RequestQueue EventQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest EventRequest = new JsonArrayRequest(Request.Method.GET, EventQuery, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Detail_Bar.setVisibility(View.GONE);
                Detail_Parent.setVisibility(View.VISIBLE);
                for(int i = 0; i < response.length(); i ++){
                    try {
                        JSONObject SearchObj = (JSONObject) response.get(i);
                        String ArtistLists = "";
                        ArtistName = SearchObj.getString("Artist/Team");
                        ArtistName = ArtistName.substring(1, ArtistName.length() - 1);
                        String[] ArtistArr = ArtistName.split(",");
                        for (int j = 0; j < ArtistArr.length; j++) {
                            ArtistArr[j] = ArtistArr[j].trim().replaceAll("^\"|\"$", "");
                        }

                        for(int index = 0; index < ArtistArr.length - 1; index++){
                            ArtistLists += ArtistArr[index] + " | ";
                        }

                        ArtistLists += ArtistArr[ArtistArr.length - 1];
                        VenueName = SearchObj.getString("Venue");

                        String date = SearchObj.getString("localDate");
                        String year = date.split("-")[0];
                        String month = date.split("-")[1];
                        String day = date.split("-")[2];

                        String monthString = new DateFormatSymbols().getMonths()[Integer.parseInt(month)-1];
                        month = monthString.substring(0, 3);
                        DateName = month + " " + day + ", " + year;

                        String time24 = SearchObj.getString("localTime");
                        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
                        SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a");
                        String time12 = "";
                        try {
                            Date date12 = inputFormat.parse(time24);
                            time12 = outputFormat.format(date12);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        TimeName = time12;

                        if(!"Undefined".equals(SearchObj.getString("Genres_segment"))){
                            Genres += SearchObj.getString("Genres_segment") + " | ";
                        }
                        if(!"Undefined".equals(SearchObj.getString("Genres_genre"))){
                            Genres += SearchObj.getString("Genres_genre") + " | ";
                            System.out.println(SearchObj.getString("Genres_genre"));
                        }
                        if(!"Undefined".equals(SearchObj.getString("Genres_subGenre"))){
                            Genres += SearchObj.getString("Genres_subGenre") + " | ";
                        }
                        if(!"Undefined".equals(SearchObj.getString("Genres_type"))){
                            Genres += SearchObj.getString("Genres_type") + " | ";
                        }
                        if(!"Undefined".equals(SearchObj.getString("Genres_subType"))){
                            Genres += SearchObj.getString("Genres_subType") + " | ";
                        }
                        if(Genres.substring(Genres.length() - 3).equals(" | ")){
                            Genres = Genres.substring(0, Genres.length() - 3);
                        }

                        if(!"Undefined".equals(SearchObj.getString("Price Ranges"))){
                            Price = SearchObj.getString("Price Ranges").split(" ")[0] + " - " + SearchObj.getString("Price Ranges").split(" ")[1] + " (USD)";
                        }else{
                            EventData_parent.removeView(Detail_Price);
                            EventName_parent.removeView(Name_Price);
                        }

                        TicketStatus = SearchObj.getString("Ticket Status");
                        TicketUrl = SearchObj.getString("Ticket URL");
                        SeatMapUrl = SearchObj.getString("SeatMap_URL");
                        EventName = SearchObj.getString("Event Name");

                        EventDetailLink = getActivity().getSharedPreferences("EventDetailLink", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = EventDetailLink.edit();
                        editor.putString("ArtistName", ArtistLists);
                        editor.putString("EventName", EventName);
                        editor.putString("TicketUrl", TicketUrl);
                        editor.apply();

                        Detail_Artist.setText(ArtistLists);
                        Detail_Artist.setSelected(true);

                        Detail_Venue.setText(VenueName);
                        Detail_Venue.setSelected(true);

                        Detail_Date.setText(DateName);
                        Detail_Time.setText(TimeName);
                        Detail_Genre.setText(Genres);
                        Detail_Genre.setSelected(true);

                        Detail_Price.setText(Price);

                        if(TicketStatus.equals("On Sale")){
                            Detail_Ticket_Status.setText(TicketStatus);
                        }else if(TicketStatus.equals("Off Sale")){
                            Detail_Ticket_Status.setText(TicketStatus);
                            Detail_Ticket_Status.setBackgroundResource(R.drawable.red_oval);
                        }else{
                            Detail_Ticket_Status.setText(TicketStatus);
                            Detail_Ticket_Status.setBackgroundResource(R.drawable.orange_oval);
                        }

                        Detail_Ticket_URL.setText(TicketUrl);
                        Detail_Ticket_URL.setSelected(true);
                        Detail_Ticket_URL.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TicketUrl));
                                startActivity(intent);
                            }
                        });
                        Detail_Ticket_URL.setPaintFlags(Detail_Ticket_URL.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                        Picasso.get().load(SeatMapUrl).fit().into(SeatMap);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Detail_Bar.setVisibility(View.GONE);
                Detail_Parent.setVisibility(View.VISIBLE);
                Snackbar.make(view, "Event Details wrong", Snackbar.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        EventQueue.add(EventRequest);
    }
}