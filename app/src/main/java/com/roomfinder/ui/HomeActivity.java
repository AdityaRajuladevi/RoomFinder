package com.roomfinder.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.Subscription;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.Message;
import com.backendless.messaging.SubscriptionOptions;
import com.backendless.persistence.BackendlessDataQuery;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.List;

import com.roomfinder.R;
import com.roomfinder.adapter.HomeSectionsPagerAdapter;
import com.roomfinder.backendlessutils.DefaultCallback;
import com.roomfinder.backendlessutils.Defaults;
import com.roomfinder.callbacks.FragmentInteractionListener;
import com.roomfinder.database.DataBaseHelper;
import com.roomfinder.messaging.ChatMessage;
import com.roomfinder.messaging.ChatUser;
import com.roomfinder.utils.KeyUtils;
import com.roomfinder.utils.Logger;
import com.roomfinder.utils.SharedPreferenceManager;
import com.roomfinder.utils.Util;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentInteractionListener {

    public static final int HOME_POST_REQUEST_CODE = 1001;
    public static final int HOME_POST_RESULT_CODE = 1002;
    private String userObjId;
    private boolean isProfilePictureAvailable;
    private SharedPreferenceManager sharedPreferenceManager;
    private Subscription subscription;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Backendless.setUrl(Defaults.SERVER_URL);
        Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);


        // Logger.v(" Home Activity ");
        sharedPreferenceManager = new SharedPreferenceManager(this);
        userObjId = sharedPreferenceManager.getObjectIdOFLoggedInUser();
        isProfilePictureAvailable = sharedPreferenceManager.getIsProfilePicAvailable();

        // initialize all the controls
        initMessageSubscriptions();
        init();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription != null)
            subscription.cancelSubscription();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (subscription != null) {
            subscription.resumeSubscription();
        }
        // Logger.v("Home Activity Resumed "+ subscription);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (subscription != null)
            subscription.pauseSubscription();
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.getMenu().getItem(0).setChecked(true);

        if (sharedPreferenceManager.getUserHasHaveFlatRequest() || sharedPreferenceManager.getUserHasNeedFlatRequest())
            navigationView.getMenu().getItem(1).setTitle("Edit Requirement");

        ImageView profileImageView = (ImageView) headerView.findViewById(R.id.profileImageView);
        if (isProfilePictureAvailable)
            Picasso.with(HomeActivity.this).load(Defaults.FILES_PROFILE_PIC_URL + "/" + userObjId + ".png").into(profileImageView);

        TextView profile_name = (TextView) headerView.findViewById(R.id.profile_name);
        String name = sharedPreferenceManager.getNameOFLoggedInUser();
        if (name != null)
            profile_name.setText(name);
        navigationView.setNavigationItemSelectedListener(this);

        HomeSectionsPagerAdapter mHomeSectionsPagerAdapter = new HomeSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mHomeSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        // register your device to receive messages from Backendless..
        if (Util.isNetworkAvailable(this)) {

            Backendless.Messaging.registerDevice(Defaults.GOOGLE_PROJECT_ID, Defaults.DEFAULT_CHANNEL, new AsyncCallback<Void>() {
                @Override
                public void handleResponse(Void response) {
                    Log.v(Defaults.GCM_TAG, "=  Registered");
                    // sharedPreferenceManager.saveDeviceRegistrationStatus(true);
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.v(Defaults.GCM_TAG, fault.toString());
                }
            });
            // save the chat user instance
            saveChatUserAsCurrentUser(name);
        }

    }


    private void setNavigationMenuRequirementStatus() {
        navigationView.getMenu().getItem(0).setChecked(true);

        if (sharedPreferenceManager.getUserHasHaveFlatRequest() || sharedPreferenceManager.getUserHasNeedFlatRequest())
            navigationView.getMenu().getItem(1).setTitle("Edit Requirement");
    }

    private void saveChatUserAsCurrentUser(String uName) {
        String deviceId = Build.SERIAL;

        if (deviceId.isEmpty()) {
            Log.v(Defaults.GCM_TAG, "Could not retrieve DEVICE ID");
            //Toast.makeText(this, "Could not retrieve DEVICE ID", Toast.LENGTH_SHORT).show();
            return;
        }

        ChatUser.currentUser().setNickname(uName);
        ChatUser.currentUser().setDeviceId(deviceId);

        BackendlessDataQuery backendlessDataQuery = new BackendlessDataQuery();
        String whereClause = "ownerId =" + "'" + sharedPreferenceManager.getObjectIdOFLoggedInUser() + "'";
        //"nickname='" + ChatUser.currentUser().getNickname() + "'"
        backendlessDataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(ChatUser.class).find(backendlessDataQuery,
                new AsyncCallback<BackendlessCollection<ChatUser>>() {

                    @Override
                    public void handleResponse(BackendlessCollection<ChatUser> chatUserBackendlessCollection) {

                        if (chatUserBackendlessCollection.getCurrentPage().isEmpty()) {
                            Backendless.Persistence.of(ChatUser.class).save(ChatUser.currentUser(), new AsyncCallback<ChatUser>() {
                                @Override
                                public void handleResponse(ChatUser response) {
                                    ChatUser.currentUser().setObjectId(response.getObjectId());
                                    // ChatUser.currentUser().setUserId(response.getUserId());
                                    Log.v(Defaults.GCM_TAG, "Chat User Details after save " + response.getNickname());
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    Log.v(Defaults.GCM_TAG, "Chat User Save Error " + backendlessFault.toString());
                                }
                            });
                        } else {
                            ChatUser foundUser = chatUserBackendlessCollection.getCurrentPage().iterator().next();
                            ChatUser.currentUser().setObjectId(foundUser.getObjectId());
                            if (!ChatUser.currentUser().getDeviceId().equals(foundUser.getDeviceId())) {
                                Backendless.Persistence.of(ChatUser.class).save(ChatUser.currentUser(), new AsyncCallback<ChatUser>() {
                                    @Override
                                    public void handleResponse(ChatUser response) {
                                        Log.v(Defaults.GCM_TAG, "Chat User Details updated to new device id " + response.getNickname());
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault backendlessFault) {
                                        Log.v(Defaults.GCM_TAG, " update chat user Error " + backendlessFault.toString());
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        String notExistingTableErrorCode = "1009";
                        if (backendlessFault.getCode().equals(notExistingTableErrorCode)) {
                            Log.v(Defaults.GCM_TAG, "Chat User Details table error");
                            Backendless.Persistence.of(ChatUser.class).save(ChatUser.currentUser(), new DefaultCallback<ChatUser>(HomeActivity.this) {
                                @Override
                                public void handleResponse(ChatUser response) {
                                    super.handleResponse(response);
                                    ChatUser.currentUser().setObjectId(response.getObjectId());
                                    Log.v(Defaults.GCM_TAG, "Chat User Details after save " + response.getNickname());
                                }
                            });
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_post_requirement: // post req
                Intent intent = new Intent(this, PostRequirementActivity.class);
                startActivityForResult(intent, HOME_POST_REQUEST_CODE);
                break;

            case R.id.nav_edit_preferences: // Edit Preferences
                item.setChecked(false);
                item.setCheckable(false);
                Intent i = new Intent(this, PreferencesActivity.class);
                i.putExtra(KeyUtils.KEY_PREFERENCE_TYPE, KeyUtils.PREFERENCE_UPDATE);
                startActivity(i);
                break;

            case R.id.nav_logout:
                // logout the user

                if (Util.isNetworkAvailable(this)) {
                    Backendless.UserService.logout(new DefaultCallback<Void>(HomeActivity.this, "Logging Out...") {
                        public void handleResponse(Void response) {
                            // user has been logged out.
                            super.handleResponse(response);
                            //clear shared prefs
                            sharedPreferenceManager.logoutUser();
                        }

                        public void handleFault(BackendlessFault fault) {
                            // something went wrong and logout failed, to get the error code call fault.getCode()
                        }
                    });
                } else
                    Util.showToast(this, getResources().getString(R.string.network_error));
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HOME_POST_REQUEST_CODE) {
            //Logger.v("Returned From Post "+resultCode);
            setNavigationMenuRequirementStatus();
        }
    }

    private void initMessageSubscriptions() {
        String userObjId = sharedPreferenceManager.getObjectIdOFLoggedInUser();
        SubscriptionOptions subscriptionOptions = new SubscriptionOptions();
        subscriptionOptions.setSelector("to='" + userObjId + "'");

        Backendless.Messaging.subscribe(Defaults.DEFAULT_CHANNEL, new AsyncCallback<List<Message>>() {
            @Override
            public void handleResponse(List<Message> response) {
                onReceiveMessage(response);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Logger.v(Defaults.GCM_TAG, " Subscription Failed " + fault.toString());
                Toast.makeText(HomeActivity.this, fault.getMessage()

                        , Toast.LENGTH_SHORT).show();
            }
        }, subscriptionOptions, new AsyncCallback<Subscription>() {
            @Override
            public void handleResponse(Subscription response) {
                Log.v(Defaults.GCM_TAG, "Subscription Response " + response);
                subscription = response;
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Logger.v(Defaults.GCM_TAG, " Subscription response failed " + backendlessFault.toString());
            }
        });
    }

    private void onReceiveMessage(List<Message> messagesList) {
        DataBaseHelper dbHelper = new DataBaseHelper(this);
        dbHelper.openDB();
        for (Message message : messagesList) {
            ChatMessage msg = (ChatMessage) message.getData();
            msg.setMessageId(message.getMessageId());

            String decodedMessage = StringEscapeUtils.unescapeJava(msg.getMessage());
            // Logger.v("MessageDecoded",decodedMessage);
            msg.setMessage(decodedMessage);

            String msgMapId = dbHelper.checkForUniqueMsgIdForCombination(msg.getFromId(), msg.getToId());
            if (msgMapId == null) {
                msgMapId = dbHelper.getUniqueMapIdForCombination(msg.getFromId(), msg.getToId());
            }
            msg.setMessageMapId(msgMapId);
            long k = dbHelper.addMessage(msg);
            sharedPreferenceManager.saveMessageReceivedStatus(true);
            Logger.v("Message", "Added in Home Activity " + message.getMessageId() + " Added status = " + k);
        }
        dbHelper.closeDB();
    }

}
