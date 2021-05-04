package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityAddTeacherBinding;

import java.util.Arrays;
import java.util.List;

public class AddTeacherActivity extends AppCompatActivity {

    private ActivityAddTeacherBinding binding;

    private String grade;
    private List<String> grades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTeacherBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Get the action type (Add/Update)
        Intent intent = getIntent();
        String action = intent.getStringExtra(Utils.ACTION);

        setupGradesSpinner();

        switch (action){
            case Utils.ACTION_ADD:
                binding.btnSave.setOnClickListener(v -> {
                    if (validateFirstName() & validateLastName() & validateEmail() & validatePassword() & grade != null){
                        String firstName = binding.txtFirstName.getEditText().getText().toString().trim();
                        String lastName = binding.txtLastName.getEditText().getText().toString().trim();
                        String email = binding.txtEmail.getEditText().getText().toString().trim();
                        String password = binding.txtPassword.getEditText().getText().toString().trim();

                        Toast.makeText(AddTeacherActivity.this, "Save", Toast.LENGTH_SHORT).show();
                    } else {
                        if (grade == null){
                            binding.txtGradeLayout.setError(getString(R.string.empty_grade_msg));
                        }
                    }
                });
                break;
            case Utils.ACTION_UPDATE:
                // Change title
                binding.title.setText(R.string.update_teacher);
                // Hide password field
                binding.txtPassword.setVisibility(View.GONE);
                // Get teacher info
                Teacher teacher = intent.getParcelableExtra(Utils.TEACHER);
                fillFields(teacher);
                // Save button
                binding.btnSave.setOnClickListener(v -> {
                    if (validateFirstName() & validateLastName() & validateEmail() & grade != null){
                        String firstName = binding.txtFirstName.getEditText().getText().toString().trim();
                        String lastName = binding.txtLastName.getEditText().getText().toString().trim();
                        String email = binding.txtEmail.getEditText().getText().toString().trim();

                        if (!teacher.getFirstName().equals(firstName) ||
                                !teacher.getLastName().equals(lastName) ||
                                !teacher.getEmail().equals(email) ||
                                !teacher.getGrade().equals(grade)){
                            Toast.makeText(AddTeacherActivity.this, "Save", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, getString(R.string.no_changes_msg), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (grade == null){
                            binding.txtGradeLayout.setError(getString(R.string.empty_grade_msg));
                        }
                    }
                });
                break;
        }

        binding.txtGrade.setOnItemClickListener((parent, view1, position, id) -> {
            binding.txtGradeLayout.setError(null);
            grade = grades.get(position);
        });

        binding.btnClose.setOnClickListener(v -> finish());
    }

    private void setupGradesSpinner() {
        grades = Arrays.asList(getResources().getStringArray(R.array.grades));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddTeacherActivity.this, R.layout.dropdown_item, grades);
        binding.txtGrade.setAdapter(arrayAdapter);
    }

    private void fillFields(Teacher teacher) {
        binding.txtFirstName.getEditText().setText(teacher.getFirstName());
        binding.txtLastName.getEditText().setText(teacher.getLastName());
        binding.txtEmail.getEditText().setText(teacher.getEmail());
        grade = teacher.getGrade();
        binding.txtGrade.setText(grade, false);
    }

    // Validation methods
    public boolean validateFirstName(){
        String firstName = binding.txtFirstName.getEditText().getText().toString().trim();

        if (firstName.isEmpty()){
            binding.txtFirstName.setError(getString(R.string.first_name_msg));
            return false;
        } else {
            binding.txtFirstName.setError(null);
            return true;
        }
    }

    public boolean validateLastName(){
        String lastName = binding.txtLastName.getEditText().getText().toString().trim();

        if (lastName.isEmpty()){
            binding.txtLastName.setError(getString(R.string.last_name_msg));
            return false;
        } else {
            binding.txtLastName.setError(null);
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
            binding.txtPassword.setError(getString(R.string.password_msg));
            return false;
        } else if (password.length() < 6) {
            binding.txtPassword.setError(getString(R.string.password_msg2));
            return false;
        } else {
            binding.txtPassword.setError(null);
            return true;
        }
    }
}