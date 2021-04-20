package com.salmi.bouchelaghem.studynet.Models;

import java.util.List;

public class Assignment {

    private int id;
    private String sectionCode;
    private int teacherId;
    // TODO: Think about adding the module's name for display purposes
    private String moduleCode;
    private String moduleType;
    private List<Integer> concernedGroups;

    public Assignment() {
    }

    public Assignment(int id, String sectionCode, int teacherId, String moduleCode, String moduleType, List<Integer> concernedGroups) {
        this.id = id;
        this.sectionCode = sectionCode;
        this.teacherId = teacherId;
        this.moduleCode = moduleCode;
        this.moduleType = moduleType;
        this.concernedGroups = concernedGroups;
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

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
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

    public List<Integer> getConcernedGroups() {
        return concernedGroups;
    }

    public void setConcernedGroups(List<Integer> concernedGroups) {
        this.concernedGroups = concernedGroups;
    }
}
