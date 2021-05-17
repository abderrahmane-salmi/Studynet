package com.salmi.bouchelaghem.studynet.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Objects;

public class Teacher extends User implements Parcelable {

    private String grade;
    private String department;
    private ArrayList<String> sections;
    private ArrayList<Assignment> assignments;

    public Teacher(Teacher teacher) {
        super(teacher.getId(), teacher.getEmail(), teacher.getFirstName(), teacher.getLastName(), teacher.getDateJoined());
        this.grade = teacher.getGrade();
        this.department = teacher.getDepartment();
        this.sections = new ArrayList<>(teacher.getSections());
        this.assignments = new ArrayList<>(teacher.getAssignments());
    }

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
        assignments = in.readArrayList(Assignment.class.getClassLoader());
        department = in.readString();
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
        dest.writeList(assignments);
        dest.writeString(department);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return Objects.equals(getFirstName(), teacher.getFirstName()) &&
                Objects.equals(getLastName(), teacher.getLastName()) &&
                Objects.equals(getEmail(), teacher.getEmail()) &&
                Objects.equals(grade, teacher.grade) &&
                Objects.equals(department, teacher.department) &&
                Objects.equals(sections, teacher.sections) &&
                Objects.equals(assignments, teacher.assignments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grade, department, sections, assignments);
    }
}
