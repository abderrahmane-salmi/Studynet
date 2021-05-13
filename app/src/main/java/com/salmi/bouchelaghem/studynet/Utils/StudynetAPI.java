package com.salmi.bouchelaghem.studynet.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.Models.Department;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Specialty;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface StudynetAPI {

    // *** Departments ***

    /** Get all departments */
    @GET("departments")
    Call<List<Department>> getDepartments();

    // *** Specialities ***

    /** Get specialities based on department */
    @GET("specialties")
    Call<List<Specialty>> getSpecialities(@Query("department") String department);
    // This will generate 'specialties/?department=INFO'

    // *** Sections ***

    /** Get sections based on specialty*/
    @GET("sections")
    Call<List<Section>> getSpecialitySections(@Query("specialty") String specialty);
    // sections/?specialty=ISIL

    /** Get sections based on department*/
    @GET("sections")
    Call<List<Section>> getDepartmentSections(@Query("department") String department);
    // sections/?department=INFO

    /** Get the user data using the token */
    @GET("user_data")
    Call<JsonObject> getUserData(@Header("Authorization") String token);

    /**Register a student */
    @POST("students/")
    Call<JsonObject> registerStudent(@Body JsonObject student);

    @POST("login/")
    Call<JsonObject> login(@Body JsonObject credentials);

    @POST("logout/")
    Call<ResponseBody> logout(@Header("Authorization") String token);

    @POST("logoutall/")
    Call<ResponseBody> logoutAll(@Header("Authorization") String token);

}
