package com.roomfinder.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.Subscription;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.Message;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.backendless.messaging.PublishStatusEnum;
import com.backendless.messaging.SubscriptionOptions;
import com.backendless.persistence.BackendlessDataQuery;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.roomfinder.R;
import com.roomfinder.backendlessutils.DefaultCallback;
import com.roomfinder.backendlessutils.Defaults;
import com.roomfinder.database.DataBaseHelper;
import com.roomfinder.messaging.ChatMessage;
import com.roomfinder.messaging.ChatUser;
import com.roomfinder.messaging.MessagesAdapter;
import com.roomfinder.utils.Logger;
import com.roomfinder.utils.SharedPreferenceManager;
import com.roomfinder.utils.Util;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

/**
 * Created by admin on 4/18/16.
 */
public class UserChatActivity extends AppCompatActivity {

    private RecyclerView messagesRecyclerList;
    private EmojiconEditText messageField;
    private ImageView emojiButton;
    private EmojIconActions emojIcon;
    private View rootView;
    private ChatUser chatCompanion;
    private String fromObjId, toObjId;
    private DataBaseHelper dataBaseHelper;
    private MessagesAdapter messagesAdapter;
    private Subscription subscription;
    private String fromNickName;
    private SharedPreferenceManager sharedPreferenceManager;
    private TextView chatPersonName;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Backendless.setUrl(Defaults.SERVER_URL);
        Backendless.initApp(this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        chatPersonName = (TextView) toolbar.findViewById(R.id.chat_user_name);

        sharedPreferenceManager = new SharedPreferenceManager(this);
        fromNickName = sharedPreferenceManager.getNameOFLoggedInUser();
        fromObjId = sharedPreferenceManager.getObjectIdOFLoggedInUser();
        dataBaseHelper = new DataBaseHelper(this);
        toObjId = getIntent().getStringExtra(Defaults.KEY_COMPANION_ID);

        if (Util.isNetworkAvailable(this)) {
            if (toObjId != null && !toObjId.isEmpty()) {
                fetchCompanionObject(toObjId);
                initMessageSubscriptions();
            }
        } else {
            Logger.v("No Internet ");
        }
        initUI();
        Logger.v("User Chat Activity created");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.v("destroyed");
        if (subscription != null)
            subscription.cancelSubscription();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.v("resumed");
        if (subscription != null)
            subscription.resumeSubscription();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.v("paused");
        if (subscription != null)
            subscription.pauseSubscription();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_activity_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clearChat:
                clearChat();
                return true;
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearChat() {
        dataBaseHelper.openDB();
        String msgMapId = dataBaseHelper.checkForUniqueMsgIdForCombination(fromObjId, toObjId);
        dataBaseHelper.closeDB();

        if (msgMapId == null) {
            dataBaseHelper.openDB();
            msgMapId = dataBaseHelper.getUniqueMapIdForCombination(fromObjId, toObjId);
            dataBaseHelper.closeDB();
        }
        // delete the corresponding records based on the msgMapId
        dataBaseHelper.openDB();
        dataBaseHelper.removeMessages(msgMapId);
        dataBaseHelper.closeDB();

        if (messagesAdapter != null) {
            ArrayList<ChatMessage> msgs = messagesAdapter.getMessagesList();
            msgs.clear();
            messagesAdapter.notifyDataSetChanged();
            sharedPreferenceManager.saveMessageReceivedStatus(true);
        }
    }

    private void fetchCompanionObject(final String companionId) {
        BackendlessDataQuery backendlessDataQuery = new BackendlessDataQuery();
        String whereClause = "ownerId =" + "'" + companionId + "'";
        backendlessDataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(ChatUser.class).find(backendlessDataQuery,
                new DefaultCallback<BackendlessCollection<ChatUser>>(this, " Loading Chat Please Wait..") {
                    @Override
                    public void handleResponse(BackendlessCollection<ChatUser> chatUserBackendlessCollection) {
                        if (!chatUserBackendlessCollection.getCurrentPage().isEmpty()) {
                            chatCompanion = chatUserBackendlessCollection.getCurrentPage().iterator().next();
                            chatPersonName.setText(chatCompanion.getNickname());
                        } else {
                            chatCompanion = null;
                        }

                        // initializing the adapter with previous messages
                        setPreviousMessagesIntoList();
                        super.handleResponse(chatUserBackendlessCollection);
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        chatCompanion = null;
                        // initializing the adapter with previous messages
                        setPreviousMessagesIntoList();
                        super.handleFault(backendlessFault);
                    }
                });
    }

    private void initUI() {

        rootView = (View) findViewById(R.id.root_view);

        messagesRecyclerList = (RecyclerView) findViewById(R.id.msgs_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        // manager.setStackFromEnd(true);
        // manager.setReverseLayout(true);
        messagesRecyclerList.setLayoutManager(manager);

        messageField = (EmojiconEditText) findViewById(R.id.inputMsg);

        emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        emojIcon = new EmojIconActions(this, rootView, messageField, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.v("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.v("Keyboard", "close");
            }
        });

        Button sendBtn = (Button) findViewById(R.id.btnMsgSend);

        if (sendBtn != null)
            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = messageField.getText().toString();
                    // Logger.v("Message",message);
                    if (!TextUtils.isEmpty(message))
                        if (chatCompanion != null)
                            sendMessageToUser(message);
                        else
                            Toast.makeText(UserChatActivity.this, " Recipient User Not Found ", Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(UserChatActivity.this, " Cannot Send Empty Message ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void setPreviousMessagesIntoList() {
        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        if (fromObjId != null && !fromObjId.isEmpty()) {
            dataBaseHelper.openDB();
            chatMessages.addAll(dataBaseHelper.getAllMessagesForChat(toObjId, fromObjId));
            dataBaseHelper.closeDB();
        }
        messagesAdapter = new MessagesAdapter(chatMessages);
        messagesRecyclerList.setAdapter(messagesAdapter);
        messagesRecyclerList.scrollToPosition(chatMessages.size() - 1);
    }

    private void sendMessageToUser(String message) {
        PublishOptions publishOptions = new PublishOptions();
        publishOptions.putHeader("to", toObjId);
        final ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFromId(fromObjId);
        chatMessage.setToId(toObjId);
        String encodedMessage = StringEscapeUtils.escapeJava(message);
        chatMessage.setMessage(encodedMessage);
        //Logger.v("Message Encoding",encodedMessage);
        // 0 for false
        chatMessage.setSelf(0);
        chatMessage.setCompanionName(fromNickName);

        Backendless.Messaging.publish(chatMessage, publishOptions, new AsyncCallback<MessageStatus>() {
            @Override
            public void handleResponse(MessageStatus response) {
                PublishStatusEnum messageStatus = response.getStatus();
                if (messageStatus == PublishStatusEnum.SCHEDULED) {
                    chatMessage.setMessageId(response.getMessageId());
                    // message sent
                    messageField.setText(" ");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(
                            messageField.getWindowToken(), 0);

                    chatMessage.setSelf(1);
                    chatMessage.setCompanionName(chatCompanion.getNickname());
                    String decodedMessage = StringEscapeUtils.unescapeJava(chatMessage.getMessage());
                    chatMessage.setMessage(decodedMessage);

                    dataBaseHelper.openDB();
                    String msgMapId = dataBaseHelper.checkForUniqueMsgIdForCombination(fromObjId, toObjId);
                    if (msgMapId == null) {
                        msgMapId = dataBaseHelper.getUniqueMapIdForCombination(fromObjId, toObjId);
                    }
                    chatMessage.setMessageMapId(msgMapId);
                    dataBaseHelper.addMessage(chatMessage);
                    dataBaseHelper.closeDB();

                    sharedPreferenceManager.saveMessageReceivedStatus(true);
                    ArrayList<ChatMessage> msgs = messagesAdapter.getMessagesList();
                    msgs.add(chatMessage);
                    messagesAdapter.notifyDataSetChanged();
                    messagesRecyclerList.scrollToPosition(msgs.size() - 1);
                } else {
                    Log.v(Defaults.GCM_TAG, "Not Scheduled Companion Publish Message Status " + messageStatus.toString());
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Log.v(Defaults.GCM_TAG, "Failed to send message " + backendlessFault.toString());
            }
        });
    }


    private void initMessageSubscriptions() {
        SubscriptionOptions subscriptionOptions = new SubscriptionOptions();
        subscriptionOptions.setSelector("to='" + fromObjId + "'");

        Backendless.Messaging.subscribe(Defaults.DEFAULT_CHANNEL, new AsyncCallback<List<Message>>() {
            @Override
            public void handleResponse(List<Message> response) {
                onReceiveMessage(response);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Logger.v(Defaults.GCM_TAG, " Subscription Failed " + fault.toString());
                Toast.makeText(UserChatActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
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
                Logger.v(Defaults.GCM_TAG, " Subscription response code = " + backendlessFault.getCode());
            }
        });
    }


    private void onReceiveMessage(List<Message> messagesList) {
        for (Message message : messagesList) {
            ChatMessage msg = (ChatMessage) message.getData();
            msg.setMessageId(message.getMessageId());
            String decodedMessage = StringEscapeUtils.unescapeJava(msg.getMessage());
            msg.setMessage(decodedMessage);
            dataBaseHelper.openDB();
            String msgMapId = dataBaseHelper.checkForUniqueMsgIdForCombination(fromObjId, toObjId);
            dataBaseHelper.closeDB();

            if (msgMapId == null) {
                dataBaseHelper.openDB();
                msgMapId = dataBaseHelper.getUniqueMapIdForCombination(fromObjId, toObjId);
                dataBaseHelper.closeDB();
            }
            msg.setMessageMapId(msgMapId);

            dataBaseHelper.openDB();
            long result = dataBaseHelper.addMessage(msg);
            dataBaseHelper.closeDB();

            if (result == -1) {
                // error occured i.e message is being repeated.. unique key violation
                Logger.v("Message", "Added in Chat Activity " + message.getMessageId() + " is already present in db ");
            } else {
                // message added successfully into the database
                if (messagesAdapter != null) {
                    ArrayList<ChatMessage> adapterMessagesList = messagesAdapter.getMessagesList();
                    adapterMessagesList.add(msg);
                    messagesAdapter.notifyDataSetChanged();
                    messagesRecyclerList.scrollToPosition(adapterMessagesList.size() - 1);
                }
                sharedPreferenceManager.saveMessageReceivedStatus(true);
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
