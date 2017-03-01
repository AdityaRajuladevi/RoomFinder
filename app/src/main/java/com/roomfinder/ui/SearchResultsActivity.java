package com.roomfinder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import com.roomfinder.R;
import com.roomfinder.adapter.SearchResultsSectionsPagerAdapter;
import com.roomfinder.callbacks.FragmentInteractionListener;
import com.roomfinder.model.SearchResultItem;
import com.roomfinder.utils.KeyUtils;

/**
 * Created by Aditya on 3/6/2016.
 */
public class SearchResultsActivity extends AppCompatActivity implements FragmentInteractionListener {

    private ViewPager mViewPager;
    private SearchResultsSectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent mIntent = getIntent();
        ArrayList<SearchResultItem> haveFlatsList = mIntent.getParcelableArrayListExtra(KeyUtils.KEY_HAVE_FLAT_RESULTS);
        ArrayList<SearchResultItem> needFLatsList = mIntent.getParcelableArrayListExtra(KeyUtils.KEY_NEED_FLAT_RESULTS);
        // initialize all the controls
        init(haveFlatsList, needFLatsList);
    }

    private void init(ArrayList<SearchResultItem> haveFlatsList, ArrayList<SearchResultItem> needFLatsList) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mSectionsPagerAdapter = new SearchResultsSectionsPagerAdapter(getSupportFragmentManager(), haveFlatsList, needFLatsList);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentInteraction() {

    }
}
