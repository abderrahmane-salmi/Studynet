package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityLoginBinding;
import com.salmi.bouchelaghem.studynet.databinding.ActivityNavigationBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private CurrentUser currentUser = CurrentUser.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

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
                currentUser.setUserType(Utils.STUDENT_ACCOUNT);
                if (binding.txtLoginEmail.getEditText().getText().toString().equals("e") &&
                        binding.txtLoginPassword.getEditText().getText().toString().equals("e") ){
                    currentUser.setUserType(Utils.STUDENT_ACCOUNT);
                } else if (binding.txtLoginEmail.getEditText().getText().toString().equals("t") &&
                        binding.txtLoginPassword.getEditText().getText().toString().equals("t") ){
                    currentUser.setUserType(Utils.TEACHER_ACCOUNT);
                } else if (binding.txtLoginEmail.getEditText().getText().toString().equals("a") &&
                        binding.txtLoginPassword.getEditText().getText().toString().equals("a") ){
                    currentUser.setUserType(Utils.ADMIN_ACCOUNT);
                }
                startActivity(new Intent(LoginActivity.this, NavigationActivity.class));
                finish();
            }
        });
    }
}