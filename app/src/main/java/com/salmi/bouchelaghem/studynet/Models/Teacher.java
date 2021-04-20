package com.salmi.bouchelaghem.studynet.Models;

import java.util.Date;
import java.util.List;

public class Teacher extends User {

    private String grade;

    public Teacher() {
    }

    public Teacher(int id, String email, String firstName, String lastName, String userType, Date dateJoined, String grade) {
        super(id, email, firstName, lastName, userType, dateJoined);
        this.grade = grade;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}