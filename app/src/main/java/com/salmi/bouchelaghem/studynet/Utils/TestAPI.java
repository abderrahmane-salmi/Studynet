package com.salmi.bouchelaghem.studynet.Utils;

import android.app.Application;

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

import java.util.ArrayList;
import java.util.Arrays;
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
        users.add(new User(1, "email1@me.com", "User1", "User1", Utils.STUDENT_ACCOUNT, new Date()));
        users.add(new User(2, "email2@me.com", "User2", "User2", Utils.TEACHER_ACCOUNT, new Date()));
        users.add(new User(3, "email3@me.com", "User3", "User3", Utils.TEACHER_ACCOUNT, new Date()));
        users.add(new User(4, "email4@me.com", "User4", "User4", Utils.TEACHER_ACCOUNT, new Date()));
        users.add(new User(5, "email5@me.com", "User5", "User5", Utils.ADMIN_ACCOUNT, new Date()));

        teachers = new ArrayList<>();
        teachers.add(new Teacher(users.get(1).getId(), users.get(1).getEmail(), users.get(1).getFirstName(), users.get(1).getLastName(), users.get(1).getUserType(), users.get(1).getDateJoined(), "MCB"));
        teachers.add(new Teacher(users.get(2).getId(), users.get(2).getEmail(), users.get(2).getFirstName(), users.get(2).getLastName(), users.get(2).getUserType(), users.get(2).getDateJoined(), "MCB"));
        teachers.add(new Teacher(users.get(3).getId(), users.get(3).getEmail(), users.get(3).getFirstName(), users.get(3).getLastName(), users.get(3).getUserType(), users.get(3).getDateJoined(), "MCB"));

        modules = new ArrayList<>();
        modules.add(new Module("BD2", "Base de données", Arrays.asList("Cours", "TD", "TP")));
        modules.add(new Module("GL2", "Genie Logiciel", Arrays.asList("Cours", "TD", "TP")));
        modules.add(new Module("EN", "English", Collections.singletonList("TD")));

        departments = new ArrayList<>();
        departments.add(new Department("INFO", "Informatique"));
        departments.add(new Department("ST", "Science et Technologie"));

        specialties = new ArrayList<>();
        specialties.add(new Specialty("ISIL", "Licence Ingénierie des Systèmes d’Information et des Logiciels", departments.get(0)));
        specialties.add(new Specialty("ACAD", "Licence Informatique Académique", departments.get(0)));
        specialties.add(new Specialty("GC", "Licence en Génie Civil", departments.get(1)));

        sections = new ArrayList<>();
        sections.add(new Section("L3_ISIL_B", 3, specialties.get(0), teachers, modules));
        sections.add(new Section("L3_ACAD_A", 3, specialties.get(1), teachers, modules));
        sections.add(new Section("L2_GENIE_CIVIL_A", 4, specialties.get(2), teachers, modules));

        students = new ArrayList<>();
        students.add(new Student(users.get(0).getId(), users.get(0).getEmail(), users.get(0).getFirstName(), users.get(0).getLastName(), users.get(0).getUserType(), users.get(0).getDateJoined(), "181831033883", sections.get(0), 2));

        assignments = new ArrayList<>();
        assignments.add(new Assignment(1, sections.get(0).getCode(), teachers.get(0).getId(), modules.get(0).getCode(), Utils.TD, Arrays.asList(1, 2)));
        assignments.add(new Assignment(2, sections.get(1).getCode(), teachers.get(1).getId(), modules.get(1).getCode(), Utils.TP, Collections.singletonList(3)));
        assignments.add(new Assignment(3, sections.get(2).getCode(), teachers.get(2).getId(), modules.get(2).getCode(), Utils.COURS, Arrays.asList(1, 2, 3)));
        assignments.add(new Assignment(4, sections.get(0).getCode(), teachers.get(1).getId(), modules.get(1).getCode(), Utils.TD, Arrays.asList(1, 2)));
        assignments.add(new Assignment(5, sections.get(0).getCode(), teachers.get(2).getId(), modules.get(1).getCode(), Utils.TP, Arrays.asList(1, 2)));
        assignments.add(new Assignment(6, sections.get(0).getCode(), teachers.get(0).getId(), modules.get(2).getCode(), Utils.COURS, Arrays.asList(1, 2, 3)));

        sessions = new ArrayList<>();
        sessions.add(new Session(1, assignments.get(0), Collections.singletonList(1), "08:00", "09:30", new Date(2021, 4, 20), "https://facultydz.webex.com/facultydz/j.php?MTID=m275c959d1786501ca18107725f7f883d", "957 867 485", "mfgUdkOp"));
        sessions.add(new Session(2, assignments.get(1), Collections.singletonList(3), "09:40", "11:20", new Date(2021, 4, 20), "https://facultydz.webex.com/facultydz/j.php?MTID=m275c959d1786501ca18107725f7f883d", "957 867 485", "mfgUdkOp"));
        sessions.add(new Session(3, assignments.get(2), Arrays.asList(1, 2, 3), "13:00", "14:30", new Date(2021, 4, 20), "https://facultydz.webex.com/facultydz/j.php?MTID=m275c959d1786501ca18107725f7f883d", "957 867 485", "mfgUdkOp"));
        sessions.add(new Session(4, assignments.get(3), Collections.singletonList(1), "08:00", "09:30", new Date(2021, 4, 20), "https://facultydz.webex.com/facultydz/j.php?MTID=m275c959d1786501ca18107725f7f883d", "957 867 485", "mfgUdkOp"));
        sessions.add(new Session(5, assignments.get(4), Collections.singletonList(3), "09:40", "11:20", new Date(2021, 4, 20), "https://facultydz.webex.com/facultydz/j.php?MTID=m275c959d1786501ca18107725f7f883d", "957 867 485", "mfgUdkOp"));
        sessions.add(new Session(6, assignments.get(5), Arrays.asList(1, 2, 3), "13:00", "14:30", new Date(2021, 4, 20), "https://facultydz.webex.com/facultydz/j.php?MTID=m275c959d1786501ca18107725f7f883d", "957 867 485", "mfgUdkOp"));

        homework = new ArrayList<>();
        homework.add(new Homework(1, assignments.get(0), Arrays.asList(1, 2), "Serie 1, Exercice 2", new Date(2021, 5, 01), "Faites le deuxième exercice de la première série, ne faites pas la dernière question car nous le ferons dans le cours."));
        homework.add(new Homework(2, assignments.get(1), Collections.singletonList(3), "Serie 3, Exercice 1", new Date(2021, 4, 25), "Faites le deuxième exercice de la première série, ne faites pas la dernière question car nous le ferons dans le cours."));
        homework.add(new Homework(3, assignments.get(2), Arrays.asList(1, 2, 3), "Serie 2, Exercice 4", new Date(2021, 5, 10), "Faites le deuxième exercice de la première série, ne faites pas la dernière question car nous le ferons dans le cours."));
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
