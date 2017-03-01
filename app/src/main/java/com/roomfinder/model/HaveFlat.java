package com.roomfinder.model;

/**
 * Created by Aditya on 3/6/2016.
 */

import com.backendless.geo.GeoPoint;

import java.util.Date;

public class HaveFlat {


    String userId;
    String userName;
    String flatLocation;
    int rentRange;
    String occupancy;
    String lookingFor;
    Date needToShiftDate;
    String phoneNumber;
    boolean shouldShowContactNumber;
    String description;
    String amenitiesAvailable;
    GeoPoint coordinates;
    private String objectId;

    public HaveFlat() {
        // default constructor
    }

    // values initialization Constructor
    public HaveFlat(String objId, String uName, String cPlace, int rent, String occ, String looking,
                    Date needShift, String pNo, boolean contactShow, String desc, GeoPoint coord) {
        this.userId = objId;
        this.userName = uName;
        this.flatLocation = cPlace;
        this.rentRange = rent;
        this.occupancy = occ;
        this.lookingFor = looking;
        this.needToShiftDate = needShift;
        this.phoneNumber = pNo;
        this.shouldShowContactNumber = contactShow;
        this.description = desc;
        this.coordinates = coord;

    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getFlatLocation() {
        return flatLocation;
    }

    public void setFlatLocation(String flatLocation) {
        this.flatLocation = flatLocation;
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

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoPoint coordinated) {
        this.coordinates = coordinated;
    }

    public String getAmenitiesAvailable() {
        return amenitiesAvailable;
    }

    public void setAmenitiesAvailable(String amenitiesAvailable) {
        this.amenitiesAvailable = amenitiesAvailable;
    }

}
