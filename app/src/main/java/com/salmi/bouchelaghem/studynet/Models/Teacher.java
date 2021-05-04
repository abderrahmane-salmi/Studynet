package com.salmi.bouchelaghem.studynet.Models;

import android.os.Parcelable;

import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Teacher extends User implements Parcelable {

    private String grade;
    private ArrayList<String> sections;

    public Teacher() {
    }

    public Teacher(int id, String email, String firstName, String lastName, ZonedDateTime dateJoined, String grade,ArrayList<String> sections) {
        super(id, email, firstName, lastName, dateJoined);
        this.grade = grade;
        this.sections = sections;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
