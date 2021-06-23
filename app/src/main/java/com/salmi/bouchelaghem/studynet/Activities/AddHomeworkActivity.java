package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.gson.JsonObject;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.salmi.bouchelaghem.studynet.Models.Assignment;
import com.salmi.bouchelaghem.studynet.Models.Homework;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.Serializers;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityAddHomeworkBinding;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("ConstantConditions")
public class AddHomeworkActivity extends AppCompatActivity {

    private ActivityAddHomeworkBinding binding;

    // Fields
    // Sections
    private String section;
    private final List<String> sections = new ArrayList<>();
    private boolean sectionSelected = false;

    // Modules
    private String module;
    private final List<String> modules = new ArrayList<>();
    private boolean moduleSelected = false;

    // Module types
    private String moduleType;
    private final List<String> moduleTypes = new ArrayList<>();
    private boolean moduleTypeSelected = false;

    // Groups
    private String[] groupsArray; // All groups as an array
    private List<Integer> groups = new ArrayList<>(); // All groups as a list
    private final List<String> selectedGroupsString = new ArrayList<>(); // The groups selected by the user (as a string)
    private ArrayList<Integer> selectedGroupsInt; // The groups selected by the user (as a int)
    private boolean[] groupsStates; // We need this just for the dialog
    private boolean groupSelected = false;

    // Homework date + time
    private LocalDate dueDate;
    private LocalTime dueTime;

    private Assignment selectedAssignment;

    private CustomLoadingDialog loadingDialog;

    // Current teacher
    private final CurrentUser currentUser = CurrentUser.getInstance();
    private final Teacher currentTeacher = currentUser.getCurrentTeacher();
    private final List<Assignment> teacherAssignments = new ArrayList<>(currentTeacher.getAssignments());

    // Studynet Api
    private StudynetAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddHomeworkBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Initialize the timezone information
        AndroidThreeTen.init(this);
        /* For more info : https://github.com/JakeWharton/ThreeTenABP */

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api, this will implement the code of all the methods in the interface.
        api = retrofit.create(StudynetAPI.class);

        //Init loading dialog
        loadingDialog = new CustomLoadingDialog(this);

        // Get the action type (Add/Update)
        Intent intent = getIntent();
        String action = intent.getStringExtra(Utils.ACTION);

        initSectionsSpinner();

