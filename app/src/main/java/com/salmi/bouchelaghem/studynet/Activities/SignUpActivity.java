package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.salmi.bouchelaghem.studynet.Models.Department;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Specialty;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.databinding.ActivityNavigationBinding;
import com.salmi.bouchelaghem.studynet.databinding.ActivitySignUpBinding;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private List<Department> departments;
    private List<Specialty> specialties;
    private List<Section> sections;

    // Flags
    private boolean departmentSelected = false;
    private boolean specialitySelected = false;
    private boolean sectionSelected = false;

    // Fields
    private String department, speciality, section;

    // Test Api
    TestAPI testAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Test Api
        testAPI = TestAPI.getInstance();

        // Setup departments list
        getAllDepartments();

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUsername() & validateEmail() & validatePassword()){

                }
            }
        });

        // When the user chooses a department
        binding.txtDepartment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                departmentSelected = true;
                department = departments.get(position).getName();
                binding.departmentTextInputLayout.setError(null);

                // Disable other spinners
                binding.txtSpeciality.setText("");
                binding.txtSection.setEnabled(false);
                binding.txtSection.setText("");
                binding.txtGroup.setEnabled(false);
                binding.txtGroup.setText("");

                // Set up the specialities spinner
                binding.txtSpeciality.setEnabled(true);
                setupSpecialitiesSpinner(departments.get(position));
            }
        });

        // When the user chooses a speciality
        binding.txtSpeciality.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                specialitySelected = true;
                speciality = specialties.get(position).getName();
                binding.specialityTextInputLayout.setError(null);

                // Disable other spinners
                binding.txtSection.setText("");
                binding.txtGroup.setEnabled(false);
                binding.txtGroup.setText("");

                // Set up the sections spinner
                binding.txtSection.setEnabled(true);
                setupSectionsSpinner(specialties.get(position));
            }
        });

        // When the user chooses a section
        binding.txtSection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                sectionSelected = true;
                section = sections.get(position).getCode();
                binding.sectionTextInputLayout.setError(null);

                // Disable other spinners
                binding.txtGroup.setText("");

                // Set up the groups spinner
                binding.txtGroup.setEnabled(true);
                setupGroupsSpinner(sections.get(position).getNbGroups());
            }
        });

    }

    private void setupGroupsSpinner(int nbGroups) {
        List<String> groups = new ArrayList<>();
        for (int i=1; i<=nbGroups; i++){
            groups.add(String.valueOf(i));
        }

        // Set up the spinner
        ArrayAdapter<String> groupsAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, groups);
        binding.txtGroup.setAdapter(groupsAdapter);
    }

    private void setupSectionsSpinner(Specialty specialty) {
        // Get all the sections for the selected spec
        sections = new ArrayList<>();
        for (Section s:testAPI.getSections()){
            if (s.getSpecialty() == specialty){
                sections.add(s);
            }
        }

        if (!sections.isEmpty()){

            // Get names
            List<String> sectionsNames = new ArrayList<>();
            for (Section s:sections){
                sectionsNames.add(s.getCode());
            }

            // Set up the spinner
            ArrayAdapter<String> sectionsAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, sectionsNames);
            binding.txtSection.setAdapter(sectionsAdapter);

        } else {
            // No sections
        }
    }

    private void setupSpecialitiesSpinner(Department department) {
        // Get all the specialities for the selected department
        specialties = new ArrayList<>();
        for (Specialty s:testAPI.getSpecialties()){
            if (s.getDepartment() == department){
                specialties.add(s);
            }
        }

        if (!specialties.isEmpty()){

            // Get names
            List<String> specialitiesNames = new ArrayList<>();
            for (Specialty s:specialties){
                specialitiesNames.add(s.getCode());
            }

            // Set up the spinner
            ArrayAdapter<String> specialitiesAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, specialitiesNames);
            binding.txtSpeciality.setAdapter(specialitiesAdapter);

        } else {
            // No specialities
        }
    }

    private void getAllDepartments() {
        departments = new ArrayList<>();
        departments = testAPI.getDepartments();

        // Get the names of the departments
        List<String> departmentsNames = new ArrayList<>();
        for (Department department:departments){
            departmentsNames.add(department.getName());
        }

        // Set up spinner
        ArrayAdapter<String> departmentsAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, departmentsNames);
        binding.txtDepartment.setAdapter(departmentsAdapter);
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
        return true;
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