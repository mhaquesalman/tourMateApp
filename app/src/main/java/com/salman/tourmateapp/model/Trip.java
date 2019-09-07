package com.salman.tourmateapp.model;

public class Trip {
    String tripId;
    String tripName;
    String tripDesc;
    String tripStartDate;
    String tripEndDate;
    String userId;

    public Trip() {
    }

    public Trip(String tripId, String tripName, String tripDesc, String tripStartDate, String tripEndDate, String userId) {
        this.tripId = tripId;
        this.tripName = tripName;
        this.tripDesc = tripDesc;
        this.tripStartDate = tripStartDate;
        this.tripEndDate = tripEndDate;
        this.userId = userId;
    }


    public String getTripId() {
        return tripId;
    }

    public String getTripName() {
        return tripName;
    }

    public String getTripDesc() {
        return tripDesc;
    }

    public String getTripStartDate() {
        return tripStartDate;
    }

    public String getTripEndDate() {
        return tripEndDate;
    }

    public String getUserId() {
        return userId;
    }
}
