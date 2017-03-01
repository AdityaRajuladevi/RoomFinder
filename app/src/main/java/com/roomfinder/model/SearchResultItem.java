package com.roomfinder.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aditya on 3/12/2016.
 */
public class SearchResultItem implements Parcelable {

    public static final Creator<SearchResultItem> CREATOR = new Creator<SearchResultItem>() {
        @Override
        public SearchResultItem[] newArray(int size) {
            return new SearchResultItem[size];
        }

        @Override
        public SearchResultItem createFromParcel(Parcel source) {
            return new SearchResultItem(source);
        }
    };
    private String objId;
    private String userName;
    private String userId;
    private String locationName;
    private int rent;
    private String occupancy;

    public SearchResultItem(Parcel source) {
        objId = source.readString();
        userId = source.readString();
        userName = source.readString();
        rent = source.readInt();
        locationName = source.readString();
        occupancy = source.readString();
    }

    public SearchResultItem(String objId, String userId, String userName, int rent, String location, String occupancy) {
        this.objId = objId;
        this.userId = userId;
        this.userName = userName;
        this.rent = rent;
        this.locationName = location;
        this.occupancy = occupancy;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String currentLocationName) {
        this.locationName = currentLocationName;
    }

    public int getRent() {
        return rent;
    }

    public void setRent(int rent) {
        this.rent = rent;
    }

    public String getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(String occupancy) {
        this.occupancy = occupancy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objId);
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeInt(rent);
        dest.writeString(locationName);
        dest.writeString(occupancy);
    }
}
