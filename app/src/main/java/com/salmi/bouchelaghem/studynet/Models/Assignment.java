package com.salmi.bouchelaghem.studynet.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Assignment implements Parcelable {

    private int id;
    private String sectionCode;
    private String moduleName;
    private String moduleCode;
    private String moduleType;
    private ArrayList<Integer> concernedGroups;

    public Assignment(Assignment assignment) {
        this.id = assignment.getId();
        this.sectionCode = assignment.getSectionCode();
        this.moduleName = assignment.getModuleName();
        this.moduleCode = assignment.getModuleCode();
        this.moduleType = assignment.getModuleType();
        this.concernedGroups = new ArrayList<>(assignment.getConcernedGroups());
    }

    public Assignment() {
    }

    public Assignment(int id, String sectionCode, String moduleName, String moduleCode, String moduleType, ArrayList<Integer> concernedGroups) {
        this.id = id;
        this.sectionCode = sectionCode;
        this.moduleName = moduleName;
        this.moduleCode = moduleCode;
        this.moduleType = moduleType;
        this.concernedGroups = concernedGroups;
    }
    @SuppressWarnings("unchecked")
    protected Assignment(Parcel in) {
        id = in.readInt();
        sectionCode = in.readString();
        moduleName = in.readString();
        moduleCode = in.readString();
        moduleType = in.readString();
        concernedGroups = (ArrayList<Integer>) in.readSerializable();
    }

    public static final Creator<Assignment> CREATOR = new Creator<Assignment>() {
        @Override
        public Assignment createFromParcel(Parcel in) {
            return new Assignment(in);
        }

        @Override
        public Assignment[] newArray(int size) {
            return new Assignment[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Assignment))  {
            return false;
        }
        Assignment other = (Assignment) obj;
        return sectionCode.equals(other.sectionCode)
                && moduleCode.equals(other.moduleCode)
                && moduleType.equals(other.moduleType);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSectionCode() {
        return sectionCode;
    }

    public void setSectionCode(String sectionCode) {
        this.sectionCode = sectionCode;
    }


    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public ArrayList<Integer> getConcernedGroups() {
        return concernedGroups;
    }

    public void setConcernedGroups(ArrayList<Integer> concernedGroups) {
        this.concernedGroups = concernedGroups;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(sectionCode);
        dest.writeString(moduleName);
        dest.writeString(moduleCode);
        dest.writeString(moduleType);
        dest.writeSerializable(concernedGroups);
    }
}
