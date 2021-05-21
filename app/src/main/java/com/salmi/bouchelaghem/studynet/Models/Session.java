package com.salmi.bouchelaghem.studynet.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Session implements Parcelable {

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

    public Session(Session session) {
        this.id = session.getId();
        this.teacherName = session.getTeacherName();
        this.teacherEmail = session.getTeacherEmail();
        this.module = session.getModule();
        this.section = session.getSection();
        this.moduleType = session.getModuleType();
        this.concernedGroups = new ArrayList<>(session.getConcernedGroups());
        this.day = session.getDay();
        this.startTime = session.getStartTime();
        this.endTime = session.getEndTime();
        this.meetingLink = session.getMeetingLink();
        this.meetingNumber = session.getMeetingNumber();
        this.meetingPassword = session.getMeetingPassword();
        this.comment = session.getComment();
        this.assignment = session.getAssignment();
    }

    public Session() {
    }

    protected Session(Parcel in) {
        id = in.readInt();
        teacherName = in.readString();
        teacherEmail = in.readString();
        module = in.readString();
        section = in.readString();
        moduleType = in.readString();
        day = in.readInt();
        startTime = in.readString();
        endTime = in.readString();
        meetingLink = in.readString();
        meetingNumber = in.readString();
        meetingPassword = in.readString();
        comment = in.readString();
        assignment = in.readInt();
        concernedGroups = in.readArrayList(null);
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel in) {
            return new Session(in);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return id == session.id &&
                day == session.day &&
                assignment == session.assignment &&
                Objects.equals(teacherName, session.teacherName) &&
                Objects.equals(teacherEmail, session.teacherEmail) &&
                Objects.equals(module, session.module) &&
                Objects.equals(section, session.section) &&
                Objects.equals(moduleType, session.moduleType) &&
                Objects.equals(concernedGroups, session.concernedGroups) &&
                this.getLocalTimeStartTime().equals(session.getLocalTimeStartTime()) &&
                this.getLocalTimeEndTime().equals(session.getLocalTimeEndTime()) &&
                Objects.equals(meetingLink, session.meetingLink) &&
                Objects.equals(meetingNumber, session.meetingNumber) &&
                Objects.equals(meetingPassword, session.meetingPassword) &&
                Objects.equals(comment, session.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, teacherName, teacherEmail, module, section, moduleType, concernedGroups, day, startTime, endTime, meetingLink, meetingNumber, meetingPassword, comment, assignment);
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

    public LocalTime getLocalTimeStartTime() {
        return LocalTime.parse(startTime);
    }

    public void setLocalTimeStartTime(LocalTime startTime) {
        this.startTime = startTime.toString();
    }

    public LocalTime getLocalTimeEndTime() {
        return LocalTime.parse(endTime);
    }

    public void setLocalTimeEndTime(LocalTime endTime) {
        this.endTime = endTime.toString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(teacherName);
        dest.writeString(teacherEmail);
        dest.writeString(module);
        dest.writeString(section);
        dest.writeString(moduleType);
        dest.writeInt(day);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(meetingLink);
        dest.writeString(meetingNumber);
        dest.writeString(meetingPassword);
        dest.writeString(comment);
        dest.writeInt(assignment);
        dest.writeList(concernedGroups);
    }
}
