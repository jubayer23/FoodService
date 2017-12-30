package com.smartysoft.foodservice.model;

/**
 * Created by comsol on 30-Dec-17.
 */

public class User {
    String id;
    String userName;
    String fcmId;
    String authImie;


    public User(String id, String userName, String fcmId, String authImie) {
        this.id = id;
        this.userName = userName;
        this.fcmId = fcmId;
        this.authImie = authImie;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFcmId() {
        return fcmId;
    }

    public void setFcmId(String fcmId) {
        this.fcmId = fcmId;
    }

    public String getAuthImie() {
        return authImie;
    }

    public void setAuthImie(String authImie) {
        this.authImie = authImie;
    }
}
