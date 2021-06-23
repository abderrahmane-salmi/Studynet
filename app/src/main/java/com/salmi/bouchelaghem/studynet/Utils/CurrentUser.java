package com.salmi.bouchelaghem.studynet.Utils;

import android.app.Application;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.salmi.bouchelaghem.studynet.Models.Admin;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;

public class CurrentUser extends Application {

    private String userType;
    private Student currentStudent;
    private Teacher currentTeacher;
    private Admin currentAdmin;
    private String token;
    private static CurrentUser instance;

    @Override
    public void onCreate() {

        super.onCreate();
        AndroidThreeTen.init(this);
    }

    public static void setInstance(CurrentUser instance) {
        CurrentUser.instance = instance;
    }

    /**
     * This class is a singleton
     */
    public static CurrentUser getInstance() {
        if (instance == null)
            instance = new CurrentUser();
        return instance;
    }

    public CurrentUser() {
    }

    /**
     * Remove all the data.
     */
    public void logout() {

        userType = null;
        currentStudent = null;
        currentTeacher = null;
        currentAdmin = null;
        token = null;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public void setCurrentStudent(Student currentStudent) {
        this.currentStudent = currentStudent;
    }

    public Teacher getCurrentTeacher() {
        return currentTeacher;
    }

    public void setCurrentTeacher(Teacher currentTeacher) {
        this.currentTeacher = currentTeacher;
    }

    public void setCurrentAdmin(Admin currentAdmin) {
        this.currentAdmin = currentAdmin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
