package com.salmi.bouchelaghem.studynet.Models;

import java.util.Date;
import java.util.List;

public class Homework {

    private int id;
    private Assignment assignment;
    private List<Integer> concernedGroups;
    private String title;
    private Date dueDate;
    private String comment;

    public Homework() {
    }

    public Homework(int id, Assignment assignment, List<Integer> concernedGroups, String title, Date dueDate, String comment) {
        this.id = id;
        this.assignment = assignment;
        this.concernedGroups = concernedGroups;
        this.title = title;
        this.dueDate = dueDate;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public List<Integer> getConcernedGroups() {
        return concernedGroups;
    }

    public void setConcernedGroups(List<Integer> concernedGroups) {
        this.concernedGroups = concernedGroups;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
