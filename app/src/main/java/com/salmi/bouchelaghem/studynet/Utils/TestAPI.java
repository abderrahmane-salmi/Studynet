package com.salmi.bouchelaghem.studynet.Utils;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.salmi.bouchelaghem.studynet.Models.Assignment;
import com.salmi.bouchelaghem.studynet.Models.Department;
import com.salmi.bouchelaghem.studynet.Models.Homework;
import com.salmi.bouchelaghem.studynet.Models.Module;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Session;
import com.salmi.bouchelaghem.studynet.Models.Specialty;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.Models.User;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TestAPI extends Application {

    private final List<User> users;
    private final List<Teacher> teachers;
    private final List<Student> students;
    private final List<Module> modules;
    private final List<Department> departments;
    private final List<Specialty> specialties;
    private final List<Section> sections;
    private final List<Assignment> assignments;
    private final List<Session> sessions;
    private final List<Homework> homework;

    private static TestAPI instance;
    public static TestAPI getInstance() {
        if (instance == null)
            instance = new TestAPI();
        return instance;
    }

    public TestAPI() {
        users = new ArrayList<>();
        users.add(new User(1, "email1@me.com", "User1", "User1", "2021-05-04T15:37:55.686454+01:00"));
        users.add(new User(2, "email2@me.com", "User2", "User2", "2021-05-04T15:37:55.686454+01:00"));
        users.add(new User(3, "email3@me.com", "User3", "User3", "2021-05-04T15:37:55.686454+01:00"));
        users.add(new User(4, "email4@me.com", "User4", "User4", "2021-05-04T15:37:55.686454+01:00"));
        users.add(new User(5, "email5@me.com", "User5", "User5", "2021-05-04T15:37:55.686454+01:00"));

        teachers = new ArrayList<>();
        teachers.add(new Teacher(users.get(1).getId(), users.get(1).getEmail(), users.get(1).getFirstName(), users.get(1).getLastName(), users.get(1).getDateJoined(), "MCB","INFO" ,new ArrayList<>(Arrays.asList("L3 ACAD A","L3 ISIL B")),new ArrayList<Assignment>()));
        teachers.add(new Teacher(users.get(2).getId(), users.get(2).getEmail(), users.get(2).getFirstName(), users.get(2).getLastName(), users.get(2).getDateJoined(), "MCB", "INFO",new ArrayList<>(Collections.singletonList("L3 ACAD A")),new ArrayList<Assignment>()));
        teachers.add(new Teacher(users.get(3).getId(), users.get(3).getEmail(), users.get(3).getFirstName(), users.get(3).getLastName(), users.get(3).getDateJoined(), "MCB", "INFO",new ArrayList<>(Arrays.asList("L3 ISIL B","L2 GENIE CIVIL A")),new ArrayList<Assignment>()));

        modules = new ArrayList<>();
        modules.add(new Module("BD2", "Base de données", Arrays.asList("Cours", "TD", "TP") , Arrays.asList("Teacher1","Teacher2")));
        modules.add(new Module("GL2", "Genie Logiciel", Arrays.asList("Cours", "TD", "TP"), Arrays.asList("Teacher1","Teacher2")));
        modules.add(new Module("EN", "English", Collections.singletonList("TD"), Arrays.asList("Teacher1","Teacher2")));

        departments = new ArrayList<>();
        departments.add(new Department("INFO", "Informatique"));
        departments.add(new Department("ST", "Science et Technologie"));

        specialties = new ArrayList<>();
        specialties.add(new Specialty("ISIL", "Licence Ingénierie des Systèmes d’Information et des Logiciels", departments.get(0).getCode()));
        specialties.add(new Specialty("ACAD", "Licence Informatique Académique", departments.get(0).getCode()));
        specialties.add(new Specialty("GC", "Licence en Génie Civil", departments.get(1).getCode()));

        sections = new ArrayList<>();
        sections.add(new Section("L3 ISIL B", 3, specialties.get(0).getCode()));
        sections.add(new Section("L3 ACAD A", 3, specialties.get(1).getCode()));
        sections.add(new Section("L2 GENIE CIVIL A", 4, specialties.get(2).getCode()));

        students = new ArrayList<>();
        students.add(new Student(users.get(0).getId(), users.get(0).getEmail(), users.get(0).getFirstName(), users.get(0).getLastName(), users.get(0).getDateJoined(), "181831033883", sections.get(0), 2));

        assignments = new ArrayList<>();
        assignments.add(new Assignment(1, sections.get(0).getCode(), modules.get(0).getName(), modules.get(0).getCode(), Utils.TD, new ArrayList<>(Arrays.asList(1, 2))));
        assignments.add(new Assignment(2, sections.get(1).getCode(), modules.get(1).getName(), modules.get(1).getCode(), Utils.TP, new ArrayList<>(Collections.singletonList(3))));
        assignments.add(new Assignment(3, sections.get(2).getCode(), modules.get(2).getName(), modules.get(2).getCode(), Utils.COURS, new ArrayList<>(Arrays.asList(1, 2, 3))));
        assignments.add(new Assignment(4, sections.get(0).getCode(), modules.get(1).getName(), modules.get(1).getCode(), Utils.TD, new ArrayList<>(Arrays.asList(1, 2))));
        assignments.add(new Assignment(5, sections.get(0).getCode(), modules.get(1).getName(), modules.get(1).getCode(), Utils.TP, new ArrayList<>(Arrays.asList(1, 2))));
        assignments.add(new Assignment(6, sections.get(0).getCode(), modules.get(2).getName(), modules.get(2).getCode(), Utils.COURS, new ArrayList<>(Arrays.asList(1, 2, 3))));

        sessions = new ArrayList<>();

        homework = new ArrayList<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Department> getDepartments() {
        return departments;
    }

    public List<Specialty> getSpecialties() {
        return specialties;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public List<Homework> getHomework() {
        return homework;
    }
}
