package com.salmi.bouchelaghem.studynet.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;

public class Homework implements Parcelable {

    private int id;
    @SerializedName("teacher_name") private String teacherName;
    @SerializedName("teacher_email") private String teacherEmail;
    private String module;
    @SerializedName("module_type") private String moduleType;
    private String section;
    private ArrayList<Integer> concernedGroups;
    private String title;
    @SerializedName("due_date") private String dueDate;
    @SerializedName("due_time") private String dueTime;
    private String comment;
    private int assignment;

    public Homework() {
    }

    protected Homework(Parcel in) {
        id = in.readInt();
        teacherName = in.readString();
        teacherEmail = in.readString();
        module = in.readString();
        moduleType = in.readString();
        section = in.readString();
        concernedGroups = in.readArrayList(null);
        title = in.readString();
        dueDate = in.readString();
        dueTime = in.readString();
        comment = in.readString();
        assignment = in.readInt();
    }

    public static final Creator<Homework> CREATOR = new Creator<Homework>() {
        @Override
        public Homework createFromParcel(Parcel in) {
            return new Homework(in);
        }

        @Override
        public Homework[] newArray(int size) {
            return new Homework[size];
        }
    };

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

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public ArrayList<Integer> getConcernedGroups() {
        return concernedGroups;
    }

    public void setConcernedGroups(ArrayList<Integer> concernedGroups) {
        this.concernedGroups = concernedGroups;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getLocalDateDueDate() {
        return LocalDate.parse(dueDate);
    }

    public void setLocalDateDueDate(LocalDate dueDate) {
        this.dueDate = dueDate.toString();
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public LocalTime getLocalTimeDueTime() {
        return LocalTime.parse(dueTime);
    }

    public void setLocalTimeDueTime(LocalTime dueTime) {
        this.dueTime = dueTime.toString();
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
        dest.writeString(moduleType);
        dest.writeString(section);
        dest.writeList(concernedGroups);
        dest.writeString(title);
        dest.writeString(dueDate);
        dest.writeString(dueTime);
        dest.writeString(comment);
        dest.writeInt(assignment);
    }
}
