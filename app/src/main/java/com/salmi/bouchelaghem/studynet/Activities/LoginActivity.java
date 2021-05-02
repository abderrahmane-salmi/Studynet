package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.salmi.bouchelaghem.studynet.Models.Admin;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.Models.User;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityLoginBinding;
import com.salmi.bouchelaghem.studynet.databinding.ActivityNavigationBinding;

import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private CurrentUser currentUser = CurrentUser.getInstance();

    TestAPI testAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Set the light theme is the default theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        testAPI = TestAPI.getInstance();

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

                // Default
                currentUser.setUserType(Utils.STUDENT_ACCOUNT);
                currentUser.setCurrentStudent(new Student(testAPI.getUsers().get(0).getId(), testAPI.getUsers().get(0).getEmail(), testAPI.getUsers().get(0).getFirstName(), testAPI.getUsers().get(0).getLastName(), testAPI.getUsers().get(0).getDateJoined(), "181831033883", testAPI.getSections().get(0), 2));

                if (binding.txtLoginEmail.getEditText().getText().toString().equals("e") ){

                    // If its a student
                    currentUser.setUserType(Utils.STUDENT_ACCOUNT);
                    currentUser.setCurrentStudent(new Student(testAPI.getUsers().get(0).getId(), testAPI.getUsers().get(0).getEmail(), testAPI.getUsers().get(0).getFirstName(), testAPI.getUsers().get(0).getLastName(), testAPI.getUsers().get(0).getDateJoined(), "181831033883", testAPI.getSections().get(0), 2));

                } else if (binding.txtLoginEmail.getEditText().getText().toString().equals("t")){

                    // If its a teacher
                    currentUser.setUserType(Utils.TEACHER_ACCOUNT);
                    currentUser.setCurrentTeacher(new Teacher(testAPI.getUsers().get(1).getId(), testAPI.getUsers().get(1).getEmail(), testAPI.getUsers().get(1).getFirstName(), testAPI.getUsers().get(1).getLastName(), testAPI.getUsers().get(1).getDateJoined(), "MCB"));

                } else if (binding.txtLoginEmail.getEditText().getText().toString().equals("a") ){

                    // If its an admin
                    currentUser.setUserType(Utils.ADMIN_ACCOUNT);
                    currentUser.setCurrentAdmin(new Admin(5, "email5@me.com", "User5", "User5", new Date()));

                }
                startActivity(new Intent(LoginActivity.this, NavigationActivity.class));
                finish();
            }
        });
    }
}