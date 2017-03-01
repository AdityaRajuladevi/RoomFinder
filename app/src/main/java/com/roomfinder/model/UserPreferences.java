package com.roomfinder.model;

/**
 * Created by admin on 2/15/16.
 */
public class UserPreferences {

    public boolean isUserPostedHaveFlatRequirement;
    public boolean isUserPostedNeedFlatRequirement;
    private String objId;
    private String foodType;
    private String drinkingType;
    private String smokingType;
    private String lifeStyleType;

    public boolean isUserPostedHaveFlatRequirement() {
        return isUserPostedHaveFlatRequirement;
    }

    public void setIsUserPostedHaveFlatRequirement(boolean isUserPostedRequirement) {
        this.isUserPostedHaveFlatRequirement = isUserPostedRequirement;
    }

    public boolean isUserPostedNeedFlatRequirement() {
        return isUserPostedNeedFlatRequirement;
    }

    public void setIsUserPostedNeedFlatRequirement(boolean isUserPostedNeedFlatRequirement) {
        this.isUserPostedNeedFlatRequirement = isUserPostedNeedFlatRequirement;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getDrinkingType() {
        return drinkingType;
    }

    public void setDrinkingType(String drinkingType) {
        this.drinkingType = drinkingType;
    }

    public String getSmokingType() {
        return smokingType;
    }

    public void setSmokingType(String smokingType) {
        this.smokingType = smokingType;
    }

    public String getLifeStyleType() {
        return lifeStyleType;
    }

    public void setLifeStyleType(String lifeStyleType) {
        this.lifeStyleType = lifeStyleType;
    }

}
