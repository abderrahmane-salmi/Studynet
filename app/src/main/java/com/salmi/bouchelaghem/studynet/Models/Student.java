package com.salmi.bouchelaghem.studynet.Models;

import org.threeten.bp.ZonedDateTime;

import java.util.Date;

public class Student extends User {

    private String registrationNumber;
    private Section section;
    private int group;

    public Student() {
    }

    public Student(int id, String email, String firstName, String lastName, String dateJoined, String registrationNumber, Section section, int group) {
        super(id, email, firstName, lastName, dateJoined);
        this.registrationNumber = registrationNumber;
        this.section = section;
        this.group = group;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }
}
