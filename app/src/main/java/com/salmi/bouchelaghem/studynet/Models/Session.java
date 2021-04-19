package com.salmi.bouchelaghem.studynet.Models;

import java.util.List;

public class Session {

    private int id;
    private Assignment assignment;
    private List<Integer> concernedGroups;
    /* Time picker gives you the hours and the minutes separated as int format
    * so we can either save startTimeHour and startTimeMin, (same for end time)
    * or save the start and end time as strings and build them when the user chooses a time
    * Example: startTime = "08:00" , endTime = "09:30" */
    private String startTime;
    private String endTime;
    private String meetingLink, meetingNumber, meetingPassword;

    public Session() {
    }

    public Session(int id, Assignment assignment, List<Integer> concernedGroups, String startTime, String endTime, String meetingLink, String meetingNumber, String meetingPassword) {
        this.id = id;
        this.assignment = assignment;
        this.concernedGroups = concernedGroups;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
