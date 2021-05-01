package com.salmi.bouchelaghem.studynet.Models;

import android.os.Parcel;
import android.os.Parcelable;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Homework implements Parcelable {

    private int id;
    private Assignment assignment;
    private ArrayList<Integer> concernedGroups;
    private String title;
    private LocalDate dueDate;
    private LocalTime dueTime;
    private String comment;

    public Homework() {
    }

    public Homework(int id, Assignment assignment, ArrayList<Integer> concernedGroups, String title, LocalDate dueDate, LocalTime dueTime, String comment) {
        this.id = id;
        this.assignment = assignment;
        this.concernedGroups = concernedGroups;
        this.title = title;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.comment = comment;
    }

    @SuppressWarnings("unchecked")
    protected Homework(Parcel in) {
        id = in.readInt();
        assignment = in.readTypedObject(Assignment.CREATOR);
        concernedGroups = (ArrayList<Integer>) in.readSerializable();
        title = in.readString();
        dueDate = (org.threeten.bp.LocalDate) in.readSerializable();
        dueTime = (org.threeten.bp.LocalTime) in.readSerializable();
        comment = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeTypedObject(assignment, flags);
        dest.writeSerializable(concernedGroups);
        dest.writeString(title);
        dest.writeSerializable(dueDate);
        dest.writeSerializable(dueTime);
        dest.writeString(comment);
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

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public List<Integer> getConcernedGroups() {
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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalTime getDueTime() {
        return dueTime;
    }

    public void setDueTime(LocalTime dueTime) {
        this.dueTime = dueTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
