package com.salmi.bouchelaghem.studynet.Models;

public class Specialty {

    private String code, name;
    private Department department;

    public Specialty() {
    }

    public Specialty(String code, String name, Department department) {
        this.code = code;
        this.name = name;
        this.department = department;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
