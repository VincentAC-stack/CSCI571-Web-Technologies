package com.txu.eventfinder.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Camera;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class EventVenueFragment extends Fragment {

//    String BackEndUrl = "http://192.168.1.52:8080/";
    String BackEndUrl = "https://hw8-nodejs-379800.wl.r.appspot.com/";
    private String VenueName = "";
    private String Venue_Address = "";
    private String Venue_City = "";
    private String Venue_Contact = "";
    private String Venue_lat = "";
    private String Venue_lon = "";
    private String OpenHours = "";
    private String GeneralRules = "";
    private String ChildRules = "";
    private SharedPreferences UserInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_venue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView Venue_Detail_Name = view.findViewById(R.id.Venue_Detail_Name);
        TextView Venue_Detail_Address = view.findViewById(R.id.Venue_Detail_Address);
        TextView Venue_Detail_City = view.findViewById(R.id.Venue_Detail_City);
        TextView Venue_Detail_Contact = view.findViewById(R.id.Venue_Detail_Contact);
        ProgressBar Venue_Bar = view.findViewById(R.id.Venue_Bar);
        NestedScrollView Venue_Detail_NestParent = view.findViewById(R.id.Venue_Detail_NestParent);
        TextView Venue_Detail_OpenHour = view.findViewById(R.id.Venue_Detail_OpenHour);
        TextView Venue_Detail_GeneralRule = view.findViewById(R.id.Venue_Detail_GeneralRule);
        TextView Venue_Detail_ChildRule = view.findViewById(R.id.Venue_Detail_ChildRule);

        UserInput = getActivity().getSharedPreferences("FormInput", Context.MODE_PRIVATE);
        String keyword = UserInput.getString("keyword","");
        String dist = UserInput.getString("distance","");
        String cat = UserInput.getString("cat","");
        String loc = UserInput.getString("loc","");

        EventDetailActivity EventActivity = (EventDetailActivity)getActivity();
        String eventRow = EventActivity.getEventRow();

        String VenueQuery = BackEndUrl + "venue?input_kw=" + keyword + "&input_dist=" + dist + "&input_cat=" + cat + "&input_location=" + loc + "&Row=" + eventRow;
        System.out.println(VenueQuery);

        Venue_Bar.setVisibility(View.VISIBLE);
        Venue_Detail_NestParent.setVisibility(View.GONE);

        RequestQueue VenueQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest VenueRequest = new JsonObjectRequest(Request.Method.GET, VenueQuery, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Venue_Bar.setVisibility(View.GONE);
                Venue_Detail_NestParent.setVisibility(View.VISIBLE);
                try {
                    VenueName = response.getString("Venue_Name");
                    Venue_Address = response.getString("Venue_Address");
                    Venue_City = response.getString("Venue City & State");
                    Venue_Contact = response.getString("Venue_Phone");
                    Venue_lat = response.getString("Venue_lat");
                    Venue_lon = response.getString("Venue_lon");
                    OpenHours = response.getString("Open_hour");
                    GeneralRules = response.getString("General_rule");
                    ChildRules = response.getString("Child_rule");

                    Venue_Detail_Name.setText(VenueName);
                    Venue_Detail_Name.setSelected(true);

                    Venue_Detail_Address.setText(Venue_Address);
                    Venue_Detail_Address.setSelected(true);

                    Venue_Detail_City.setText(Venue_City);
                    Venue_Detail_City.setSelected(true);

                    Venue_Detail_Contact.setText(Venue_Contact);
                    Venue_Detail_Contact.setSelected(true);

                    SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.Venue_Detail_Map);
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng venueLoc = new LatLng(Double.parseDouble(Venue_lat), Double.parseDouble(Venue_lon));
                            googleMap.addMarker(new MarkerOptions().position(venueLoc).title("Marker in Venue"));
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(venueLoc, 15);
                            googleMap.animateCamera(cameraUpdate);
                        }
                    });

                    Venue_Detail_OpenHour.setText(OpenHours);
                    Venue_Detail_GeneralRule.setText(GeneralRules);
                    Venue_Detail_ChildRule.setText(ChildRules);

                    Venue_Detail_OpenHour.setOnClickListener(new View.OnClickListener() {
                        boolean isExpanded = false;
                        @Override
                        public void onClick(View view) {
                            if(isExpanded){
                                Venue_Detail_OpenHour.setMaxLines(3);
                                isExpanded = false;
                            } else {
                                Venue_Detail_OpenHour.setMaxLines(Integer.MAX_VALUE);
                                isExpanded = true;
                            }
                        }
                    });

                    Venue_Detail_GeneralRule.setOnClickListener(new View.OnClickListener() {
                        boolean isExpanded = false;
                        @Override
                        public void onClick(View view) {
                            if(isExpanded){
                                Venue_Detail_GeneralRule.setMaxLines(3);
                                isExpanded = false;
                            } else {
                                Venue_Detail_GeneralRule.setMaxLines(Integer.MAX_VALUE);
                                isExpanded = true;
                            }
                        }
                    });

                    Venue_Detail_ChildRule.setOnClickListener(new View.OnClickListener() {
                        boolean isExpanded = false;
                        @Override
                        public void onClick(View view) {
                            if(isExpanded){
                                Venue_Detail_ChildRule.setMaxLines(3);
                                isExpanded = false;
                            } else {
                                Venue_Detail_ChildRule.setMaxLines(Integer.MAX_VALUE);
                                isExpanded = true;
                            }
                        }
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Venue_Bar.setVisibility(View.GONE);
                Venue_Detail_NestParent.setVisibility(View.VISIBLE);
                Snackbar.make(view, "Venue Details wrong", Snackbar.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });
        VenueQueue.add(VenueRequest);
    }
}