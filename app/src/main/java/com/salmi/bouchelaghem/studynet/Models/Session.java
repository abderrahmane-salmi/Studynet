package com.salmi.bouchelaghem.studynet.Models;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalTime;
import java.util.List;

public class Session {

    private int id;
    @SerializedName("teacher_name") private String teacherName;
    @SerializedName("teacher_email") private String teacherEmail;
    private String module;
    private String section;
    @SerializedName("module_type") private String moduleType;
    @SerializedName("concerned_groups") private List<Integer> concernedGroups;
    private int day;
    @SerializedName("start_time") private String startTime;
    @SerializedName("end_time") private String endTime;
    @SerializedName("meeting_link") private String meetingLink;
    @SerializedName("meeting_number") private String meetingNumber;
    @SerializedName("meeting_password") private String meetingPassword;
    private String comment;
    private int assignment;

    public Session() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public List<Integer> getConcernedGroups() {
        return concernedGroups;
    }

    public void setConcernedGroups(List<Integer> concernedGroups) {
        this.concernedGroups = concernedGroups;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public LocalTime getStartTime() {
        return LocalTime.parse(startTime);
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime.toString();
    }

    public LocalTime getEndTime() {
        return LocalTime.parse(endTime);
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime.toString();
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getAssignment() {
        return assignment;
    }

    public void setAssignment(int assignment) {
        this.assignment = assignment;
    }
}
