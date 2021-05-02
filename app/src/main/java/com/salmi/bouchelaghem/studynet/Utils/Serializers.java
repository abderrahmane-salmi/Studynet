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

    /** Creates a student object from JsonObject (assumes that the section is detailed)*/
    public static Student StudentDeserializer(JsonObject studentData)
    {
        JsonObject userData = studentData.getAsJsonObject("user");
        JsonObject sectionData = studentData.getAsJsonObject("section");

        //Create the section of this student.
        Section section = new Section(
                sectionData.get("code").getAsString(),
                sectionData.get("number_of_groups").getAsInt(),
                sectionData.get("specialty").getAsString());
        //Create the student.
        Student student = new Student(
                userData.get("id").getAsInt(),
                userData.get("email").getAsString(),
                userData.get("first_name").getAsString(),
                userData.get("last_name").getAsString(),
                ZonedDateTime.parse(userData.get("date_joined").getAsString(), DateTimeFormatter.ISO_DATE_TIME),
                studentData.get("registration_number").getAsString(),
                section,
                studentData.get("group").getAsInt()
                );
        return student;
    }
}
