package com.salmi.bouchelaghem.studynet.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.Serializers;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private CurrentUser currentUser = CurrentUser.getInstance();
    private StudynetAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        // Set the light theme is the default theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api, this will implement the code of all the methods in the interface
        api = retrofit.create(StudynetAPI.class);

        binding.btnGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        binding.btnGoToResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateEmail() && validatePassword())
                {
                    JsonObject credentials = new JsonObject();
                    credentials.addProperty("email",binding.txtLoginEmail.getEditText().getText().toString().trim());
                    credentials.addProperty("password",binding.txtLoginPassword.getEditText().getText().toString());
                    Call<JsonObject> login = api.login(credentials);
                    login.enqueue(new LoginCallback());
                }
            }
        });
    }

    public boolean validateEmail(){
        String email = binding.txtLoginEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()){
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
    public boolean validatePassword(){
        String password = binding.txtLoginPassword.getEditText().getText().toString().trim();

        if (password.isEmpty()){
            binding.txtLoginPassword.setError(getString(R.string.password_msg));
            return false;
        } else if (password.length() < 6) {
            binding.txtLoginPassword.setError(getString(R.string.password_msg2));
            return false;
        } else {
            binding.txtLoginPassword.setError(null);
            return true;
        }
    }

    /** Logs in the teacher given in the teacher data. (takes care of the token too)*/
    public static CurrentUser loginTeacher(JsonObject teacher)
    {
        CurrentUser currentUser = CurrentUser.getInstance();
        //Set the current user
        currentUser.setUserType(Utils.TEACHER_ACCOUNT);
        currentUser.setCurrentTeacher(Serializers.TeacherDeserializer(teacher));
        currentUser.setToken(teacher.get("token").getAsString());
        return currentUser;
    }

    /** Logs in the admin given in the admin data. (takes care of the token too)*/
    public static void loginAdmin(JsonObject admin)
    {
        CurrentUser currentUser = CurrentUser.getInstance();
        //Set the current user
        currentUser.setUserType(Utils.ADMIN_ACCOUNT);
        currentUser.setCurrentAdmin(Serializers.AdminDeserializer(admin));
        currentUser.setToken(admin.get("token").getAsString());
    }

    /** Callback logic for the login process.*/
    private class LoginCallback implements Callback<JsonObject> {

        @Override
        public void onResponse(@NonNull Call<JsonObject> call, Response<JsonObject> response) {
            switch (response.code())
            {
                case Utils.HttpResponses.HTTP_201_CREATED:
                    JsonObject responseData = response.body();
                    //Determine the type of the user
                    assert responseData != null; //The response is not supposed to be null.
                    if (responseData.has(Utils.STUDENT_ACCOUNT))
                    {
                        //It's a student
                        Utils.loginStudent(responseData.getAsJsonObject(Utils.STUDENT_ACCOUNT));
                        Toast.makeText(LoginActivity.this, "Successfully logged in as a student.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if (responseData.has(Utils.TEACHER_ACCOUNT))
                        {
                            //It's a teacher
                            loginTeacher(responseData.getAsJsonObject(Utils.TEACHER_ACCOUNT));
                            Toast.makeText(LoginActivity.this, "Successfully logged in as a teacher.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if (responseData.has(Utils.ADMIN_ACCOUNT))
                            {
                                //It's an administrator
                                loginAdmin(responseData.getAsJsonObject(Utils.ADMIN_ACCOUNT));
                                Toast.makeText(LoginActivity.this, "Successfully logged in as an administrator.", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                //Unexpected response from the server.
                                Toast.makeText(LoginActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }

                    }
                    startActivity(new Intent(LoginActivity.this, NavigationActivity.class));
                    finish();
                    break;
                case Utils.HttpResponses.HTTP_400_BAD_REQUEST:
                    //The credentials are invalid.
                    binding.txtLoginEmail.setError("Invalid credentials");
                    break;
            }
        }

        @Override
        public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
            Toast.makeText(LoginActivity.this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
        }
    }
}