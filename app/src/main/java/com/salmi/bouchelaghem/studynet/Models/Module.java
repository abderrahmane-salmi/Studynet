package com.salmi.bouchelaghem.studynet.Models;

import java.util.List;

public class Module {

    private String code, name;
    private List<String> types;

    public Module() {
    }

    public Module(String code, String name, List<String> types) {
        this.code = code;
        this.name = name;
        this.types = types;
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

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
