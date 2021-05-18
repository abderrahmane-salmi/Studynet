package com.salmi.bouchelaghem.studynet.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.salmi.bouchelaghem.studynet.Models.Assignment;
import com.salmi.bouchelaghem.studynet.Models.Session;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityAddClassBinding;

import org.threeten.bp.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AddClassActivity extends AppCompatActivity {

    private ActivityAddClassBinding binding;

    // Fields
    private Session session;

    private String section;
    private final List<String> sections = new ArrayList<>();
    private boolean sectionSelected = false;

    private String module;
    private final List<String> modules = new ArrayList<>();
    private boolean moduleSelected = false;

    private String moduleType;
    private final List<String> moduleTypes = new ArrayList<>();
    private boolean moduleTypeSelected = false;

    private String[] groupsArray; // All groups as an array
    private List<Integer> groups = new ArrayList<>(); // All groups as a list
    private final List<String> selectedGroupsString = new ArrayList<>(); // The groups selected by the user (as a string)
    private ArrayList<Integer> selectedGroupsInt; // The groups selected by the user (as a int)
    private boolean[] groupsStates; // We need this just for the dialog
    private boolean groupSelected = false;

    private String dayName;
    private int day;
    private List<String> days;
    private boolean daySelected = false;

    private LocalTime startTime;
    private LocalTime endTime;

    private boolean otherMeetingFields = false;

    private Assignment selectedAssignment;

    // Current teacher
    private final CurrentUser currentUser = CurrentUser.getInstance();
    private final Teacher currentTeacher = currentUser.getCurrentTeacher();
    private final List<Assignment> teacherAssignments = new ArrayList<>(currentTeacher.getAssignments());

    TestAPI testAPI = TestAPI.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddClassBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Get the action type (Add/Update)
        Intent intent = getIntent();
        String action = intent.getStringExtra(Utils.ACTION);

        // Init Sections spinner
        initSectionsSpinner();
        // Init Days spinner
        initDays();

        switch (action) {
            case Utils.ACTION_ADD: // Add a new session
                // When the user clicks on save we create a new session
                binding.btnSave.setOnClickListener(v -> {

                    if (sectionSelected & moduleSelected & moduleTypeSelected
                            & groupSelected & daySelected & validateMeetingLink()
                            & startTime != null & endTime != null) {

                        String meetingLink = binding.txtMeetingNumber.getEditText().getText().toString().trim();
                        String meetingNumber = binding.txtMeetingNumber.getEditText().getText().toString().trim();
                        String meetingPassword = binding.txtMeetingPassword.getEditText().getText().toString().trim();
                        String notes = binding.txtClassNotes.getEditText().getText().toString().trim();

                        session = new Session();
                        session.setId(-1);
                        session.setSection(section);
                        session.setModule(module);
                        session.setModuleType(moduleType);
                        session.setConcernedGroups(selectedGroupsInt);
                        session.setLocalTimeStartTime(startTime);
                        session.setLocalTimeEndTime(endTime);
                        session.setDay(day);
                        session.setMeetingLink(meetingLink);
                        session.setMeetingNumber(meetingNumber);
                        session.setMeetingNumber(meetingPassword);
                        session.setComment(notes);

                        // Save the session to the database


                    } else {
                        if (!sectionSelected) {
                            binding.classSectionTextLayout.setError(getString(R.string.empty_section_msg));
                        }
                        if (!moduleSelected) {
                            binding.classModuleLayout.setError(getString(R.string.empty_module_msg));
                        }
                        if (!moduleTypeSelected) {
                            binding.classTypeTextLayout.setError(getString(R.string.empty_type_msg));
                        }
                        if (!groupSelected) {
                            binding.classGroup.setError("");
                        }
                        if (!daySelected) {
                            binding.classDayTextLayout.setError(getString(R.string.empty_day_msg));
                        }
                        if (startTime == null) {
                            binding.btnSelectStartTime.setError("");
                        }
                        if (endTime == null) {
                            binding.btnSelectEndTime.setError("");
                        }
                    }


                });

                break;
            case Utils.ACTION_UPDATE: // Update a session
                // Change activity's title
                binding.title.setText(getString(R.string.update_class));

                // Get session
                Session currentSession = intent.getParcelableExtra(Utils.SESSION);
                Session ogSession = new Session(currentSession);

                // Fill the session's fields
                fillFields(currentSession);

                // When the user clicks on save we update an existing session
                binding.btnSave.setOnClickListener(v -> {

                    if (sectionSelected & moduleSelected & moduleTypeSelected
                            & groupSelected & daySelected & validateMeetingLink()
                            & startTime != null & endTime != null) {

                        String meetingLink = binding.txtMeetingNumber.getEditText().getText().toString().trim();
                        String meetingNumber = binding.txtMeetingNumber.getEditText().getText().toString().trim();
                        String meetingPassword = binding.txtMeetingPassword.getEditText().getText().toString().trim();
                        String notes = binding.txtClassNotes.getEditText().getText().toString().trim();

                        // Update meeting info + notes
                        currentSession.setMeetingLink(meetingLink);
                        currentSession.setMeetingNumber(meetingNumber);
                        currentSession.setMeetingNumber(meetingPassword);
                        currentSession.setComment(notes);

                        if (!ogSession.equals(currentSession)){
                            // Update the session in the database
                        } else {
                            Toast.makeText(this, getString(R.string.no_changes_msg), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (!sectionSelected) {
                            binding.classSectionTextLayout.setError(getString(R.string.empty_section_msg));
                        }
                        if (!moduleSelected) {
                            binding.classModuleLayout.setError(getString(R.string.empty_module_msg));
                        }
                        if (!moduleTypeSelected) {
                            binding.classTypeTextLayout.setError(getString(R.string.empty_type_msg));
                        }
                        if (!groupSelected) {
                            binding.classGroup.setError("");
                        }
                        if (!daySelected) {
                            binding.classDayTextLayout.setError(getString(R.string.empty_day_msg));
                        }
                        if (startTime == null) {
                            binding.btnSelectStartTime.setError("");
                        }
                        if (endTime == null) {
                            binding.btnSelectEndTime.setError("");
                        }
                    }


                });
                break;
        }

        // Spinners
        binding.classSection.setOnClickListener(v -> {
            if (sections.isEmpty()) {
                Toast.makeText(AddClassActivity.this, getString(R.string.no_sections), Toast.LENGTH_SHORT).show();
            }
        });

        binding.classSection.setOnItemClickListener((parent, view1, position, id) -> {
            // Get selected item
            sectionSelected = true;
            section = sections.get(position);
            binding.classSectionTextLayout.setError(null);

            // Disable other spinners
            binding.classModule.setText("", false);
            moduleSelected = false;

            binding.classTypeTextLayout.setEnabled(false);
            binding.classType.setText("", false);
            moduleTypeSelected = false;

            binding.classGroup.setEnabled(false);
            binding.classGroup.setText("");
            binding.classGroup.setHint(R.string.groups);
            groupSelected = false;

            // Setup the next spinner
            binding.classModuleLayout.setEnabled(true);
            setupModulesSpinner(section);
        });

        binding.classModule.setOnItemClickListener((parent, view14, position, id) -> {
            // Get selected item
            moduleSelected = true;
            module = modules.get(position);
            binding.classModuleLayout.setError(null);

            // Disable other spinners
            binding.classType.setText("", false);
            moduleTypeSelected = false;

            binding.classGroup.setEnabled(false);
            binding.classGroup.setText("");
            binding.classGroup.setHint(R.string.groups);
            groupSelected = false;

            // Setup the next spinner
            binding.classTypeTextLayout.setEnabled(true);
            setupModuleTypesSpinner(section, module);
        });

        binding.classType.setOnItemClickListener((parent, view13, position, id) -> {
            // Get selected item
            moduleTypeSelected = true;
            moduleType = moduleTypes.get(position);
            binding.classTypeTextLayout.setError(null);

            // Disable other spinners
            binding.classGroup.setText("");
            binding.classGroup.setHint(R.string.groups);
            groupSelected = false;

            // Setup the next spinner
            binding.classGroup.setEnabled(true);
            getGroups(section, module, moduleType);
        });

        binding.classGroup.setOnClickListener(v -> {
            if (groupsArray != null && groupsArray.length != 0) {
                binding.classGroup.setError(null);
                // Init builder
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AddClassActivity.this, R.style.MyAlertDialogTheme);
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

                    binding.classGroup.setText("");
                    selectedGroupsInt = new ArrayList<>();

                    if (!selectedGroupsString.isEmpty()) {
                        groupSelected = true;
                        Collections.sort(selectedGroupsString);

                        for (int i = 0; i < selectedGroupsString.size() - 1; i++) {
                            // Show the selected groups in the text view
                            binding.classGroup.append(selectedGroupsString.get(i) + ", ");
                            // Save the selected groups as integers
                            selectedGroupsInt.add(Integer.parseInt(selectedGroupsString.get(i)));
                        }
                        binding.classGroup.append(selectedGroupsString.get(selectedGroupsString.size() - 1));
                        selectedGroupsInt.add(Integer.parseInt(selectedGroupsString.get(selectedGroupsString.size() - 1)));
                    } else {
                        groupSelected = false;
                        binding.classGroup.setHint(R.string.groups);
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

        binding.classDay.setOnItemClickListener((parent, view12, position, id) -> {
            // Get selected item
            daySelected = true;
            dayName = days.get(position);
            day = position + 1;
            binding.classDayTextLayout.setError(null);
        });

        // Time pickers
        binding.btnSelectStartTime.setOnClickListener(v -> {
            MaterialTimePicker picker;
            if (startTime != null) { // If we have already a selected time
                picker = openTimePicker(getString(R.string.select_start_time), startTime.getHour(), startTime.getMinute());
            } else { // Else: make 8:00 the default time
                picker = openTimePicker(getString(R.string.select_start_time), 8, 0);
            }

            picker.addOnPositiveButtonClickListener(v1 -> {
                int hour = picker.getHour();
                int minute = picker.getMinute();
                startTime = LocalTime.of(hour, minute);
                binding.btnSelectStartTime.setText(startTime.toString());
                binding.btnSelectStartTime.setError(null);
            });
            picker.addOnCancelListener(dialog -> picker.dismiss());
        });

        binding.btnSelectEndTime.setOnClickListener(v -> {
            MaterialTimePicker picker;
            if (endTime != null) { // If we have already a selected time
                picker = openTimePicker(getString(R.string.select_end_time), endTime.getHour(), endTime.getMinute());
            } else { // Else: make 8:00 the default time
                picker = openTimePicker(getString(R.string.select_end_time), 8, 0);
            }
            picker.addOnPositiveButtonClickListener(v12 -> {
                int hour = picker.getHour();
                int minute = picker.getMinute();
                endTime = LocalTime.of(hour, minute);
                binding.btnSelectEndTime.setText(endTime.toString());
                binding.btnSelectEndTime.setError(null);
            });
            picker.addOnCancelListener(dialog -> picker.dismiss());
        });

        // Buttons
        binding.btnClose.setOnClickListener(v -> finish());

        binding.btnShowOtherMeetingFields.setOnClickListener(v -> {
            if (!otherMeetingFields) {
                otherMeetingFields = true;
                binding.meetingFieldsGroup.setVisibility(View.VISIBLE);
                binding.btnShowOtherMeetingFields.setImageResource(R.drawable.ic_arrow_up);
            } else {
                otherMeetingFields = false;
                binding.meetingFieldsGroup.setVisibility(View.GONE);
                binding.btnShowOtherMeetingFields.setImageResource(R.drawable.ic_arrow_down);
            }
        });

    }

    private MaterialTimePicker openTimePicker(String title, int hour, int minute) {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTitleText(title)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour)
                .setMinute(minute)
                .build();

        picker.show(getSupportFragmentManager(), "TAG");
        return picker;
    }

    private void fillFields(Session session) {

        // Section
        sectionSelected = true;
        section = session.getSection();
        // Set selected item
        binding.classSection.setText(section, false);

        // Module
        moduleSelected = true;
        module = session.getModule();
        // Fill the spinner
        setupModulesSpinner(section);
        binding.classModuleLayout.setEnabled(true);
        // Set selected item
        binding.classModule.setText(module, false);

        // Class type
        moduleTypeSelected = true;
        moduleType = session.getModuleType();
        // Fill the spinner
        setupModuleTypesSpinner(section, module);
        binding.classTypeTextLayout.setEnabled(true);
        // Set selected item
        binding.classType.setText(moduleType, false);

        // Groups
        groupSelected = true;
        getGroups(section, module, moduleType);
        // Fill the selected groups
        setSelectedGroups(session.getConcernedGroups());
        binding.classGroup.setEnabled(true);

        // Set the selected groups to the text view
        int nbGroups = session.getConcernedGroups().size();
        if (nbGroups == 1) { // If there is only one group
            binding.classGroup.setText(String.valueOf(session.getConcernedGroups().get(0)));
        } else {
            binding.classGroup.setText("");
            for (int i = 0; i < session.getConcernedGroups().size() - 1; i++) {
                // Show the selected groups in the text view
                binding.classGroup.append(session.getConcernedGroups().get(i) + ", ");
            }
            binding.classGroup.append(String.valueOf(session.getConcernedGroups().get(nbGroups - 1)));
        }


        // Day
        initDays();
        daySelected = true;
        binding.classDay.setText(days.get(session.getDay() - 1));

        // Time
        startTime = session.getLocalTimeStartTime();
        binding.btnSelectStartTime.setText(startTime.toString());
        endTime = session.getLocalTimeEndTime();
        binding.btnSelectEndTime.setText(endTime.toString());

        // Meeting info
        binding.txtMeetingLink.getEditText().setText(session.getMeetingLink());

        // If this session has other meeting info
        if (session.getMeetingNumber() != null && session.getMeetingPassword() != null) {
            binding.meetingFieldsGroup.setVisibility(View.VISIBLE);
            binding.txtMeetingNumber.getEditText().setText(session.getMeetingNumber());
            binding.txtMeetingPassword.getEditText().setText(session.getMeetingPassword());
        }

        if (session.getComment() != null){
            binding.txtClassNotes.getEditText().setText(session.getComment());
        }

    }

    private void initDays() {
        days = Arrays.asList(getResources().getStringArray(R.array.days));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddClassActivity.this, R.layout.dropdown_item, days);
        binding.classDay.setAdapter(arrayAdapter);
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

    // Set the selected groups in case of update
    private void setSelectedGroups(List<Integer> concernedGroups) {

        selectedGroupsString.clear();
        for (int grp : concernedGroups) {
            groupsStates[grp - 1] = true;
            selectedGroupsString.add(String.valueOf(grp));
        }

    }

    // Get the module's types depending on the module and the section
    private void setupModuleTypesSpinner(String sectionCode, String moduleCode) {
        moduleTypes.clear();
        for (Assignment assignment : teacherAssignments) {
            if (assignment.getSectionCode().equals(sectionCode) && assignment.getModuleCode().equals(moduleCode)) {
                moduleTypes.add(assignment.getModuleType());
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddClassActivity.this, R.layout.dropdown_item, moduleTypes);
        binding.classType.setAdapter(arrayAdapter);
    }

    // Get the modules taught by the current teacher in the selected section
    private void setupModulesSpinner(String sectionCode) {
        modules.clear();
        for (Assignment assignment : teacherAssignments) {
            if (assignment.getSectionCode().equals(sectionCode) && !modules.contains(assignment.getModuleCode())) {
                modules.add(assignment.getModuleCode());
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddClassActivity.this, R.layout.dropdown_item, modules);
        binding.classModule.setAdapter(arrayAdapter);
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddClassActivity.this, R.layout.dropdown_item, sections);
        binding.classSection.setAdapter(arrayAdapter);

    }

    public boolean validateMeetingLink() {
        String meetingLink = binding.txtMeetingLink.getEditText().getText().toString().trim();

        if (meetingLink.isEmpty()) {
            binding.txtMeetingLink.setError(getString(R.string.link_msg));
            return false;
        } else {
            binding.txtMeetingLink.setError(null);
            return true;
        }
    }

}