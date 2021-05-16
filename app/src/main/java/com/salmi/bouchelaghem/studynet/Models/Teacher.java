package com.salmi.bouchelaghem.studynet.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Teacher extends User implements Parcelable {

    private String grade;
    private String department;
    private ArrayList<String> sections;
    private ArrayList<Assignment> assignments;
    public Teacher() {
    }

    public Teacher(int id, String email, String firstName, String lastName, String dateJoined, String grade, String department, ArrayList<String> sections, ArrayList<Assignment> assignments) {
        super(id, email, firstName, lastName, dateJoined);
        this.grade = grade;
        this.sections = sections;
        this.assignments = assignments;
        this.department = department;
    }

    public ArrayList<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(ArrayList<Assignment> assignments) {
        this.assignments = assignments;
    }

    protected Teacher(Parcel in) {
        super(in);
        grade = in.readString();
        sections = in.createStringArrayList();
    }

    public static final Creator<Teacher> CREATOR = new Creator<Teacher>() {
        @Override
        public Teacher createFromParcel(Parcel in) {
            return new Teacher(in);
        }

        @Override
        public Teacher[] newArray(int size) {
            return new Teacher[size];
        }
    };

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public ArrayList<String> getSections() {
        return sections;
    }

    public void setSections(ArrayList<String> sections) {
        this.sections = sections;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(grade);
        dest.writeStringList(sections);
    }
}
