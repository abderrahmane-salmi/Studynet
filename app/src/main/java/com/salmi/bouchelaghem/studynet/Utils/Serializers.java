package com.salmi.bouchelaghem.studynet.Utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.Models.Admin;
import com.salmi.bouchelaghem.studynet.Models.Assignment;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;

import java.lang.reflect.Array;
import java.util.ArrayList;

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
                userData.get("date_joined").getAsString(),
                studentData.get("registration_number").getAsString(),
                section,
                studentData.get("group").getAsInt()
                );
        return student;
    }

    /** Creates an admin object from JsonObject*/
    public static Admin AdminDeserializer(JsonObject adminData)
    {
        //Create the admin
        Admin admin = new Admin(
                adminData.get("id").getAsInt(),
                adminData.get("email").getAsString(),
                adminData.get("first_name").getAsString(),
                adminData.get("last_name").getAsString(),
                adminData.get("date_joined").getAsString());
        return admin;
    }

    /** Creates a teacher java object from JsonObject*/
    public static Teacher TeacherDeserializer(JsonObject teacherData)
    {
        JsonObject userData = teacherData.get("user").getAsJsonObject();
        JsonArray sections = teacherData.get("sections").getAsJsonArray();
        JsonArray assignments = teacherData.get("assignments").getAsJsonArray();

        //Create the array of sections
        ArrayList<String> sectionsArray = new ArrayList<String>();
        for(int i = 0 ; i < sections.size();++i)
        {
            sectionsArray.add(sections.get(i).getAsString());
        }
        //Create the array of assignments
        ArrayList<Assignment> assignmentArray = new ArrayList<Assignment>();
        for(int i = 0; i < assignments.size(); ++i)
        {
            JsonObject assignmentJson = assignments.get(i).getAsJsonObject();
            JsonArray concernedGroupsJson = assignmentJson.get("concerned_groups").getAsJsonArray();
            ArrayList<Integer> concernedGroups = new ArrayList<Integer>();
            //Build the array of concerned groups for this assignment.
            for (int j = 0; j < concernedGroupsJson.size(); ++j)
            {
                concernedGroups.add(concernedGroupsJson.get(j).getAsInt());
            }
            //Create the assignment and add it to the assignments.
            Assignment assignment = new Assignment(
                    assignmentJson.get("id").getAsInt(),
                    assignmentJson.get("section").getAsString(),
                    "TEMPORARY",
                    assignmentJson.get("module").getAsString(),
                    assignmentJson.get("module_type").getAsString(),
                    concernedGroups);
                    assignmentArray.add(assignment);
        }
        //Create the teacher object
        Teacher teacher = new Teacher(
                userData.get("id").getAsInt(),
                userData.get("email").getAsString(),
                userData.get("first_name").getAsString(),
                userData.get("last_name").getAsString(),
                userData.get("date_joined").getAsString(),
                teacherData.get("grade").getAsString(),
                teacherData.get("department").getAsString(),
                sectionsArray,
                assignmentArray);
        return teacher;
    }

    /** Creates JsonObject from a teacher object and a password string*/
    public static JsonObject CreateTeacherSerializer(Teacher teacher, String password)
    {
        JsonObject userJson = new JsonObject();
        JsonArray sectionsJsonArray = new JsonArray();
        JsonArray assignmentsJsonArray = new JsonArray();
        JsonObject teacherJson = new JsonObject();
        //Create the user json object
        userJson.addProperty("email",teacher.getEmail());
        userJson.addProperty("password",password);
        userJson.addProperty("first_name",teacher.getFirstName());
        userJson.addProperty("last_name",teacher.getLastName());

        //Create the sections json array
        ArrayList<String> sections = teacher.getSections();
        for(int i = 0; i < sections.size(); ++i)
        {
            sectionsJsonArray.add(sections.get(i));
        }

        //Build the assignments json array
        ArrayList<Assignment> assignments = teacher.getAssignments();
        for(int i = 0; i < assignments.size(); ++i)
        {
            Assignment assignment = assignments.get(i);
            JsonObject assignmentJson = new JsonObject();
            JsonArray concernedGroupsJsonArray = new JsonArray();
            //Build the concerned groups array
            ArrayList<Integer> concernedGroups = assignment.getConcernedGroups();
            for(int j=0 ; j < concernedGroups.size(); ++j)
            {
                concernedGroupsJsonArray.add(concernedGroups.get(j));
            }
            //Build the assignment json object
            assignmentJson.addProperty("section",assignment.getSectionCode());
            assignmentJson.addProperty("module_type",assignment.getModuleType());
            assignmentJson.addProperty("module",assignment.getModuleCode());
            assignmentJson.add("concerned_groups",concernedGroupsJsonArray);
            assignmentsJsonArray.add(assignmentJson);
        }

        //Create the teacher object
        teacherJson.add("user",userJson);
        teacherJson.addProperty("grade",teacher.getGrade());
        teacherJson.addProperty("department",teacher.getDepartment());
        teacherJson.add("sections",sectionsJsonArray);
        teacherJson.add("assignments",assignmentsJsonArray);

        return teacherJson;

    }
}