        switch (action) {
            case Utils.ACTION_ADD:
                // When the user clicks on save we create a new homework
                binding.btnSave.setOnClickListener(v -> {
                    if (sectionSelected & moduleSelected & moduleTypeSelected & groupSelected & validateTitle()
                            & dueDate != null & dueTime != null) {

                        String title = binding.txtHomeworkTitle.getEditText().getText().toString().trim();
                        String notes = binding.txtHomeworkNotes.getEditText().getText().toString().trim();

                        Homework homework = new Homework();
                        homework.setId(-1);

                        homework.setConcernedGroups(selectedGroupsInt);
                        homework.setLocalDateDueDate(dueDate);
                        homework.setLocalTimeDueTime(dueTime);
                        homework.setTitle(title);
                        homework.setComment(notes);
                        homework.setAssignment(selectedAssignment.getId());

                        //Get the jsonObject that will be sent to the api
                        JsonObject homeworkJson = Serializers.HomeworkSerializer(homework);

                        Call<Homework> createHomeworkCall = api.createHomework(homeworkJson, "Token " + currentUser.getToken());

                        loadingDialog.show();
                        createHomeworkCall.enqueue(new CreateHomeworkCallback());

                    } else {
                        if (!sectionSelected) {
                            binding.txtHomeworkSectionLayout.setError(getString(R.string.empty_section_msg));
                        }
                        if (!moduleSelected) {
                            binding.txtHomeworkSubjectLayout.setError(getString(R.string.empty_module_msg));
                        }
                        if (!moduleTypeSelected) {
                            binding.txtHomeworkSubjectTypeLayout.setError(getString(R.string.empty_type_msg));
                        }
                        if (!groupSelected) {
                            binding.txtHomeworkGroup.setError("");
                        }
                        if (dueDate == null) {
                            binding.btnSelectDueDate.setError("");
                        }
                        if (dueTime == null) {
                            binding.btnSelectDueTime.setError("");
                        }
                    }
                });
                break;
            case Utils.ACTION_UPDATE:
                // Change activity's title
                binding.title.setText(getString(R.string.update_homework));

                // Get the homework
                Homework currentHomework = intent.getParcelableExtra(Utils.HOMEWORK);
                Homework ogHomework = new Homework(currentHomework);

                fillFields(currentHomework);
                //Fill the concerned groups variable:
                selectedGroupsInt = currentHomework.getConcernedGroups();

                // When the user clicks on save we update an existing session
                binding.btnSave.setOnClickListener(v -> {
                    if (sectionSelected & moduleSelected & moduleTypeSelected & groupSelected & validateTitle()
                            & dueDate != null & dueTime != null) {

                        String title = binding.txtHomeworkTitle.getEditText().getText().toString().trim();
                        String notes = binding.txtHomeworkNotes.getEditText().getText().toString().trim();

                        currentHomework.setConcernedGroups(selectedGroupsInt);
                        currentHomework.setLocalDateDueDate(dueDate);
                        currentHomework.setLocalTimeDueTime(dueTime);
                        currentHomework.setTitle(title);
                        currentHomework.setComment(notes);
                        currentHomework.setAssignment(selectedAssignment.getId());

                        if (!ogHomework.equals(currentHomework)) {
                            //Get the jsonObject that will be sent to the api
                            JsonObject updatedHomeworkJson = Serializers.HomeworkSerializer(currentHomework);
                            Call<Homework> updateHomeworkCall = api.updateHomework(currentHomework.getId(), updatedHomeworkJson, "Token " + currentUser.getToken());
                            loadingDialog.show();
                            updateHomeworkCall.enqueue(new UpdateHomeworkCallback());
                        } else {
                            Toast.makeText(this, getString(R.string.no_changes_msg), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        if (!sectionSelected) {
                            binding.txtHomeworkSectionLayout.setError(getString(R.string.empty_section_msg));
                        }
                        if (!moduleSelected) {
                            binding.txtHomeworkSubjectLayout.setError(getString(R.string.empty_module_msg));
                        }
                        if (!moduleTypeSelected) {
                            binding.txtHomeworkSubjectTypeLayout.setError(getString(R.string.empty_type_msg));
                        }
                        if (!groupSelected) {
                            binding.txtHomeworkGroup.setError("");
                        }
                        if (dueDate == null) {
                            binding.btnSelectDueDate.setError("");
                        }
                        if (dueTime == null) {
                            binding.btnSelectDueTime.setError("");
                        }
                    }
                });

                break;
        }

        // Spinners
        binding.sectionsSpinner.setOnClickListener(v -> {
            if (sections.isEmpty()) {
                Toast.makeText(this, getString(R.string.no_sections), Toast.LENGTH_SHORT).show();
            }
        });

        binding.sectionsSpinner.setOnItemClickListener((parent, view1, position, id) -> {
            // Get selected item
            sectionSelected = true;
            section = sections.get(position);
            binding.txtHomeworkSectionLayout.setError(null);

            // Disable other spinners
            binding.modulesSpinner.setText("", false);
            moduleSelected = false;

            binding.txtHomeworkSubjectTypeLayout.setEnabled(false);
            binding.moduleTypesSpinner.setText("", false);
            moduleTypeSelected = false;

            binding.txtHomeworkGroup.setEnabled(false);
            binding.txtHomeworkGroup.setText("");
            binding.txtHomeworkGroup.setHint(R.string.groups);
            groupSelected = false;

            // Setup the next spinner
            binding.txtHomeworkSubjectLayout.setEnabled(true);
            setupModulesSpinner(section);
        });

        binding.modulesSpinner.setOnItemClickListener((parent, view12, position, id) -> {
            // Get selected item
            moduleSelected = true;
            module = modules.get(position);
            binding.txtHomeworkSubjectLayout.setError(null);

            // Disable other spinners
            binding.moduleTypesSpinner.setText("", false);
            moduleTypeSelected = false;

            binding.txtHomeworkGroup.setEnabled(false);
            binding.txtHomeworkGroup.setText("");
            binding.txtHomeworkGroup.setHint(R.string.groups);
            groupSelected = false;

            // Setup the next spinner
            binding.txtHomeworkSubjectTypeLayout.setEnabled(true);
            setupModuleTypesSpinner(section, module);
        });

        binding.moduleTypesSpinner.setOnItemClickListener((parent, view13, position, id) -> {
            // Get selected item
            moduleTypeSelected = true;
            moduleType = moduleTypes.get(position);
            binding.txtHomeworkSubjectTypeLayout.setError(null);

            // Disable other spinners
            binding.txtHomeworkGroup.setText("");
            binding.txtHomeworkGroup.setHint(R.string.groups);
            groupSelected = false;

            // Setup the next spinner
            binding.txtHomeworkGroup.setEnabled(true);
            getGroups(section, module, moduleType);
        });

        binding.txtHomeworkGroup.setOnClickListener(v -> {
            if (groupsArray != null && groupsArray.length != 0) {
                binding.txtHomeworkGroup.setError(null);
                // Init builder
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.MyAlertDialogTheme);
                // Set title
                builder.setTitle(R.string.select_groups);
                // No cancel
                builder.setCancelable(false);

                // Clone the groups list + their states in case the user cancels
                ArrayList<String> tmpSelectedGroups = new ArrayList<>(selectedGroupsString);
                boolean[] tmpStates = groupsStates.clone();

                builder.setMultiChoiceItems(groupsArray, groupsStates, (dialog, which, isChecked) -> {

                    // Get the current item
                    String currentGroup = groupsArray[which];
                    if (isChecked) { // If its selected then add it to the selected items list
                        selectedGroupsString.add(currentGroup);
                    } else { // if not then remove it from the list
                        selectedGroupsString.remove(currentGroup);
                    }
                });

                builder.setPositiveButton(R.string.save, (dialog, which) -> {

                    binding.txtHomeworkGroup.setText("");
                    selectedGroupsInt = new ArrayList<>();

                    if (!selectedGroupsString.isEmpty()) {
                        groupSelected = true;
                        Collections.sort(selectedGroupsString);

                        for (int i = 0; i < selectedGroupsString.size() - 1; i++) {
                            // Show the selected groups in the text view
                            binding.txtHomeworkGroup.append(selectedGroupsString.get(i) + ", ");
                            // Save the selected groups as integers
                            selectedGroupsInt.add(Integer.parseInt(selectedGroupsString.get(i)));
                        }
                        binding.txtHomeworkGroup.append(selectedGroupsString.get(selectedGroupsString.size() - 1));
                        selectedGroupsInt.add(Integer.parseInt(selectedGroupsString.get(selectedGroupsString.size() - 1)));
                    } else {
                        groupSelected = false;
                        binding.txtHomeworkGroup.setHint(R.string.groups);
                    }
                });

                builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // If the user cancels then restore the items and their states
                    selectedGroupsString.clear();
                    selectedGroupsString.addAll(tmpSelectedGroups);
                    groupsStates = tmpStates.clone();
                    dialog.dismiss();
                });

                builder.show();
            } else {
                Toast.makeText(this, getString(R.string.no_groups), Toast.LENGTH_SHORT).show();
            }
        });

        // Buttons
        binding.btnSelectDueDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker;
            if (dueDate != null) { // If we have already a selected date
                picker = openDatePicker(dueDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000L);
                /* I added one day to the date *plusDays(1)* when showing it because the date picker is always showing
                 * the date one day behind, so this is the only solution I found for now*/
            } else { // Else: make today's date the default date
                picker = openDatePicker(MaterialDatePicker.todayInUtcMilliseconds());
            }

            picker.addOnPositiveButtonClickListener(selection -> {
                dueDate = Instant.ofEpochMilli(selection).atZone(ZoneId.systemDefault()).toLocalDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                binding.btnSelectDueDate.setText(formatter.format(dueDate));
                binding.btnSelectDueDate.setError(null);
            });
            picker.addOnCancelListener(dialog -> picker.dismiss());
        });

        binding.btnSelectDueTime.setOnClickListener(v -> {
            MaterialTimePicker picker;
            if (dueTime != null) { // If we have already a selected time
                picker = openTimePicker(dueTime.getHour(), dueTime.getMinute());
            } else { // Else: make 8:00 the default time
                picker = openTimePicker(8, 0);
            }

            picker.addOnPositiveButtonClickListener(v1 -> {
                int hour = picker.getHour();
                int minute = picker.getMinute();
                dueTime = LocalTime.of(hour, minute);
                binding.btnSelectDueTime.setText(dueTime.toString());
                binding.btnSelectDueTime.setError(null);
            });
            picker.addOnCancelListener(dialog -> picker.dismiss());
        });

        binding.btnClose.setOnClickListener(v -> finish());
    }

    private MaterialTimePicker openTimePicker(int hour, int minute) {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTitleText(R.string.select_due_time)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour)
                .setMinute(minute)
                .build();

        picker.show(getSupportFragmentManager(), "DueTimePicker");
        return picker;
    }

    private MaterialDatePicker<Long> openDatePicker(long selection) {
        // Calendar Constraints
        // This will prevent the user from choosing a date < today
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now());
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.select_due_date)
                .setSelection(selection)
                .setCalendarConstraints(constraintsBuilder.build())
                .build();
        picker.show(getSupportFragmentManager(), "DueDatePicker");
        return picker;
    }

    // Get the groups that belongs to the selected section and they are taught by the current teacher
    private void getGroups(String sectionCode, String moduleCode, String moduleType) {
        for (Assignment assignment : teacherAssignments) {
            if (assignment.getSectionCode().equals(sectionCode)
                    && assignment.getModuleCode().equals(moduleCode)
                    && assignment.getModuleType().equals(moduleType)) {
                groups = new ArrayList<>(assignment.getConcernedGroups());
                // At this point we have a specific assignment so we will save it
                selectedAssignment = new Assignment(assignment);
                break;
            }
        }

        // Convert groups list to an array
        groupsArray = new String[groups.size()];
        for (int i = 0; i < groups.size(); i++) {
            groupsArray[i] = String.valueOf(groups.get(i));
        }
        groupsStates = new boolean[groupsArray.length];
        selectedGroupsString.clear();
    }

    // Get the module's types depending on the module and the section
    private void setupModuleTypesSpinner(String sectionCode, String moduleCode) {
        moduleTypes.clear();
        for (Assignment assignment : teacherAssignments) {
            if (assignment.getSectionCode().equals(sectionCode) && assignment.getModuleCode().equals(moduleCode)) {
                moduleTypes.add(assignment.getModuleType());
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, moduleTypes);
        binding.moduleTypesSpinner.setAdapter(arrayAdapter);
    }

    // Get the modules taught by the current teacher in the selected section
    private void setupModulesSpinner(String sectionCode) {
        modules.clear();
        for (Assignment assignment : teacherAssignments) {
            if (assignment.getSectionCode().equals(sectionCode) && !modules.contains(assignment.getModuleCode())) {
                modules.add(assignment.getModuleCode());
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, modules);
        binding.modulesSpinner.setAdapter(arrayAdapter);
    }

    // Get only the current teacher's sections
    private void initSectionsSpinner() {
        // We get the teacher's sections from its assignments
        sections.clear();
        for (Assignment assignment : teacherAssignments) {
            if (!sections.contains(assignment.getSectionCode())) {
                // We only add the section to the list if it doesn't exist already
                sections.add(assignment.getSectionCode());
            }
        }

        // Init spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, sections);
        binding.sectionsSpinner.setAdapter(arrayAdapter);
    }

    private void fillFields(Homework homework) {
        // Section
        sectionSelected = true;
        section = homework.getSection();
        // Set selected item
        binding.sectionsSpinner.setText(section, false);

        // Module
        moduleSelected = true;
        module = homework.getModule();
        // Fill the spinner
        setupModulesSpinner(section);
        binding.txtHomeworkSubjectLayout.setEnabled(true);
        // Set selected item
        binding.modulesSpinner.setText(module, false);

        // Class type
        moduleTypeSelected = true;
        moduleType = homework.getModuleType();
        // Fill the spinner
        setupModuleTypesSpinner(section, module);
        binding.txtHomeworkSubjectTypeLayout.setEnabled(true);
        // Set selected item
        binding.moduleTypesSpinner.setText(moduleType, false);

        // Groups
        groupSelected = true;
        getGroups(section, module, moduleType);
        // Fill the selected groups
        setSelectedGroups(homework.getConcernedGroups());
        binding.txtHomeworkGroup.setEnabled(true);

        // Set the selected groups to the text view
        int nbGroups = homework.getConcernedGroups().size();
        if (nbGroups == 1) { // If there is only one group
            binding.txtHomeworkGroup.setText(String.valueOf(homework.getConcernedGroups().get(0)));
        } else {
            binding.txtHomeworkGroup.setText("");
            for (int i = 0; i < homework.getConcernedGroups().size() - 1; i++) {
                // Show the homework groups in the text view
                binding.txtHomeworkGroup.append(homework.getConcernedGroups().get(i) + ", ");
            }
            binding.txtHomeworkGroup.append(String.valueOf(homework.getConcernedGroups().get(nbGroups - 1)));
        }

        // Title
        binding.txtHomeworkTitle.getEditText().setText(homework.getTitle());

        // Date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        dueDate = homework.getLocalDateDueDate();
        binding.btnSelectDueDate.setText(formatter.format(dueDate));

        // Time
        dueTime = homework.getLocalTimeDueTime();
        binding.btnSelectDueTime.setText(dueTime.toString());

        // Notes
        if (!homework.getComment().isEmpty())
            binding.txtHomeworkNotes.getEditText().setText(homework.getComment());
    }

    // Set the selected groups in case of update
    private void setSelectedGroups(List<Integer> concernedGroups) {

        selectedGroupsString.clear();
        for (int grp : concernedGroups) {
            groupsStates[grp - 1] = true;
            selectedGroupsString.add(String.valueOf(grp));
        }

    }

    // Validation methods
    public boolean validateTitle() {
        String data = binding.txtHomeworkTitle.getEditText().getText().toString().trim();

        if (data.isEmpty()) {
            binding.txtHomeworkTitle.setError(getString(R.string.empty_title_msg));
            return false;
        } else {
            binding.txtHomeworkTitle.setError(null);
            return true;
        }
    }

    private class CreateHomeworkCallback implements Callback<Homework> {
        @Override
        public void onResponse(@NonNull Call<Homework> call, Response<Homework> response) {
            if (response.code() == Utils.HttpResponses.HTTP_201_CREATED) {
                Toast.makeText(AddHomeworkActivity.this, getString(R.string.homework_created_success), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            } else {
                Toast.makeText(AddHomeworkActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        }

        @Override
        public void onFailure(@NonNull Call<Homework> call, @NonNull Throwable t) {
            Toast.makeText(AddHomeworkActivity.this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
        }
    }

    private class UpdateHomeworkCallback implements Callback<Homework> {
        @Override
        public void onResponse(@NonNull Call<Homework> call, @NonNull Response<Homework> response) {
            if (response.code() == Utils.HttpResponses.HTTP_200_OK) {
                Toast.makeText(AddHomeworkActivity.this, getString(R.string.homework_edited_success), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                finish();
            } else {
                Toast.makeText(AddHomeworkActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        }

        @Override
        public void onFailure(@NonNull Call<Homework> call, @NonNull Throwable t) {
            Toast.makeText(AddHomeworkActivity.this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            loadingDialog.dismiss();
        }
    }
}