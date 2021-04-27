package com.salmi.bouchelaghem.studynet.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Section {

    private String code;
    @SerializedName("number_of_groups")
    private int nbGroups;
    private String specialty;

    public Section() {
    }

    public Section(String code, int nbGroups, String specialty) {
        this.code = code;
        this.nbGroups = nbGroups;
        this.specialty = specialty;
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

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }
}
