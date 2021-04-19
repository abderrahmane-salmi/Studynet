package com.salmi.bouchelaghem.studynet.Models;

import java.util.Date;
import java.util.List;

public class Teacher extends User {

    private String grade;
    private List<Section> sections;

    public Teacher() {
    }

    public Teacher(int id, String email, String firstName, String lastName, String userType, Date dateJoined, String grade, List<Section> sections) {
        super(id, email, firstName, lastName, userType, dateJoined);
        this.grade = grade;
        this.sections = sections;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }
}
