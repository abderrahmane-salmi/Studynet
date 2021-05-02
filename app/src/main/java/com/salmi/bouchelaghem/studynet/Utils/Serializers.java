package com.salmi.bouchelaghem.studynet.Utils;

import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Student;

import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class Serializers {
    /** Creates a json student object from student data. */
    public static JsonObject studentSerializer(String email, String password,
                                        String firstName, String lastName,
                                        String registrationNumber, String section, int group)
    {
        JsonObject user = new JsonObject();
        JsonObject student = new JsonObject();
        user.addProperty("email",email);
        user.addProperty("password",password);
        user.addProperty("first_name",firstName);
        user.addProperty("last_name",lastName);
        student.add("user",user);
        student.addProperty("registration_number",registrationNumber);
        student.addProperty("group",group);
        student.addProperty("section",section);
        return student;
    }
    
    /** Fills the data returned by the registration of a student into the current user (takes care of the returned token too).*/
    public static CurrentUser RegisterStudentDeserializer(JsonObject studentLoginData)
    {
        JsonObject userData = studentLoginData.getAsJsonObject("user");
        JsonObject sectionData = studentLoginData.getAsJsonObject("section");
        CurrentUser currentUser = CurrentUser.getInstance();
        //Create the section of this student.
        Section section = new Section(
                sectionData.get("code").getAsString(),
                sectionData.get("number_of_groups").getAsInt(),
                sectionData.get("specialty").getAsString());
        //Create the student that we will be setting as the current user.
        Student student = new Student(
                userData.get("id").getAsInt(),
                userData.get("email").getAsString(),
                userData.get("first_name").getAsString(),
                userData.get("last_name").getAsString(),
                ZonedDateTime.parse(userData.get("date_joined").getAsString(), DateTimeFormatter.ISO_DATE_TIME),
                studentLoginData.get("registration_number").getAsString(),
                section,
                studentLoginData.get("group").getAsInt()
                );
        //Create the current user.
        currentUser.setUserType(Utils.STUDENT_ACCOUNT);
        currentUser.setCurrentStudent(student);
        currentUser.setToken(studentLoginData.get("token").getAsString());
        return currentUser;
    }
}
