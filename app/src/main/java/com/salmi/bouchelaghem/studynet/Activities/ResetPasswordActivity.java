package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.databinding.ActivityLoginBinding;
import com.salmi.bouchelaghem.studynet.databinding.ActivityResetPasswordBinding;

public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnGoBackFromReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail()){
                    String email = binding.txtEmail.getEditText().getText().toString().trim();

                    Toast.makeText(ResetPasswordActivity.this, "Done", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
}