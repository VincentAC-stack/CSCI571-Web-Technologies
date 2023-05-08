package com.txu.eventfinder.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.txu.eventfinder.R;
import com.txu.eventfinder.models.SearchCard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SearchFragment extends Fragment {
    Spinner myspinner;
    ArrayAdapter<CharSequence> adapter;
    String BackEndUrl = "https://hw8-nodejs-379800.wl.r.appspot.com/";
//    String BackEndUrl = "http://192.168.1.52:8080/";
    String IP_URL = "https://ipinfo.io/?token=5f3978d3111782";

    private String KwData = "";
    private String DisData = "";
    private String CatData = "";
    private String LocData = "";
    private ArrayList<SearchCard> searchData = new ArrayList<SearchCard>();
    private SharedPreferences UserInput;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        myspinner = (Spinner) getView().findViewById(R.id.cat_edit);
        adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(), R.array.cat, R.layout.spinner_text);
        myspinner.setAdapter(adapter);

        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.keyword_auto);
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        EditText distance_edit = view.findViewById(R.id.distance_edit);
        Spinner cat_edit = view.findViewById(R.id.cat_edit);
        SwitchCompat switchButton = view.findViewById(R.id.loc_switch);
        EditText loc_edit = view.findViewById(R.id.loc_edit);
        AppCompatButton clearButton = view.findViewById(R.id.clear_btn);
        AppCompatButton searchButton = view.findViewById(R.id.search_btn);

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String inputQuery = charSequence.toString();
                String url = BackEndUrl + "autocomplete?keyword=" + inputQuery;

                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String[] AutoTexts = new String[response.length()];
                        for (int index = 0; index < response.length(); index++) {
                            try {
                                AutoTexts[index] = response.getString(index);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), R.layout.my_dropdown_item, AutoTexts);
                        autoCompleteTextView.setThreshold(1);
                        autoCompleteTextView.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(view, "AutoText wrong", Snackbar.LENGTH_SHORT).show();
                    }
                });
                progressBar.setVisibility(View.VISIBLE);
                queue.add(request);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        autoCompleteTextView.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
            public void onDismiss() {
                // Hide progress bar when dropdown is dismissed
                progressBar.setVisibility(View.GONE);
            }
        });

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    loc_edit.setVisibility(View.GONE);

                    RequestQueue Locqueue = Volley.newRequestQueue(getActivity());
                    JsonObjectRequest Locrequest = new JsonObjectRequest(Request.Method.GET, IP_URL, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                LocData = response.getString("loc");
                                loc_edit.setText(LocData);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Snackbar.make(view, "Location wrong", Snackbar.LENGTH_SHORT).show();
                            error.printStackTrace();
                        }
                    });
                    Locqueue.add(Locrequest);
                } else {
                    loc_edit.setVisibility(View.VISIBLE);
                    loc_edit.setText("");
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCompleteTextView.setText("");
                distance_edit.setText("10");
                cat_edit.setSelection(0);
                loc_edit.setText("");
                switchButton.setChecked(false);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Input_Auto = autoCompleteTextView.getText().toString().trim();
                String Input_Loc = loc_edit.getText().toString().trim();

                if(Input_Auto.isEmpty() || Input_Loc.isEmpty()){
                    ContextThemeWrapper ctw = new ContextThemeWrapper(getActivity(), R.style.CustomSnackbarTheme);
                    Snackbar snackbar = Snackbar.make(ctw, view, "Please fill all fields", Snackbar.LENGTH_LONG);
                    TextView tv = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                    tv.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    KwData = Input_Auto;
                    DisData = distance_edit.getText().toString();
                    CatData = cat_edit.getSelectedItem().toString();
                    if(CatData.equals("All")){
                        CatData = "Default";
                    }
                    LocData = Input_Loc;

                    String SearchQuery = BackEndUrl + "search?input_kw=" + KwData + "&input_dist=" + DisData + "&input_cat=" + CatData + "&input_location=" + LocData;
                    System.out.println(SearchQuery);

                    UserInput = getActivity().getSharedPreferences("FormInput", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = UserInput.edit();
                    editor.putString("keyword", KwData);
                    editor.putString("distance", DisData);
                    editor.putString("cat", CatData);
                    editor.putString("loc", LocData);
                    editor.apply();

                    RequestQueue Searchqueue = Volley.newRequestQueue(getActivity());
                    JsonArrayRequest Searchrequest = new JsonArrayRequest(Request.Method.GET, SearchQuery, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            if(response.length() == 0){
                                NavController navController = Navigation.findNavController(view);
                                navController.navigate(R.id.action_searchFragment_to_searchDetail);
                            }else{
                                NavController navController = Navigation.findNavController(view);

                                searchData.clear();
                                for(int i = 0; i < response.length(); i ++){
                                    try {
                                        JSONObject SearchObj = (JSONObject) response.get(i);
                                        String date = "";
                                        String time24 = "";
                                        String time12 = "";
                                        if(SearchObj.getString("Date").split("\\s+").length == 1){
                                            date = SearchObj.getString("Date").split("\\s+")[0];

                                            String year = date.split("-")[0];
                                            String month = date.split("-")[1];
                                            String day = date.split("-")[2];

                                            date = month + "/" + day + "/" + year;
                                        }else{
                                            date = SearchObj.getString("Date").split("\\s+")[0];
                                            time24 = SearchObj.getString("Date").split("\\s+")[1];

                                            String year = date.split("-")[0];
                                            String month = date.split("-")[1];
                                            String day = date.split("-")[2];

                                            date = month + "/" + day + "/" + year;

                                            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
                                            SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a");
                                            try {
                                                Date date12 = inputFormat.parse(time24);
                                                time12 = outputFormat.format(date12);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        String icon = SearchObj.getString("Icon");
                                        String event = SearchObj.getString("Event");
                                        String genre = SearchObj.getString("Genre");
                                        String venue = SearchObj.getString("Venue");
                                        String index = SearchObj.getString("index");
                                        searchData.add(new SearchCard(icon, event, venue, genre, date, time12, index));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("SearchData", searchData);
                                navController.navigate(R.id.action_searchFragment_to_searchDetail, bundle);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Bundle bundle = new Bundle();
                            NavController navController = Navigation.findNavController(view);
                            bundle.putSerializable("NoRecord", "No Records found");
                            navController.navigate(R.id.action_searchFragment_to_searchDetail, bundle);
//                            Snackbar.make(view, "Search Data Error", Snackbar.LENGTH_SHORT).show();
//                            error.printStackTrace();
                        }
                    });
                    Searchqueue.add(Searchrequest);
                }
            }
        });

    }
}