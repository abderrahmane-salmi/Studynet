package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.salmi.bouchelaghem.studynet.Adapters.AssignmentsAdapter;
import com.salmi.bouchelaghem.studynet.Models.Assignment;
import com.salmi.bouchelaghem.studynet.Models.Department;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityAddTeacherBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddTeacherActivity extends AppCompatActivity {

    private ActivityAddTeacherBinding binding;

    private String grade;
    private List<String> grades;

    private String department;
    private List<String> departments;

    private String[] sectionsArray; // All sections as an array
    private List<String> selectedSections; // The sections selected by the user
    private boolean[] sectionsStates; // We need this just for the dialog
    private boolean sectionsSelected = false;

    // Recycler view
    private List<Assignment> assignments;
    private AssignmentsAdapter adapter;

    private int step = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTeacherBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Get the action type (Add/Update)
        Intent intent = getIntent();
        String action = intent.getStringExtra(Utils.ACTION);

        // Init spinners
        setupGradesSpinner();
        setupDepartmentsSpinner();

        switch (action){
            case Utils.ACTION_ADD:
                binding.btnNext.setOnClickListener(v -> {
                    switch (step){
                        case 1: // Step1: Fill the teacher's basic info
//                            if (validateFirstName() & validateLastName() & validateEmail() & validatePassword() & grade != null & department != null & sectionsSelected){
//                                step = 2;
//                                binding.teacherInfoLayout.setVisibility(View.GONE);
//                                binding.assignmentsRecView.setVisibility(View.VISIBLE);
//                                binding.btnAdd.setVisibility(View.VISIBLE);
//                                // Show back button
//                                binding.btnStepBack.setVisibility(View.VISIBLE);
//                                // Show empty msg
//                                binding.emptyMsg.setVisibility(View.VISIBLE);
//                            } else {
//                                if (grade == null){
//                                    binding.txtGradeLayout.setError(getString(R.string.empty_grade_msg));
//                                }
//                                if (department == null){
//                                    binding.txtDepartmentLayout.setError(getString(R.string.empty_msg1));
//                                }
//                                if (grade == null){
//                                    binding.txtSectionsList.setError("");
//                                }
//                            }
                            // Go to the next step
                            step = 2;
                            binding.teacherInfoLayout.setVisibility(View.GONE);
                            binding.assignmentsRecView.setVisibility(View.VISIBLE);
                            binding.btnAdd.setVisibility(View.VISIBLE);
                            // Show back button
                            binding.btnStepBack.setVisibility(View.VISIBLE);
                            // Show empty msg
                            binding.emptyMsg.setVisibility(View.VISIBLE);
                            break;
                        case 2: // Step 2: Add assignments
                            // Save the teacher's info
                            String firstName = binding.txtFirstName.getEditText().getText().toString().trim();
                            String lastName = binding.txtLastName.getEditText().getText().toString().trim();
                            String email = binding.txtEmail.getEditText().getText().toString().trim();
                            String password = binding.txtPassword.getEditText().getText().toString().trim();

                            Toast.makeText(AddTeacherActivity.this, "Save", Toast.LENGTH_SHORT).show();
                            break;
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
                binding.btnNext.setOnClickListener(v -> {
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

        binding.txtGradeSpinner.setOnItemClickListener((parent, view1, position, id) -> {
            binding.txtGradeLayout.setError(null);
            grade = grades.get(position);
        });

        binding.txtDepartmentSpinner.setOnItemClickListener((parent, view12, position, id) -> {
            binding.txtDepartmentLayout.setError(null);
            department = departments.get(position);

            // Enable and init sections spinner
            binding.txtSectionsList.setEnabled(true);
            binding.txtSectionsList.setText("");
            setupSectionsSpinner(department);
        });

        binding.txtSectionsList.setOnClickListener(v -> {
            binding.txtSectionsList.setError(null);
            // Init builder
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AddTeacherActivity.this, R.style.MyAlertDialogTheme);
            // Set title
            builder.setTitle(R.string.select_section);
            // No cancel
            builder.setCancelable(false);

            builder.setMultiChoiceItems(sectionsArray, sectionsStates, (dialog, which, isChecked) -> {

                // Get the current item
                String currentSection = sectionsArray[which];
                if (isChecked) { // If its selected then add it to the selected items list
                    selectedSections.add(currentSection);
                } else { // if not then remove it from the list
                    selectedSections.remove(currentSection);
                }
            });

            builder.setPositiveButton(R.string.save, (dialog, which) -> {

                binding.txtSectionsList.setText("");

                if (!selectedSections.isEmpty()) {
                    sectionsSelected = true;
                    for (int i = 0; i < selectedSections.size() - 1; i++) {
                        // Show the selected groups in the text view
                        binding.txtSectionsList.append(selectedSections.get(i) + ", ");
                    }
                    binding.txtSectionsList.append(selectedSections.get(selectedSections.size() - 1));
                } else {
                    sectionsSelected = false;
                    binding.txtSectionsList.setHint(R.string.group);
                }
            });

            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

            builder.show();

        });

        binding.btnClose.setOnClickListener(v -> finish());

        binding.btnStepBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (step == 2){
                    step = 1;
                    // Hide step2
                    binding.assignmentsRecView.setVisibility(View.GONE);
                    binding.emptyMsg.setVisibility(View.GONE);
                    binding.btnAdd.setVisibility(View.GONE);
                    binding.btnStepBack.setVisibility(View.INVISIBLE);
                    // Show step1
                    binding.teacherInfoLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // Get all the sections in the selected department
    private void setupSectionsSpinner(String department) {
        // Get sections
        List<Section> sectionList = TestAPI.getInstance().getSections();

        // Init lists
        sectionsArray = new String[sectionList.size()];
        sectionsStates = new boolean[sectionList.size()];
        selectedSections = new ArrayList<>();

        // Fill the sections array
        for (int i=0; i<sectionList.size(); i++){
            sectionsArray[i] = sectionList.get(i).getCode();
        }
    }

    private void setupDepartmentsSpinner() {
        departments = new ArrayList<>();
        for (Department department: TestAPI.getInstance().getDepartments()){
            departments.add(department.getCode());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddTeacherActivity.this, R.layout.dropdown_item, departments);
        binding.txtDepartmentSpinner.setAdapter(arrayAdapter);
    }

    private void setupGradesSpinner() {
        grades = Arrays.asList(getResources().getStringArray(R.array.grades));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddTeacherActivity.this, R.layout.dropdown_item, grades);
        binding.txtGradeSpinner.setAdapter(arrayAdapter);
    }

    private void fillFields(Teacher teacher) {
        binding.txtFirstName.getEditText().setText(teacher.getFirstName());
        binding.txtLastName.getEditText().setText(teacher.getLastName());
        binding.txtEmail.getEditText().setText(teacher.getEmail());

        grade = teacher.getGrade();
        binding.txtGradeSpinner.setText(grade, false);

        department = "INFO"; // TODO: replace this with the teacher's department
        binding.txtDepartmentSpinner.setText(department, false);

        // Get all sections (of the teacher's department) and setup the spinner
        binding.txtSectionsList.setEnabled(true);
        setupSectionsSpinner(department);

        // Set the selected sections
        selectedSections = teacher.getSections();
        sectionsSelected = true;
        setSelectedSections(selectedSections);

        // Set the selected sections to the text view
        int nbSections = selectedSections.size();
        if (nbSections == 0){
            binding.txtSectionsList.setText(R.string.no_sections_selected_msg);
        } else if (nbSections == 1){
            binding.txtSectionsList.setText(selectedSections.get(0));
        } else {
            binding.txtSectionsList.setText("");
            for (int i = 0; i< selectedSections.size()-1; i++){
                // Show the selected sections in the text view
                binding.txtSectionsList.append(selectedSections.get(i) + ", ");
            }
            binding.txtSectionsList.append(selectedSections.get(nbSections-1));
        }

    }

    private void setSelectedSections(List<String> selectedSections) {
        for (int i=0; i<sectionsArray.length; i++){
            sectionsStates[i] = selectedSections.contains(sectionsArray[i]);
        }
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