package com.salmi.bouchelaghem.studynet.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.Serializers;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityLoginBinding;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private final CurrentUser currentUser = CurrentUser.getInstance();
    private StudynetAPI api;
    private SharedPreferences sharedPreferences;
    private CustomLoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Set the light theme is the default theme.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        loadingDialog = new CustomLoadingDialog(LoginActivity.this);

        //Get the shared preferences.
        sharedPreferences = getApplicationContext().getSharedPreferences(Utils.SHARED_PREFERENCES_USER_DATA, MODE_PRIVATE);

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api, this will implement the code of all the methods in the interface.
        api = retrofit.create(StudynetAPI.class);

        loadingDialog = new CustomLoadingDialog(LoginActivity.this);

        binding.btnGoToSignUp.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));

        binding.btnGoToResetPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class)));

        binding.btnLogin.setOnClickListener(v -> {
            if (validateEmail() & validatePassword()) {
                loadingDialog.show();
                JsonObject credentials = new JsonObject();
                credentials.addProperty("email", binding.txtLoginEmail.getEditText().getText().toString().trim());
                credentials.addProperty("password", binding.txtLoginPassword.getEditText().getText().toString());
                Call<JsonObject> login = api.login(credentials);
                login.enqueue(new LoginCallback());
            }
        });
    }

    public boolean validateEmail() {
        String email = binding.txtLoginEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            binding.txtLoginEmail.setError(getString(R.string.email_msg1));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtLoginEmail.setError(getString(R.string.email_msg2));
            return false;
        } else {
            binding.txtLoginEmail.setError(null);
            return true;
        }
    }

    public boolean validatePassword() {
        String password = binding.txtLoginPassword.getEditText().getText().toString().trim();

        if (password.isEmpty()) {
            binding.txtLoginPassword.setError(getString(R.string.empty_password_msg));
            return false;
        } else if (password.length() < 6) {
            binding.txtLoginPassword.setError(getString(R.string.password_msg2));
            return false;
        } else {
            binding.txtLoginPassword.setError(null);
            return true;
        }
    }

    /**
     * Logs in the teacher given in the teacher data. (takes care of the token too)
     */
    public static void loginTeacher(JsonObject teacher) {
        CurrentUser currentUser = CurrentUser.getInstance();
        //Set the current user
        currentUser.setUserType(Utils.TEACHER_ACCOUNT);
        currentUser.setCurrentTeacher(Serializers.TeacherDeserializer(teacher));
        currentUser.setToken(teacher.get("token").getAsString());
    }

    /**
     * Logs in the admin given in the admin data. (takes care of the token too)
     */
    public static void loginAdmin(JsonObject admin) {
        CurrentUser currentUser = CurrentUser.getInstance();
        //Set the current user
        currentUser.setUserType(Utils.ADMIN_ACCOUNT);
        currentUser.setCurrentAdmin(Serializers.AdminDeserializer(admin));
        currentUser.setToken(admin.get("token").getAsString());
    }

    /**
     * Callback logic for the login process.
     */
    private class LoginCallback implements Callback<JsonObject> {

        @Override
        public void onResponse(@NonNull Call<JsonObject> call, Response<JsonObject> response) {
            switch (response.code()) {
                case Utils.HttpResponses.HTTP_201_CREATED:
                    JsonObject responseData = response.body();
                    //Determine the type of the user
                    assert responseData != null; //The response is not supposed to be null.
                    if (responseData.has(Utils.STUDENT_ACCOUNT)) {
                        //It's a student, log him in
                        Utils.loginStudent(responseData.getAsJsonObject(Utils.STUDENT_ACCOUNT));
                        //Subscribe this device to this student's section's push notifications.
                        String sectionCode = currentUser.getCurrentStudent().getSection().getCode();
                        FirebaseMessaging.getInstance().subscribeToTopic(sectionCode.replace(' ','_'));
                    } else {
                        if (responseData.has(Utils.TEACHER_ACCOUNT)) {
                            //It's a teacher
                            loginTeacher(responseData.getAsJsonObject(Utils.TEACHER_ACCOUNT));
                            //Get the FCM token for this device
                            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(FCMToken -> {
                                //Send the FCMToken to the backend to enable targeted notifications for this teacher.
                                JsonObject fcmTokenJson = new JsonObject();
                                fcmTokenJson.addProperty("FCM_token",FCMToken);
                                Call<ResponseBody> registerFcmCall = api.registerFCM(fcmTokenJson,"Token " + currentUser.getToken());
                                registerFcmCall.enqueue(new FCMTokenRegisterCallback<>());
                            });
                        } else {
                            if (responseData.has(Utils.ADMIN_ACCOUNT)) {
                                //It's an administrator
                                loginAdmin(responseData.getAsJsonObject(Utils.ADMIN_ACCOUNT));
                            } else {
                                //Unexpected response from the server.
                                Toast.makeText(LoginActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                                loadingDialog.dismiss();
                                break;
                            }
                        }

                    }
                    //Save the user data locally.
                    saveCurrentUser();
                    startActivity(new Intent(LoginActivity.this, NavigationActivity.class));
                    Toast.makeText(LoginActivity.this, getString(R.string.logged_in_msg), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    finish();
                    break;
                case Utils.HttpResponses.HTTP_400_BAD_REQUEST:
                    //The credentials are invalid.
                    Toast.makeText(LoginActivity.this, getString(R.string.email_password_incorrect), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    break;
            }
        }

        @Override
        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
            Toast.makeText(LoginActivity.this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
        }
    }

    /**
     * Saves the user token using shared preferences.
     */
    private void saveCurrentUser() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        //Save the token.
        prefsEditor.putString(Utils.SHARED_PREFERENCES_TOKEN, currentUser.getToken());
        prefsEditor.putBoolean(Utils.SHARED_PREFERENCES_LOGGED_IN, true);
        prefsEditor.apply();
    }

    public class FCMTokenRegisterCallback<T> implements Callback<T>
    {

        @Override
        public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
            //Do nothing
        }

        @Override
        public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
            //Do nothing, not a critical error
        }
    }
}