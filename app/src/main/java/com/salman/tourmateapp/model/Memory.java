package com.salman.tourmateapp.model;

public class Memory {
    String memoryId;
    String memoryimage;
    String memoryDesc;
    String tripLocatiion;
    String userId;

    public Memory() {
    }

    public Memory(String memoryId, String memoryimage, String memoryDesc, String tripLocatiion, String userId) {
        this.memoryId = memoryId;
        this.memoryimage = memoryimage;
        this.memoryDesc = memoryDesc;
        this.tripLocatiion = tripLocatiion;
        this.userId = userId;
    }

    public String getMemoryId() {
        return memoryId;
    }

    public String getMemoryimage() {
        return memoryimage;
    }

    public String getMemoryDesc() {
        return memoryDesc;
    }

    public String getTripLocatiion() {
        return tripLocatiion;
    }

    public String getUserId() {
        return userId;
    }
}
