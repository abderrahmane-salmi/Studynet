package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.Models.Admin;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.Models.User;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityLoginBinding;
import com.salmi.bouchelaghem.studynet.databinding.ActivityNavigationBinding;

import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import okhttp3.internal.Util;
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

    private class LoginCallback implements Callback<JsonObject> {

        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            switch (response.code())
            {
                case Utils.HttpResponses.HTTP_201_CREATED:
                    JsonObject responseData = response.body();
                    //Determine the type of the user
                    assert responseData != null; //The response is not supposed to be null.
                    if (responseData.has("student"))
                    {
                        //it's a student
                        Utils.loginStudent(responseData.getAsJsonObject("student"));
                        Toast.makeText(LoginActivity.this, "Successfully logged in as a student.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, NavigationActivity.class));
                        finish();
                        break;
                    }
                    if (responseData.has("teacher"))
                    {
                        break;
                    }
                    if (responseData.has("administrator"))
                    {
                        break;
                    }
                    //Unexpected response from the server.
                    Toast.makeText(LoginActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                    break;
                case Utils.HttpResponses.HTTP_400_BAD_REQUEST:
                    //The credentials are invalid.
                    binding.txtLoginEmail.setError("Invalid credentials");
                    break;
            }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {

        }
    }
}