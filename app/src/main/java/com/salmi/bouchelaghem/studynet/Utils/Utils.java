package com.salmi.bouchelaghem.studynet.Utils;

public class Utils {

    // Api base url
    public static final String API_BASE_URL = "https://study-net-api.herokuapp.com/api/";

    // Tables
    public static final String HOMEWORK = "Homework";

    // Fields
    public static final String ID = "id";

    // Session
    public static final String MEETING_LINK = "Meeting Link";
    public static final String MEETING_NUMBER = "Meeting Number";
    public static final String MEETING_PASSWORD = "Meeting Password";

    // User types
    public static final String STUDENT_ACCOUNT = "Student";
    public static final String TEACHER_ACCOUNT = "Teacher";
    public static final String ADMIN_ACCOUNT = "Admin";

    // Homework filter
    // TODO: take this to a string array (so we can translate it)
    public static final String YESTERDAY = "Yesterday";
    public static final String TODAY = "Today";
    public static final String TOMORROW = "Tomorrow";
    public static final String THIS_WEEK = "This week";
    public static final String ALL = "All";

    // Class types
    public static final String COURS = "Cours";
    public static final String TD = "TD";
    public static final String TP = "TP";

    // Action types
    public static final String ACTION = "Action";
    public static final String ACTION_ADD = "Add";
    public static final String ACTION_UPDATE = "Update";
    public static final String ACTION_DELETE = "Delete";

}
