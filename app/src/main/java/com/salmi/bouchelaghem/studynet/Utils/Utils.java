package com.salmi.bouchelaghem.studynet.Utils;

import com.google.gson.JsonObject;

public class Utils {

    // Api base url
    public static final String API_BASE_URL = "https://study-net-api.herokuapp.com/api/";

    // Tables
    public static final String HOMEWORK = "Homework";
    public static final String TEACHER = "Teacher";
    public static final String SESSION = "Session";

    // Fields
    public static final String ID = "id";
    public static final String SECTIONS = "Sections";

    // Session
    public static final String MEETING_LINK = "Meeting Link";
    public static final String MEETING_NUMBER = "Meeting Number";
    public static final String MEETING_PASSWORD = "Meeting Password";

    // User types
    public static final String STUDENT_ACCOUNT = "student";
    public static final String TEACHER_ACCOUNT = "teacher";
    public static final String ADMIN_ACCOUNT = "administrator";

    //Shared preferences data
    public static final String SHARED_PREFERENCES_USER_DATA = "userData";
    public static final String SHARED_PREFERENCES_LOGGED_IN = "loggedIn";
    public static final String SHARED_PREFERENCES_TOKEN = "token";

    // Homework filter
    public static final String YESTERDAY = "Yesterday";

    // Action types
    public static final String ACTION = "Action";
    public static final String ACTION_ADD = "Add";
    public static final String ACTION_UPDATE = "Update";

    // Notifications
    public static final String NOTIFICATION_CHANNEL_ID = "1";
    public static final CharSequence NOTIFICATION_CHANNEL_NAME = "1";

    /**
     * Logs in the student given in the student data. (takes care of the token too)
     */
    public static CurrentUser loginStudent(JsonObject student) {
        CurrentUser currentUser = CurrentUser.getInstance();
        //Set the current user
        currentUser.setUserType(Utils.STUDENT_ACCOUNT);
        currentUser.setCurrentStudent(Serializers.StudentDeserializer(student));
        currentUser.setToken(student.get("token").getAsString());
        return currentUser;
    }

    public static class HttpResponses {
        public static final int HTTP_200_OK = 200;
        public static final int HTTP_201_CREATED = 201;
        public static final int HTTP_202_ACCEPTED = 202;
        public static final int HTTP_203_NON_AUTHORITATIVE_INFORMATION = 203;
        public static final int HTTP_204_NO_CONTENT = 204;
        public static final int HTTP_302_FOUND = 302;
        public static final int HTTP_304_NOT_MODIFIED = 304;
        public static final int HTTP_400_BAD_REQUEST = 400;
        public static final int HTTP_401_UNAUTHORIZED = 401;
        public static final int HTTP_403_FORBIDDEN = 403;
        public static final int HTTP_404_NOT_FOUND = 404;
        public static final int HTTP_405_METHOD_NOT_ALLOWED = 405;
        public static final int HTTP_406_NOT_ACCEPTABLE = 406;
        public static final int HTTP_410_GONE = 410;
    }
}
