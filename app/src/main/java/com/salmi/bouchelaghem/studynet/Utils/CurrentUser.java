package com.salmi.bouchelaghem.studynet.Utils;

import android.app.Application;

public class CurrentUser extends Application {

    private String userType;
    private static CurrentUser instance;

    public static void setInstance(CurrentUser instance) {
        CurrentUser.instance = instance;
    }

    // This class is a singleton
    public static CurrentUser getInstance(){
        if (instance == null)
            instance = new CurrentUser();
        return instance;
    }

    public CurrentUser() {
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
