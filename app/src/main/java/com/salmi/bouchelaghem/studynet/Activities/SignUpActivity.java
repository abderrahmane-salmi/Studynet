package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.Models.Department;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Specialty;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.Serializers;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivitySignUpBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private List<Department> departments;
    private List<Specialty> specialties;
    private List<Section> sections;

    // Flags
    private boolean departmentSelected = false;
    private boolean specialitySelected = false;
    private boolean sectionSelected = false;
    private boolean groupSelected = false;

    // Fields
    private int group;
    private Section studentSection;

    private CustomLoadingDialog loadingDialog;

    // Studynet Api
    private StudynetAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        loadingDialog = new CustomLoadingDialog(SignUpActivity.this);

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api, this will implement the code of all the methods in the interface
        api = retrofit.create(StudynetAPI.class);

        // Setup departments list
        getAllDepartments();

        binding.btnSignUp.setOnClickListener(v -> performSignup());

        // When the user chooses a department
        binding.txtDepartment.setOnItemClickListener((parent, view14, position, id) -> {
            // Get the selected item
            departmentSelected = true;
            binding.departmentTextInputLayout.setError(null);

            // Disable other spinners
            binding.txtSpeciality.setText("");
            specialitySelected = false;

            binding.sectionTextInputLayout.setEnabled(false);
            binding.txtSection.setText("");
            sectionSelected = false;

            binding.groupTextInputLayout.setEnabled(false);
            binding.txtGroup.setText("");
            groupSelected = false;

            // Set up the specialities spinner
            binding.specialityTextInputLayout.setEnabled(true);
            setupSpecialitiesSpinner(departments.get(position));
        });

        // When the user chooses a speciality
        binding.txtSpeciality.setOnItemClickListener((parent, view13, position, id) -> {
            // Get the selected item
            specialitySelected = true;
            binding.specialityTextInputLayout.setError(null);

            // Disable other spinners
            binding.txtSection.setText("");
            sectionSelected = false;

            binding.groupTextInputLayout.setEnabled(false);
            binding.txtGroup.setText("");
            groupSelected = false;

            // Set up the sections spinner
            binding.sectionTextInputLayout.setEnabled(true);
            setupSectionsSpinner(specialties.get(position));
        });

        // When the user chooses a section
        binding.txtSection.setOnItemClickListener((parent, view12, position, id) -> {
            // Get the selected item
            sectionSelected = true;
            studentSection = sections.get(position);
            binding.sectionTextInputLayout.setError(null);

            // Disable other spinners
            binding.txtGroup.setText("");
            groupSelected = false;

            // Set up the groups spinner
            binding.groupTextInputLayout.setEnabled(true);
            setupGroupsSpinner(sections.get(position).getNbGroups());
        });

        // When the user chooses a group
        binding.txtGroup.setOnItemClickListener((parent, view1, position, id) -> {
            // Get the selected item
            groupSelected = true;
            group = position + 1;
            binding.groupTextInputLayout.setError(null);
        });

    }

    private void setupGroupsSpinner(int nbGroups) {
        List<String> groups = new ArrayList<>();
        for (int i = 1; i <= nbGroups; i++) {
            groups.add(String.valueOf(i));
        }

        // Set up the spinner
        ArrayAdapter<String> groupsAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, groups);
        binding.txtGroup.setAdapter(groupsAdapter);
    }

    // Get all the sections for the selected spec
    private void setupSectionsSpinner(Specialty s) {
        Call<List<Section>> call = api.getSpecialitySections(s.getCode());
        call.enqueue(new Callback<List<Section>>() {
            @Override
            public void onResponse(@NonNull Call<List<Section>> call, @NonNull Response<List<Section>> response) {
                if (response.isSuccessful()) {
                    sections = response.body();

                    // Get names
                    List<String> sectionsNames = new ArrayList<>();
                    for (Section sec : sections) {
                        sectionsNames.add(sec.getCode());
                    }

                    // Set up the spinner
                    ArrayAdapter<String> sectionsAdapter = new ArrayAdapter<>(SignUpActivity.this, R.layout.dropdown_item, sectionsNames);
                    binding.txtSection.setAdapter(sectionsAdapter);
                } else {
                    Toast.makeText(SignUpActivity.this, getString(R.string.error) + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Section>> call, @NonNull Throwable t) {
                Toast.makeText(SignUpActivity.this, getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Get all the specialities for the selected department
    private void setupSpecialitiesSpinner(Department department) {
        Call<List<Specialty>> call = api.getSpecialities(department.getCode());
        call.enqueue(new Callback<List<Specialty>>() {
            @Override
            public void onResponse(@NonNull Call<List<Specialty>> call, @NonNull Response<List<Specialty>> response) {
                if (response.isSuccessful()) {
                    specialties = response.body();

                    // Get names
                    List<String> specialitiesNames = new ArrayList<>();
                    for (Specialty s : specialties) {
                        specialitiesNames.add(s.getCode());
                    }

                    // Set up the spinner
                    ArrayAdapter<String> specialitiesAdapter = new ArrayAdapter<>(SignUpActivity.this, R.layout.dropdown_item, specialitiesNames);
                    binding.txtSpeciality.setAdapter(specialitiesAdapter);
                } else {
                    Toast.makeText(SignUpActivity.this, getString(R.string.error) + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Specialty>> call, @NonNull Throwable t) {
                Toast.makeText(SignUpActivity.this, getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllDepartments() {
        // Get the departments from the api
        Call<List<Department>> call = api.getDepartments();
        call.enqueue(new Callback<List<Department>>() {
            @Override
            public void onResponse(@NonNull Call<List<Department>> call, @NonNull Response<List<Department>> response) {
                if (response.isSuccessful()) {
                    departments = response.body();

                    // Get the names of the departments
                    List<String> departmentsNames = new ArrayList<>();
                    for (Department department : departments) {
                        departmentsNames.add(department.getName());
                    }

                    // Set up spinner
                    ArrayAdapter<String> departmentsAdapter = new ArrayAdapter<>(SignUpActivity.this, R.layout.dropdown_item, departmentsNames);
                    binding.txtDepartment.setAdapter(departmentsAdapter);
                } else {
                    Toast.makeText(SignUpActivity.this, getString(R.string.error) + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Department>> call, @NonNull Throwable t) {
                Toast.makeText(SignUpActivity.this, getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validateRegistrationNumber() {
        String regNumber = binding.txtRegistrationNumber.getEditText().getText().toString().trim();

        if (regNumber.isEmpty()) {
            binding.txtRegistrationNumber.setError(getString(R.string.empty_reg_number_msg));
            return false;
        } else {
            binding.txtRegistrationNumber.setError(null);
            return true;
        }
    }

    public boolean validateFirstName() {
        String firstName = binding.txtFirstName.getEditText().getText().toString().trim();

        if (firstName.isEmpty()) {
            binding.txtFirstName.setError(getString(R.string.empty_first_name_msg));
            return false;
        } else {
            binding.txtFirstName.setError(null);
            return true;
        }
    }

    public boolean validateLastName() {
        String lastName = binding.txtLastName.getEditText().getText().toString().trim();

        if (lastName.isEmpty()) {
            binding.txtLastName.setError(getString(R.string.empty_last_name_msg));
            return false;
        } else {
            binding.txtLastName.setError(null);
            return true;
        }
    }

    public boolean validateEmail() {
        String email = binding.txtEmail.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
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

    public boolean validatePassword() {
        String password = binding.txtPassword.getEditText().getText().toString().trim();

        if (password.isEmpty()) {
            binding.txtPassword.setError(getString(R.string.empty_password_msg));
            return false;
        } else if (password.length() < 6) {
            binding.txtPassword.setError(getString(R.string.password_msg2));
            return false;
        } else {
            binding.txtPassword.setError(null);
            return true;
        }
    }

    private void performSignup() {
        if (validateRegistrationNumber() & validateFirstName() & validateLastName() & validateEmail() & validatePassword() &
                departmentSelected & specialitySelected & sectionSelected & groupSelected) {

            loadingDialog.show();
            String registrationNumber = binding.txtRegistrationNumber.getEditText().getText().toString().trim();
            String firstName = binding.txtFirstName.getEditText().getText().toString().trim();
            String lastName = binding.txtLastName.getEditText().getText().toString().trim();
            String email = binding.txtEmail.getEditText().getText().toString().trim();
            //Get the raw password (no trim either).
            String password = binding.txtPassword.getEditText().getText().toString();
            //Create the json data to send to the api.
            JsonObject studentData = Serializers.studentSerializer(email, password, firstName, lastName, registrationNumber, studentSection.getCode(), group);
            //Send the data to the API.
            Call<JsonObject> studentRegister = api.registerStudent(studentData);
            studentRegister.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    switch (response.code()) {
                        case Utils.HttpResponses.HTTP_201_CREATED:
                            //The student has been successfully registered, we log him in using the data that was sent back by the API.
                            Utils.loginStudent(response.body());
                            //We save his data locally.
                            saveCurrentUser();
                            //Take him to the navigation activity.
                            Toast.makeText(SignUpActivity.this, getString(R.string.signed_up_msg), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, NavigationActivity.class));
                            loadingDialog.dismiss();
                            finish();
                            break;
                        case Utils.HttpResponses.HTTP_400_BAD_REQUEST:
                            //The email is already taken.
                            binding.txtEmail.setError(getString(R.string.email_taken));
                            loadingDialog.dismiss();
                            break;
                        default:
                            Toast.makeText(SignUpActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                            break;
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    loadingDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, getString(R.string.connection_failed), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            if (!departmentSelected) {
                binding.departmentTextInputLayout.setError(getString(R.string.empty_department_msg));
            }
            if (!specialitySelected) {
                binding.specialityTextInputLayout.setError(getString(R.string.empty_speciality_msg));
            }
            if (!sectionSelected) {
                binding.sectionTextInputLayout.setError(getString(R.string.empty_section_msg));
            }
            if (!groupSelected) {
                binding.groupTextInputLayout.setError(getString(R.string.empty_group_msg));
            }
        }
    }

    private void saveCurrentUser() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Utils.SHARED_PREFERENCES_USER_DATA, MODE_PRIVATE);
        CurrentUser currentUser = CurrentUser.getInstance();

        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        //Save the token
        prefsEditor.putString(Utils.SHARED_PREFERENCES_TOKEN, currentUser.getToken());
        prefsEditor.putBoolean(Utils.SHARED_PREFERENCES_LOGGED_IN, true);
        prefsEditor.apply();
    }
}