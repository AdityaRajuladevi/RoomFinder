package com.roomfinder.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.backendless.Backendless;

import java.util.ArrayList;

import com.roomfinder.R;
import com.roomfinder.adapter.ImagesGridAdapter;
import com.roomfinder.backendlessutils.Defaults;

public class PhotosFullScreenViewActivity extends AppCompatActivity {

    public static final String KEY_PHOTOS_URLS = "photosUrls";
    public static final String KEY_PHOTOS_CURRENT_PHOTO = "currentPhoto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_full_screen_view);

        Backendless.setUrl(Defaults.SERVER_URL);
        Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("RoomFinder");
        }

        Bundle extras = getIntent().getExtras();
        ArrayList<String> imageUrls = extras.getStringArrayList(KEY_PHOTOS_URLS);
        int currentPhotoPosition = extras.getInt(KEY_PHOTOS_CURRENT_PHOTO);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_photos_fullscreen);
        GridLayoutManager imagesGridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(imagesGridLayoutManager);
        recyclerView.setHasFixedSize(true);

        ImagesGridAdapter imagesGridAdapter = new ImagesGridAdapter(this, imageUrls, true);
        recyclerView.setAdapter(imagesGridAdapter);
        recyclerView.scrollToPosition(currentPhotoPosition);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
