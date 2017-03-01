package com.roomfinder.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.UUID;

import com.roomfinder.messaging.ChatMessage;
import com.roomfinder.utils.Logger;

/**
 * Created by admin on 4/18/16.
 */
public class DataBaseHelper {


    public static final String DB_NAME = "RoomFinder.db";
    public static final String TABLE_MESSAGES = "RoomFinder_Messages";
    public static final String TABLE_MESSAGES_MAPPING = "RoomFinder_Messages_Mapping";
    public static final String DROP_MESSAGES_TBL = "DROP TABLE IF EXISTS " + TABLE_MESSAGES;
    public static final String DROP_MESSAGES_MAPPING_TBL = "DROP TABLE IF EXISTS " + TABLE_MESSAGES_MAPPING;
    public static final int VER = 4;
    private static final String KEY_MESSAGES_MAPPING_ID = "msgMapId";
    private static final String KEY_MESSAGES_ID = "msgId";
    private static final String KEY_MESSAGES_FROM_ID = "msgFromId";
    private static final String KEY_MESSAGES_TO_ID = "msgToId";
    public static final String CREATE_MESSAGES_MAPPING_TABLE = " CREATE TABLE " + TABLE_MESSAGES_MAPPING
            + " (" + KEY_MESSAGES_MAPPING_ID + " TEXT UNIQUE, " + KEY_MESSAGES_FROM_ID + " TEXT , " + KEY_MESSAGES_TO_ID + " TEXT)";
    private static final String KEY_MESSAGES_COMPANION_NAME = "msgOwner";
    private static final String KEY_MESSAGES_TEXT = "msgText";

    private static final String KEY_MESSAGES_IS_SELF = "msgSelf";
    public static final String[] MESSAGE_ALL_FIELDS = new String[]{KEY_MESSAGES_ID, KEY_MESSAGES_FROM_ID,
            KEY_MESSAGES_TO_ID, KEY_MESSAGES_MAPPING_ID, KEY_MESSAGES_TEXT, KEY_MESSAGES_IS_SELF, KEY_MESSAGES_COMPANION_NAME};
    private static final String KEY_MESSAGES_CREATED_AT = "created_at";

