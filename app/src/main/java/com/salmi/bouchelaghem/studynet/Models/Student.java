package com.salmi.bouchelaghem.studynet.Models;

import java.util.Date;

public class Student extends User {

    private String registrationNumber;
    private Section section;

    public Student() {
    }

    public Student(int id, String email, String firstName, String lastName, String userType, Date dateJoined, String registrationNumber, Section section) {
        super(id, email, firstName, lastName, userType, dateJoined);
        this.registrationNumber = registrationNumber;
        this.section = section;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
