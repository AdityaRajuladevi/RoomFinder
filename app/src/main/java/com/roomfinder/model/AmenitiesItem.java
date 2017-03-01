package com.roomfinder.model;

/**
 * Created by admin on 4/7/16.
 */
public class AmenitiesItem {

    private String amenitiesName;
    private int amenitiesResourceId;


    public AmenitiesItem(String txt, int id) {
        this.amenitiesName = txt;
        this.amenitiesResourceId = id;
    }

    public int getAmenitiesResourceId() {
        return amenitiesResourceId;
    }

    public String getAmenitiesName() {
        return amenitiesName;
    }
}
