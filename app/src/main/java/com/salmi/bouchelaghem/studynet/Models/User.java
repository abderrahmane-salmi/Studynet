package com.salmi.bouchelaghem.studynet.Models;

import org.threeten.bp.ZonedDateTime;

import java.util.Date;

public class User {

    private int id;
    private String email, firstName, lastName;
    private ZonedDateTime dateJoined;

    public User() {
    }

    public User(int id, String email, String firstName, String lastName, ZonedDateTime dateJoined) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateJoined = dateJoined;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ZonedDateTime getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(ZonedDateTime dateJoined) {
        this.dateJoined = dateJoined;
    }

}