    public static final String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES
            + " (" + KEY_MESSAGES_ID + " TEXT UNIQUE, " + KEY_MESSAGES_FROM_ID + " TEXT , "
            + KEY_MESSAGES_TO_ID + " TEXT , " + KEY_MESSAGES_MAPPING_ID + " TEXT,"
            + KEY_MESSAGES_TEXT + " TEXT, " + KEY_MESSAGES_IS_SELF + " INTEGER, "
            + KEY_MESSAGES_COMPANION_NAME + " TEXT, "
            + KEY_MESSAGES_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP  )";
    private SQLiteDatabase db;
    private MyDBOpenHelper myDBOpenHelper;

    public DataBaseHelper(Context context) {
        myDBOpenHelper = new MyDBOpenHelper(context);
    }

    public long addMessage(String messageId, String msgFromId, String msgToId, String msgText) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_MESSAGES_ID, messageId);
        contentValues.put(KEY_MESSAGES_FROM_ID, msgFromId);
        contentValues.put(KEY_MESSAGES_TO_ID, msgToId);
        contentValues.put(KEY_MESSAGES_TEXT, msgText);
        return db.insert(TABLE_MESSAGES, null, contentValues);
    }

    public long addMessage(ChatMessage message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_MESSAGES_ID, message.getMessageId());
        contentValues.put(KEY_MESSAGES_FROM_ID, message.getFromId());
        contentValues.put(KEY_MESSAGES_TO_ID, message.getToId());
        contentValues.put(KEY_MESSAGES_MAPPING_ID, message.getMessageMapId());
        contentValues.put(KEY_MESSAGES_TEXT, message.getMessage().trim());
        contentValues.put(KEY_MESSAGES_IS_SELF, message.isSelf());
        contentValues.put(KEY_MESSAGES_COMPANION_NAME, message.getCompanionName());
        return db.insert(TABLE_MESSAGES, null, contentValues);
    }

    public ArrayList<ChatMessage> getAllMessagesForId(String toId) {
        Cursor cursor = db.query(TABLE_MESSAGES, MESSAGE_ALL_FIELDS, KEY_MESSAGES_TO_ID + " = ? ", new String[]{toId}, null, null, KEY_MESSAGES_CREATED_AT + " DESC");
        ArrayList<ChatMessage> msgsList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            ChatMessage eachItem = new ChatMessage();
            eachItem.setMessageId(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_ID)));
            eachItem.setFromId(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_FROM_ID)));
            eachItem.setToId(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_TO_ID)));
            eachItem.setMessage(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_TEXT)));
            eachItem.setSelf(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_IS_SELF)));
            msgsList.add(eachItem);
            cursor.moveToNext();
        }
        cursor.close();
        return msgsList;
    }

    public ArrayList<ChatMessage> getAllMessagesForChat(String toId, String fromId) {
        String query = "select * from " + TABLE_MESSAGES + " where " + KEY_MESSAGES_FROM_ID + " IN ('" + fromId + "','" + toId + "')  AND " +
                KEY_MESSAGES_TO_ID + " IN ('" + fromId + "','" + toId + "');";
        Cursor cursor = db.rawQuery(query, null);
        // Cursor cursor = db.query(TABLE_MESSAGES, MESSAGE_ALL_FIELDS, KEY_MESSAGES_TO_ID + " = ? ", new String[]{toId}, null, null, KEY_MESSAGES_CREATED_AT + " DESC");
        ArrayList<ChatMessage> msgsList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            ChatMessage eachItem = new ChatMessage();
            eachItem.setMessageId(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_ID)));
            eachItem.setFromId(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_FROM_ID)));
            eachItem.setToId(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_TO_ID)));
            eachItem.setMessageMapId(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_MAPPING_ID)));
            eachItem.setMessage(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_TEXT)));
            // Logger.v("MessageDB",eachItem.getMessage());
            eachItem.setSelf(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_IS_SELF)));
            eachItem.setCompanionName(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_COMPANION_NAME)));
            msgsList.add(eachItem);
            cursor.moveToNext();
        }
        cursor.close();
        return msgsList;
    }

    public ArrayList<ChatMessage> getRecentMessageFromUsers() {
        String query = "select * from " + TABLE_MESSAGES + " group by " + KEY_MESSAGES_MAPPING_ID + ";";
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<ChatMessage> msgsList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            ChatMessage eachItem = new ChatMessage();
            eachItem.setMessageId(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_ID)));
            eachItem.setFromId(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_FROM_ID)));
            eachItem.setToId(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_TO_ID)));
            eachItem.setMessageMapId(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_MAPPING_ID)));
            eachItem.setMessage(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_TEXT)));
            eachItem.setSelf(cursor.getInt(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_IS_SELF)));
            eachItem.setCompanionName(cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_COMPANION_NAME)));
            msgsList.add(eachItem);
            cursor.moveToNext();
        }
        cursor.close();
        return msgsList;
    }

    public String getUniqueMapIdForCombination(String fromId, String toId) {
        ContentValues contentValues = new ContentValues();
        String uniqueID = UUID.randomUUID().toString();
        contentValues.put(KEY_MESSAGES_MAPPING_ID, uniqueID);
        contentValues.put(KEY_MESSAGES_FROM_ID, fromId);
        contentValues.put(KEY_MESSAGES_TO_ID, toId);
        db.insert(TABLE_MESSAGES_MAPPING, null, contentValues);
        return uniqueID;
    }

    public String checkForUniqueMsgIdForCombination(String fromId, String toId) {
        String query = "select " + KEY_MESSAGES_MAPPING_ID + " from " + TABLE_MESSAGES_MAPPING + " where " + KEY_MESSAGES_FROM_ID + " IN ('" + fromId + "','" + toId + "')  AND " +
                KEY_MESSAGES_TO_ID + " IN ('" + fromId + "','" + toId + "');";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) {
            cursor.close();
            return null;
        } else {
            String id = cursor.getString(cursor.getColumnIndex(DataBaseHelper.KEY_MESSAGES_MAPPING_ID));
            cursor.close();
            return id;
        }
    }

    public void removeMessages(String msgMapId) {
        // String query="delete from "+ TABLE_MESSAGES_MAPPING + " where "+ KEY_MESSAGES_MAPPING_ID +" = '"+msgMapId+"' ;";
        Logger.v("Rows Deleted For chat = " + db.delete(TABLE_MESSAGES, KEY_MESSAGES_MAPPING_ID + "=?", new String[]{msgMapId}));
    }

    public void openDB() throws SQLiteException {
        db = myDBOpenHelper.getWritableDatabase();
    }

    public void closeDB() {
        db.close();
    }


    private class MyDBOpenHelper extends SQLiteOpenHelper {
        public MyDBOpenHelper(Context context) {
            super(context, DB_NAME, null, VER);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_MESSAGES_TABLE);
            db.execSQL(CREATE_MESSAGES_MAPPING_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                db.execSQL(DROP_MESSAGES_TBL);
                db.execSQL(DROP_MESSAGES_MAPPING_TBL);
                onCreate(db);
            }
        }
    }

}