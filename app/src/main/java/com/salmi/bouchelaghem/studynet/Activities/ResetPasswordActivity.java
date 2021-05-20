package com.salmi.bouchelaghem.studynet.Activities;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.salmi.bouchelaghem.studynet.Fragments.ResetPasswordConfirmationFragment;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.databinding.ActivityResetPasswordBinding;

public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnGoBackFromReset.setOnClickListener(v -> finish());

        binding.btnResetPassword.setOnClickListener(v -> {
            if (validateEmail()){
                String email = binding.txtEmail.getEditText().getText().toString().trim();
                // TODO: Send reset password email
                // Show the confirmation fragment after you send the email
                // use: showConfirmationFragment() to show the fragment
                Toast.makeText(ResetPasswordActivity.this, "Done", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnConfirmReset.setOnClickListener(v -> showConfirmationFragment());
    }

    private void showConfirmationFragment(){
        ResetPasswordConfirmationFragment fragment = new ResetPasswordConfirmationFragment();
        fragment.show(getSupportFragmentManager(), "ResetPasswordConfirmationFragment");
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