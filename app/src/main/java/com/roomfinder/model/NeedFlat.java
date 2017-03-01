package com.roomfinder.model;

import com.backendless.geo.GeoPoint;

import java.util.Date;

/**
 * Created by Aditya on 3/12/2016.
 */
public class NeedFlat {

    String userId;
    String userName;
    String currentFlatLocation;
    String needFlatLocation;
    int rentRange;
    String occupancy;
    String lookingFor;
    Date needToShiftDate;
    String phoneNumber;
    boolean shouldShowContactNumber;
    String description;
    GeoPoint currentLocationCoordinates;
    GeoPoint anotherLocationCoordinates;
    private String objectId;

    public NeedFlat() {
        // default constructor
    }

    // values initialization Constructor
    public NeedFlat(String objId, String uName, String cPlace, String nPlace, int rent, String occ, String looking,
                    Date needShift, String pNo, boolean contactShow, String desc, GeoPoint coord, GeoPoint anotherL0c) {
        this.userId = objId;
        this.userName = uName;
        this.currentFlatLocation = cPlace;
        this.needFlatLocation = nPlace;
        this.rentRange = rent;
        this.occupancy = occ;
        this.lookingFor = looking;
        this.needToShiftDate = needShift;
        this.phoneNumber = pNo;
        this.shouldShowContactNumber = contactShow;
        this.description = desc;
        this.currentLocationCoordinates = coord;
        this.anotherLocationCoordinates = anotherL0c;

    }

    public String getObjId() {
        return objectId;
    }

    public void setObjId(String objId) {
        this.objectId = objId;
    }

    public String getCurrentFlatLocation() {
        return currentFlatLocation;
    }

    public void setCurrentFlatLocation(String currentFlatLocation) {
        this.currentFlatLocation = currentFlatLocation;
    }


    public String getNeedFlatLocation() {
        return needFlatLocation;
    }

    public void setNeedFlatLocation(String needFlatLocation) {
        this.needFlatLocation = needFlatLocation;
    }

    public int getRentRange() {
        return rentRange;
    }

    public void setRentRange(int rentRange) {
        this.rentRange = rentRange;
    }

    public String getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(String occupancy) {
        this.occupancy = occupancy;
    }

    public String getLookingFor() {
        return lookingFor;
    }

    public void setLookingFor(String lookingFor) {
        this.lookingFor = lookingFor;
    }

    public Date getNeedToShiftDate() {
        return needToShiftDate;
    }

    public void setNeedToShiftDate(Date needToShiftDate) {
        this.needToShiftDate = needToShiftDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isShouldShowContactNumber() {
        return shouldShowContactNumber;
    }

    public void setShouldShowContactNumber(boolean shouldShowContactNumber) {
        this.shouldShowContactNumber = shouldShowContactNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public GeoPoint getCurrentLocationCoordinates() {
        return currentLocationCoordinates;
    }

    public void setCurrentLocationCoordinates(GeoPoint coordinated) {
        this.currentLocationCoordinates = coordinated;
    }

    public GeoPoint getAnotherLocationCoordinates() {
        return anotherLocationCoordinates;
    }

    public void setAnotherLocationCoordinates(GeoPoint anotherLocationCoordinates) {
        this.anotherLocationCoordinates = anotherLocationCoordinates;
    }
}
