package com.roomfinder.messaging;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by admin on 4/18/16.
 */
public class ChatUser {
    private static ChatUser currentUser = new ChatUser();
    private String objectId;
    private String nickname;
    private String deviceId;

    public static ChatUser currentUser() {
        return currentUser;
    }

//    public String getUserId() {
//        return userId;
//    }
//
//    public void setUserId(String userId) {
//        this.userId = userId;
//    }

    //private String userId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


}