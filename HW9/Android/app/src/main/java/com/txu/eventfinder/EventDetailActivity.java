package com.txu.eventfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.txu.eventfinder.adapters.EventViewPagerAdapter;

public class EventDetailActivity extends AppCompatActivity {
    TabLayout EventTabLayout;
    ViewPager2 EventViewPager2;
    EventViewPagerAdapter EventViewPagerAdapter;
    private String eventRow;
    private SharedPreferences EventDetailLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        statusbarcolor();
        EventTabLayout = findViewById(R.id.event_tab_layout);
        EventViewPager2 = findViewById(R.id.event_view_Pager);

        AppCompatButton Twitter_Btn = findViewById(R.id.Twitter_Btn);
        AppCompatButton Facebook_btn = findViewById(R.id.Facebook_btn);
        AppCompatButton event_heart_filled = findViewById(R.id.event_heart_filled);
        AppCompatButton event_heart_outline = findViewById(R.id.event_heart_outline);

        EventDetailLink = getSharedPreferences("EventDetailLink", Context.MODE_PRIVATE);
        String EventNameLink = EventDetailLink.getString("EventName","");
        String TicketUrl = EventDetailLink.getString("TicketUrl","");

        Twitter_Btn.setOnClickListener(new View.OnClickListener() {
            String TwitterApi = "https://twitter.com/compose/tweet?text=Check out " + EventNameLink + " on Ticketmaster! " + TicketUrl;
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(TwitterApi));
                startActivity(intent);
            }
        });

        Facebook_btn.setOnClickListener(new View.OnClickListener() {
            String FacebookApi = "https://www.facebook.com/sharer/sharer.php?u=" + TicketUrl;
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FacebookApi));
                startActivity(intent);
            }
        });

        event_heart_outline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event_heart_filled.setVisibility(View.VISIBLE);
                event_heart_outline.setVisibility(View.GONE);

                String showText = EventNameLink + " added to favorites";

                Snackbar.make(view, showText, Snackbar.LENGTH_SHORT).show();
            }
        });

        event_heart_filled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event_heart_filled.setVisibility(View.GONE);
                event_heart_outline.setVisibility(View.VISIBLE);

                String showText = EventNameLink + " removed from favorites";

                Snackbar.make(view, showText, Snackbar.LENGTH_SHORT).show();
            }
        });

        EventViewPagerAdapter = new EventViewPagerAdapter(this);
        EventViewPager2.setAdapter(EventViewPagerAdapter);

        TabLayout.Tab tab1 = EventTabLayout.getTabAt(0);
        TabLayout.Tab tab2 = EventTabLayout.getTabAt(1);
        TabLayout.Tab tab3 = EventTabLayout.getTabAt(2);
        int tabIconColor = ContextCompat.getColor(EventDetailActivity.this, R.color.white);
        int SelectedTabIconColor = ContextCompat.getColor(EventDetailActivity.this, R.color.ActionBarTitleColor);
        tab1.getIcon().setColorFilter(SelectedTabIconColor, PorterDuff.Mode.SRC_IN);
        tab2.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tab3.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

        Intent intent = getIntent();
        String EventName = intent.getStringExtra("EventName");
        eventRow = intent.getStringExtra("RowNum");

        Toolbar mToolbar = findViewById(R.id.event_toolbar);
        setSupportActionBar(mToolbar);

        TextView titleText = mToolbar.findViewById(R.id.Event_Title);
        getSupportActionBar().setTitle("");
        titleText.setText(EventName);
        titleText.setSelected(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.green_back_btn);

        EventTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                EventViewPager2.setCurrentItem(tab.getPosition());
                int tabIconColor = ContextCompat.getColor(EventDetailActivity.this, R.color.ActionBarTitleColor);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(EventDetailActivity.this, R.color.white);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        EventViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                EventTabLayout.getTabAt(position).select();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void statusbarcolor(){
        getWindow().setStatusBarColor(getResources().getColor(R.color.black, this.getTheme()));
    }

    public String getEventRow(){
        return eventRow;
    }
}