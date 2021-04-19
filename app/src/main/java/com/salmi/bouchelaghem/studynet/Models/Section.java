package com.salmi.bouchelaghem.studynet.Models;

import java.util.List;

public class Section {

    private String code;
    private int nbGroups;
    private Specialty specialty;
    private List<Teacher> teachers;
    private List<Module> modules;

    public Section() {
    }

    public Section(String code, int nbGroups, Specialty specialty, List<Teacher> teachers, List<Module> modules) {
        this.code = code;
        this.nbGroups = nbGroups;
        this.specialty = specialty;
        this.teachers = teachers;
        this.modules = modules;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getNbGroups() {
        return nbGroups;
    }

    public void setNbGroups(int nbGroups) {
        this.nbGroups = nbGroups;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }
}
