package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.databinding.ActivityNavigationBinding;
import com.salmi.bouchelaghem.studynet.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnGoBackFromSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUsername() & validateEmail() & validatePassword()){

                }
            }
        });
    }

    public boolean validateUsername(){
        String username = binding.txtUsername.getEditText().getText().toString().trim();

        if (username.isEmpty()){
            binding.txtUsername.setError(getString(R.string.username_msg1));
            return false;
        } else if (username.length() > 20) {
            binding.txtUsername.setError(getString(R.string.username_msg2));
            return false;
        } else {
            binding.txtUsername.setError(null);
            return true;
        }
    }

    public boolean validateEmail(){
        String email = binding.txtEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()){
            binding.txtEmail.setError(getString(R.string.email_msg1));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtEmail.setError(getString(R.string.email_msg2));
            return false;
        } else {
            binding.txtEmail.setError(null);
            return true;
        }
    }

    public boolean validatePassword(){
        String password = binding.txtPassword.getEditText().getText().toString().trim();

        if (password.isEmpty()){
            binding.txtPassword.setError("Password can't be empty");
            return false;
        } else if (password.length() < 6) {
            binding.txtPassword.setError("Password too short");
            return false;
        } else {
            binding.txtPassword.setError(null);
            return true;
        }
    }
}