package com.salmi.bouchelaghem.studynet.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.Adapters.AssignmentsAdapter;
import com.salmi.bouchelaghem.studynet.Models.Assignment;
import com.salmi.bouchelaghem.studynet.Models.Department;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.Serializers;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityAddTeacherBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddTeacherActivity extends AppCompatActivity {

    private ActivityAddTeacherBinding binding;

    private String grade;
    private List<String> grades;

    private String department;
    private List<String> departments;

    private String[] sectionsArray; // All sections as an array
    private ArrayList<String> selectedSections; // The sections selected by the user
    private boolean[] sectionsStates; // We need this just for the dialog
    private boolean sectionsSelected = false;

    // Recycler view
    private ArrayList<Assignment> assignments;
    private AssignmentsAdapter adapter;

    // Studynet Api
    private StudynetAPI api;
    private final CurrentUser currentUser = CurrentUser.getInstance();

    private static Teacher teacher;

    private int step = 1;

    private CustomLoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTeacherBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Init loading dialog
        loadingDialog = new CustomLoadingDialog(this);

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api
        api = retrofit.create(StudynetAPI.class);

        initRecView();

        // Get the action type (Add/Update)
        Intent intent = getIntent();
        String action = intent.getStringExtra(Utils.ACTION);

        // Init spinners
        setupGradesSpinner();
        setupDepartmentsSpinner();

        switch (action) {
            case Utils.ACTION_ADD:
                binding.btnNext.setOnClickListener(v -> {
                    switch (step) {
                        case 1: // Step1: Fill the teacher's basic info
                            if (validateFirstName() & validateLastName() & validateEmail() & validatePassword() & grade != null & department != null & sectionsSelected){

                                String email = binding.txtEmail.getEditText().getText().toString().trim();
                                // Check if the email is used
                                JsonObject emailJson = new JsonObject();
                                emailJson.addProperty("email",email);
                                Call<ResponseBody> call = api.checkEmail(emailJson,"Token " + currentUser.getToken());
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                                        if (response.code() == Utils.HttpResponses.HTTP_302_FOUND){
                                            // The email is already used
                                            binding.txtEmail.setError(getString(R.string.email_taken));
                                        } else if (response.code() == Utils.HttpResponses.HTTP_200_OK) {
                                            // Go to the next step
                                            step = 2;
                                            binding.teacherInfoLayout.setVisibility(View.GONE);
                                            binding.assignmentsRecView.setVisibility(View.VISIBLE);
                                            binding.btnAdd.setVisibility(View.VISIBLE);
                                            // Show back button
                                            binding.btnNext.setText(R.string.save);
                                            binding.btnStepBack.setVisibility(View.VISIBLE);
                                            // Show empty msg
                                            binding.emptyMsg.setVisibility(View.VISIBLE);

                                            // Save the teacher's info
                                            String firstName = binding.txtFirstName.getEditText().getText().toString().trim();
                                            String lastName = binding.txtLastName.getEditText().getText().toString().trim();
                                            String email = binding.txtEmail.getEditText().getText().toString().trim();

                                            if (teacher != null){
                                                // This means that the user already clicked next at least once
                                                // Check if the user changed the teacher's sections
                                                if (!teacher.getSections().equals(selectedSections)){
                                                    // This means that the user changed the sections
                                                    deleteUnusedAssignments(selectedSections);
                                                }
                                            }

                                            teacher = new Teacher();
                                            teacher.setId(-1);
                                            teacher.setFirstName(firstName);
                                            teacher.setLastName(lastName);
                                            teacher.setEmail(email);
                                            teacher.setAssignments(assignments);
                                            teacher.setSections(selectedSections);
                                            teacher.setGrade(grade);
                                            teacher.setDepartment(department);

                                            getAssignments();

                                        } else {
                                            // There is a problem with this email
                                            binding.txtEmail.setError(getString(R.string.email_msg3));
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                                        Toast.makeText(AddTeacherActivity.this, getString(R.string.error)+t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                if (grade == null){
                                    binding.txtGradeLayout.setError(getString(R.string.empty_grade_msg));
                                }
                                if (department == null){
                                    binding.txtDepartmentLayout.setError(getString(R.string.empty_department_msg));
                                }
                                if (!sectionsSelected){
                                    binding.txtSectionsList.setError("");
                                }
                            }
                            // Go to the next step
//                            step = 2;
//                            binding.teacherInfoLayout.setVisibility(View.GONE);
//                            binding.assignmentsRecView.setVisibility(View.VISIBLE);
//                            binding.btnAdd.setVisibility(View.VISIBLE);
//                            // Show back button
//                            binding.btnStepBack.setVisibility(View.VISIBLE);
//                            // Show empty msg
//                            binding.emptyMsg.setVisibility(View.VISIBLE);
                            break;
                        case 2: // Step 2: Add assignments
                            // Save the teacher's info in the api
                            //TODO: make the api teacher creation call here
                            String password = binding.txtPassword.getEditText().getText().toString().trim();
                            //Build the teacher json.
                            JsonObject teacherJson = Serializers.CreateTeacherSerializer(teacher,password);
                            //Make the call to the api.
                            Call<JsonObject> createTeacherCall = api.createTeacher(teacherJson,"Token " + currentUser.getToken());
                            loadingDialog.show();
                            createTeacherCall.enqueue(new TeacherCreationCallback());
                            break;
                    }
                });

                // Add new assignment
                binding.btnAdd.setOnClickListener(v -> {
                    Intent intent1 = new Intent(AddTeacherActivity.this, AddAssignmentActivity.class);
                    intent1.putExtra(Utils.ACTION, Utils.ACTION_ADD);
                    // TODO: get the teacher id
                    intent1.putExtra(Utils.ID, 1);
                    intent1.putExtra(Utils.SECTIONS, selectedSections);
                    startActivity(intent1);
                });
                break;
            case Utils.ACTION_UPDATE:
                // Change title
                binding.title.setText(R.string.update_teacher);
                // Hide password field
                binding.txtPassword.setVisibility(View.GONE);
                // Get teacher info
                teacher = intent.getParcelableExtra(Utils.TEACHER);
                fillFields(teacher);
                // Save button
                binding.btnNext.setOnClickListener(v -> {
                    switch (step) {
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

                            // (for later)
//                if (!teacher.getFirstName().equals(firstName) ||
//                        !teacher.getLastName().equals(lastName) ||
//                        !teacher.getEmail().equals(email) ||
//                        !teacher.getGrade().equals(grade)){
//                    Toast.makeText(AddTeacherActivity.this, "Save", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, getString(R.string.no_changes_msg), Toast.LENGTH_SHORT).show();
//                }
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
                            binding.btnNext.setText(R.string.save);
                            binding.btnStepBack.setVisibility(View.VISIBLE);
                            // Show current teacher's assignments
                            getAssignments();
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

                // Add new assignment
                binding.btnAdd.setOnClickListener(v -> {
                    Intent intent1 = new Intent(AddTeacherActivity.this, AddAssignmentActivity.class);
                    intent1.putExtra(Utils.ACTION, Utils.ACTION_ADD);
                    intent1.putExtra(Utils.ID, teacher.getId());
                    intent1.putExtra(Utils.SECTIONS, selectedSections);
                    startActivity(intent1);
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
            if (sectionsArray != null && sectionsArray.length != 0){
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
                        binding.txtSectionsList.setHint(R.string.sections);
                    }
                });

                builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

                builder.show();
            } else {
                Toast.makeText(this, getString(R.string.no_sections_in_department), Toast.LENGTH_SHORT).show();
            }

        });

        binding.btnClose.setOnClickListener(v -> finish());

        binding.btnStepBack.setOnClickListener(v -> {
            if (step == 2) {
                step = 1;
                // Hide step2
                binding.assignmentsRecView.setVisibility(View.GONE);
                binding.emptyMsg.setVisibility(View.GONE);
                binding.btnAdd.setVisibility(View.GONE);
                binding.btnStepBack.setVisibility(View.INVISIBLE);
                // Show step1
                binding.btnNext.setText(R.string.next);
                binding.teacherInfoLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void deleteUnusedAssignments(ArrayList<String> selectedSections) {
        for (Assignment assignment:assignments){
            if (!selectedSections.contains(assignment.getSectionCode())){
                assignments.remove(assignment);
            }
        }
    }

    private void initRecView() {
        assignments = new ArrayList<>();
        binding.assignmentsRecView.setLayoutManager(new LinearLayoutManager(AddTeacherActivity.this));
        binding.assignmentsRecView.addItemDecoration(new DividerItemDecoration(AddTeacherActivity.this, LinearLayout.VERTICAL));
        adapter = new AssignmentsAdapter(AddTeacherActivity.this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adminCallBack);
        itemTouchHelper.attachToRecyclerView(binding.assignmentsRecView);
    }

    private void getAssignments() {
        if (teacher != null){
            assignments = teacher.getAssignments();
            if (assignments != null) {
                if (!assignments.isEmpty()) {
                    adapter.setAssignments(assignments);
                    binding.assignmentsRecView.setAdapter(adapter);
                    binding.assignmentsRecView.setVisibility(View.VISIBLE);
                    binding.emptyMsg.setVisibility(View.GONE);
                } else {
                    binding.assignmentsRecView.setVisibility(View.GONE);
                    binding.emptyMsg.setVisibility(View.VISIBLE);
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        teacher = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (teacher != null){
            assignments = teacher.getAssignments();
            if(step==2) {
                if (assignments != null) {
                    if (!assignments.isEmpty()) {
                        adapter.setAssignments(assignments);
                        binding.assignmentsRecView.setAdapter(adapter);
                        binding.assignmentsRecView.setVisibility(View.VISIBLE);
                        binding.emptyMsg.setVisibility(View.GONE);
                    } else {
                        binding.assignmentsRecView.setVisibility(View.GONE);
                        binding.emptyMsg.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    // Get all the sections in the selected department
    private void setupSectionsSpinner(String department) {
        Call<List<Section>> call = api.getDepartmentSections(department);
        call.enqueue(new Callback<List<Section>>() {
            @Override
            public void onResponse(@NonNull Call<List<Section>> call, @NonNull Response<List<Section>> response) {
                if (response.isSuccessful()){
                    // Get sections
                    List<Section> sectionList = response.body();

                    // Init lists
                    assert sectionList != null;
                    sectionsArray = new String[sectionList.size()];
                    sectionsStates = new boolean[sectionList.size()];
                    selectedSections = new ArrayList<>();

                    // Fill the sections array
                    for (int i = 0; i < sectionList.size(); i++) {
                        sectionsArray[i] = sectionList.get(i).getCode();
                    }
                } else {
                    Toast.makeText(AddTeacherActivity.this, getString(R.string.error)+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Section>> call, @NonNull Throwable t) {
                Toast.makeText(AddTeacherActivity.this, getString(R.string.error)+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDepartmentsSpinner() {
        // Get the departments from the api
        Call<List<Department>> call = api.getDepartments();
        call.enqueue(new Callback<List<Department>>() {
            @Override
            public void onResponse(@NonNull Call<List<Department>> call, @NonNull Response<List<Department>> response) {
                if (response.isSuccessful()){
                    List<Department> departmentsList = response.body();

                    // Get the names of the departments
                    departments = new ArrayList<>();
                    assert departmentsList != null;
                    for (Department department:departmentsList){
                        departments.add(department.getCode());
                    }

                    // Set up spinner
                    ArrayAdapter<String> departmentsAdapter = new ArrayAdapter<>(AddTeacherActivity.this, R.layout.dropdown_item, departments);
                    binding.txtDepartmentSpinner.setAdapter(departmentsAdapter);
                } else {
                    Toast.makeText(AddTeacherActivity.this, getString(R.string.error)+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Department>> call, @NonNull Throwable t) {
                Toast.makeText(AddTeacherActivity.this, getString(R.string.error)+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

        department = teacher.getDepartment();
        binding.txtDepartmentSpinner.setText(department, false);

        // Get all sections (of the teacher's department) and setup the spinner
        binding.txtSectionsList.setEnabled(true);
        Call<List<Section>> call = api.getDepartmentSections(department);
        call.enqueue(new Callback<List<Section>>() {
            @Override
            public void onResponse(@NonNull Call<List<Section>> call, @NonNull Response<List<Section>> response) {
                if (response.isSuccessful()){
                    // Get sections
                    List<Section> sectionList = response.body();

                    // Init lists
                    assert sectionList != null;
                    sectionsArray = new String[sectionList.size()];
                    sectionsStates = new boolean[sectionList.size()];
                    selectedSections = new ArrayList<>();

                    // Fill the sections array
                    for (int i = 0; i < sectionList.size(); i++) {
                        sectionsArray[i] = sectionList.get(i).getCode();
                    }

                    // Set the selected sections
                    selectedSections = teacher.getSections();
                    sectionsSelected = true;
                    setSelectedSections(selectedSections);

                    // Set the selected sections to the text view
                    int nbSections = selectedSections.size();
                    if (nbSections == 0) {
                        binding.txtSectionsList.setText(R.string.no_sections_selected_msg);
                    } else if (nbSections == 1) {
                        binding.txtSectionsList.setText(selectedSections.get(0));
                    } else {
                        binding.txtSectionsList.setText("");
                        for (int i = 0; i < selectedSections.size() - 1; i++) {
                            // Show the selected sections in the text view
                            binding.txtSectionsList.append(selectedSections.get(i) + ", ");
                        }
                        binding.txtSectionsList.append(selectedSections.get(nbSections - 1));
                    }
                } else {
                    Toast.makeText(AddTeacherActivity.this, getString(R.string.error)+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Section>> call, @NonNull Throwable t) {
                Toast.makeText(AddTeacherActivity.this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setSelectedSections(List<String> selectedSections) {
        for (int i = 0; i < sectionsArray.length; i++) {
            sectionsStates[i] = selectedSections.contains(sectionsArray[i]);
        }
    }

    // Validation methods
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

    // Admin call back (swipe feature)
    // Swipe to delete and edit in the recycler view
    ItemTouchHelper.SimpleCallback adminCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            Assignment currentAssignment = adapter.getAssignments().get(position);

            if (direction == ItemTouchHelper.LEFT){
                AlertDialog.Builder builder = new AlertDialog.Builder(AddTeacherActivity.this);
                    builder.setMessage(R.string.are_you_sure);
                    builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                        assignments.remove(currentAssignment);
                        adapter.getAssignments().remove(currentAssignment);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(AddTeacherActivity.this, getString(R.string.assignment_deleted_msg), Toast.LENGTH_SHORT).show();

                        teacher.setAssignments(assignments);
                        if (!assignments.isEmpty()){
                            adapter.setAssignments(assignments);
                            binding.assignmentsRecView.setAdapter(adapter);
                            binding.assignmentsRecView.setVisibility(View.VISIBLE);
                            binding.emptyMsg.setVisibility(View.GONE);
                        } else {
                            binding.assignmentsRecView.setVisibility(View.GONE);
                            binding.emptyMsg.setVisibility(View.VISIBLE);
                        }
                    });
                    builder.setNegativeButton(R.string.no, (dialog, which) -> {
                        // Do Nothing
                        adapter.notifyItemChanged(position); // To reset the item on the screen
                    });
                    builder.create().show();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(AddTeacherActivity.this, R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(AddTeacherActivity.this, R.color.green))
                    .addSwipeRightActionIcon(R.drawable.ic_modify)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public static Teacher getTeacher() {
        return teacher;
    }

    private class TeacherCreationCallback implements Callback<JsonObject> {

        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            switch(response.code())
            {
                case Utils.HttpResponses.HTTP_201_CREATED:
                    Toast.makeText(AddTeacherActivity.this, "Teacher successfully created.", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    Toast.makeText(AddTeacherActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    break;
            }
        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            Toast.makeText(AddTeacherActivity.this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
        }
    }
}