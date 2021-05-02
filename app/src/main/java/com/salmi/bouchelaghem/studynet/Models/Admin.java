package com.salmi.bouchelaghem.studynet.Models;

import java.util.Date;

public class Admin extends User {

    public Admin() {
    }

    public Admin(int id, String email, String firstName, String lastName, Date dateJoined) {
        super(id, email, firstName, lastName, dateJoined);
    }
}
