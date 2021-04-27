package com.salmi.bouchelaghem.studynet.Utils;

import com.salmi.bouchelaghem.studynet.Models.Department;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StudynetAPI {

    // Departments

    // Get all departments
    @GET("departments/")
    Call<List<Department>> getDepartments();

    // Specialities

    // Sections

}
