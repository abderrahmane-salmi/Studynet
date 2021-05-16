package com.salmi.bouchelaghem.studynet.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Module implements Parcelable {

    private String code, name;
    private List<String> types;
    private List<String> teachers;



    public Module() {
    }

    public Module(String code, String name, List<String> types, List<String> teachers) {
        this.code = code;
        this.name = name;
        this.types = types;
        this.teachers = teachers;
    }

    protected Module(Parcel in) {
        code = in.readString();
        name = in.readString();
        types = in.createStringArrayList();
    }

    public static final Creator<Module> CREATOR = new Creator<Module>() {
        @Override
        public Module createFromParcel(Parcel in) {
            return new Module(in);
        }

        @Override
        public Module[] newArray(int size) {
            return new Module[size];
        }
    };
    public List<String> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<String> teachers) {
        this.teachers = teachers;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
        dest.writeStringList(types);
    }
}
