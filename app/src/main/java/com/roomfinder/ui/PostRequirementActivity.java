package com.roomfinder.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.backendless.Backendless;

import com.roomfinder.R;
import com.roomfinder.adapter.PostRequirementSectionsPagerAdapter;
import com.roomfinder.backendlessutils.Defaults;
import com.roomfinder.callbacks.FragmentInteractionListener;
import com.roomfinder.utils.SharedPreferenceManager;

public class PostRequirementActivity extends AppCompatActivity
        implements FragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_requirement);

        Backendless.setUrl(Defaults.SERVER_URL);
        Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);
        // initialize all the controls
        init();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.title_postRequirement));
        }
        PostRequirementSectionsPagerAdapter mPostRequirementSectionsPagerAdapter = new PostRequirementSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPostRequirementSectionsPagerAdapter);

        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this);
        if (sharedPreferenceManager.getUserHasHaveFlatRequest())
            mViewPager.setCurrentItem(0);
        else if (sharedPreferenceManager.getUserHasNeedFlatRequest())
            mViewPager.setCurrentItem(1);
        else
            mViewPager.setCurrentItem(0);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mPostRequirementSectionsPagerAdapter.checkToRemoveRequest(0);
        mPostRequirementSectionsPagerAdapter.checkToRemoveRequest(1);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentInteraction() {

    }

}
