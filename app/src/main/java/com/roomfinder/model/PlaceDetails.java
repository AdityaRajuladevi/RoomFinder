package com.roomfinder.model;

/**
 * Created by admin on 4/27/16.
 */
public class PlaceDetails {
    private double latitude;
    private double longitude;
    private String placeName;
    private String address;

    public PlaceDetails(double lat, double lng, String name, String addr) {
        latitude = lat;
        longitude = lng;
        placeName = name;
        address = addr;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getAddress() {
        return address;
    }
}