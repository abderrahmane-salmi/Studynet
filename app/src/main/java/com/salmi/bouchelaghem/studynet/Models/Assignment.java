package com.salmi.bouchelaghem.studynet.Models;

import java.util.List;

public class Assignment {

    private int id;
    private Section section;
    private Teacher teacher;
    private Module module;
    private String moduleType;
    private List<Integer> concernedGroups;

    public Assignment() {
    }

    public Assignment(int id, Section section, Teacher teacher, Module module, String moduleType, List<Integer> concernedGroups) {
        this.id = id;
        this.section = section;
        this.teacher = teacher;
        this.module = module;
        this.moduleType = moduleType;
        this.concernedGroups = concernedGroups;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
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
