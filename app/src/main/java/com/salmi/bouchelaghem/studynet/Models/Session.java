package com.salmi.bouchelaghem.studynet.Models;

import org.threeten.bp.LocalTime;
import java.util.List;

public class Session {

    private int id;
    private Assignment assignment;
    private List<Integer> concernedGroups;
    private LocalTime startTime;
    private LocalTime endTime;
    private int day;
    private String meetingLink, meetingNumber, meetingPassword;

    public Session() {
    }

    public Session(int id, Assignment assignment, List<Integer> concernedGroups, LocalTime startTime, LocalTime endTime, int day, String meetingLink, String meetingNumber, String meetingPassword) {
        this.id = id;
        this.assignment = assignment;
        this.concernedGroups = concernedGroups;
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
        this.meetingLink = meetingLink;
        this.meetingNumber = meetingNumber;
        this.meetingPassword = meetingPassword;
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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getMeetingNumber() {
        return meetingNumber;
    }

    public void setMeetingNumber(String meetingNumber) {
        this.meetingNumber = meetingNumber;
    }

    public String getMeetingPassword() {
        return meetingPassword;
    }

    public void setMeetingPassword(String meetingPassword) {
        this.meetingPassword = meetingPassword;
    }
}
