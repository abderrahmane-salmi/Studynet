package com.salmi.bouchelaghem.studynet.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.salmi.bouchelaghem.studynet.Models.Assignment;
import com.salmi.bouchelaghem.studynet.Models.Module;
import com.salmi.bouchelaghem.studynet.Models.Section;
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

    private boolean otherMeetingFields = false;
    private ActivityAddClassBinding binding;

    // Fields
    private Session session;

    private Section section;
    private List<Section> sections;
    private boolean sectionSelected = false;

    private Module module;
    private List<Module> modules;
    private boolean moduleSelected = false;

    private String classType;
    private List<String> classTypes;
    private boolean classTypeSelected = false;

    private String[] groupsArray; // All groups as an array
    private List<String> selectedGroupsString; // The groups selected by the user (as a string)
    private ArrayList<Integer> selectedGroupsInt; // The groups selected by the user (as a int)
    private boolean[] groupsStates; // We need this just for the dialog
    private boolean groupSelected = false;

    private String dayName;
    private int day;
    private List<String> days;
    private boolean daySelected = false;

    private LocalTime startTime;
    private LocalTime endTime;


    private String meetingLink, meetingNumber, meetingPassword;

    private Teacher currentTeacher;
    private final CurrentUser currentUser = CurrentUser.getInstance();
    TestAPI testAPI = TestAPI.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddClassBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Get current teacher
        currentTeacher = currentUser.getCurrentTeacher();

        // Get the action type (Add/Update)
        Intent intent = getIntent();
        String action = intent.getStringExtra(Utils.ACTION);

        fillSpinners();

        binding.btnClose.setOnClickListener(v -> finish());

        binding.btnShowOtherMeetingFields.setOnClickListener(v -> {
            if (!otherMeetingFields){
                otherMeetingFields = true;
                binding.meetingFieldsGroup.setVisibility(View.VISIBLE);
                binding.btnShowOtherMeetingFields.setImageResource(R.drawable.ic_arrow_up);
            } else {
                otherMeetingFields = false;
                binding.meetingFieldsGroup.setVisibility(View.GONE);
                binding.btnShowOtherMeetingFields.setImageResource(R.drawable.ic_arrow_down);
            }
        });

        switch (action){
            case Utils.ACTION_ADD: // Add a new session
                // When the user clicks on save we create a new session
                binding.btnSave.setOnClickListener(v -> {

                    if (sectionSelected & moduleSelected & classTypeSelected
                    & groupSelected & daySelected & validateMeetingLink()
                    & startTime != null & endTime != null){

                        // TODO: set id
                        session = new Session();
                        session.setConcernedGroups(selectedGroupsInt);
                        session.setStartTime(startTime);
                        session.setEndTime(endTime);
                        session.setDay(day);
                        session.setMeetingLink(meetingLink);

                        if (!binding.txtMeetingNumber.getEditText().getText().toString().isEmpty() &&
                        !binding.txtMeetingPassword.getEditText().getText().toString().isEmpty()){
                            meetingNumber = binding.txtMeetingNumber.getEditText().getText().toString().trim();
                            session.setMeetingNumber(meetingNumber);
                            meetingPassword = binding.txtMeetingPassword.getEditText().getText().toString().trim();
                            session.setMeetingNumber(meetingPassword);
                        }

                        Assignment assignment = new Assignment();
                        // TODO: set id
                        assignment.setSectionCode(section.getCode());
                        assignment.setTeacherId(currentTeacher.getId());
                        assignment.setModuleName(module.getName());
                        assignment.setModuleCode(module.getCode());
                        assignment.setModuleType(classType);
                        assignment.setConcernedGroups(selectedGroupsInt);

                        session.setAssignment(assignment);

                        // Save it to the database


                    } else {
                        if (!sectionSelected){
                            binding.classSectionTextLayout.setError(getString(R.string.empty_msg3));
                        }
                        if (!moduleSelected){
                            binding.classModuleLayout.setError(getString(R.string.empty_msg5));
                        }
                        if (!classTypeSelected){
                            binding.classTypeTextLayout.setError(getString(R.string.empty_msg6));
                        }
                        if (!groupSelected){
                            binding.classGroup.setError("");
                        }
                        if (!daySelected){
                            binding.classDayTextLayout.setError(getString(R.string.empty_msg7));
                        }
                        if (startTime == null){
                            binding.btnSelectStartTime.setError("");
                        }
                        if (endTime == null){
                            binding.btnSelectEndTime.setError("");
                        }
                    }


                });

                break;
            case Utils.ACTION_UPDATE: // Update a session
                // Change activity's title
                binding.title.setText(getString(R.string.update_class));

                // Get the session's id
                int id = intent.getIntExtra(Utils.ID, 0);

                // Fill the session's fields
                fillFields(id);

                // When the user clicks on save we update an existing session
                binding.btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Same as 'add' but we only update the session object in the database
                    }
                });
                break;
        }

        binding.classSection.setOnItemClickListener((parent, view1, position, id) -> {
            // Get selected item
            sectionSelected = true;
            section = sections.get(position);
            binding.classSectionTextLayout.setError(null);

            // Disable other spinners
            binding.classModule.setText("");
            moduleSelected = false;

            binding.classTypeTextLayout.setEnabled(false);
            binding.classType.setText("");
            classTypeSelected = false;

            binding.classGroup.setEnabled(false);
            binding.classGroup.setText("");
            binding.classGroup.setHint(R.string.group);
            groupSelected = false;

            // Setup the next spinner
            binding.classModuleLayout.setEnabled(true);
            getModules(currentTeacher.getId(), section.getCode());
        });

        binding.classModule.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get selected item
                moduleSelected = true;
                module = modules.get(position);
                binding.classModuleLayout.setError(null);

                // Disable other spinners
                binding.classType.setText("");
                classTypeSelected = false;

                binding.classGroup.setEnabled(false);
                binding.classGroup.setText("");
                binding.classGroup.setHint(R.string.group);
                groupSelected = false;

                // Setup the next spinner
                binding.classTypeTextLayout.setEnabled(true);
                getModuleTypes(currentTeacher.getId(), section.getCode(), module.getCode());
            }
        });

        binding.classType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get selected item
                classTypeSelected = true;
                classType = classTypes.get(position);
                binding.classTypeTextLayout.setError(null);

                // Disable other spinners
                binding.classGroup.setText("");
                binding.classGroup.setHint(R.string.group);
                groupSelected = false;

                // Setup the next spinner
                binding.classGroup.setEnabled(true);
                getGroups(currentTeacher.getId(), section);
            }
        });

        binding.classGroup.setOnClickListener(v -> {
            binding.classGroup.setError(null);
            // Init builder
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AddClassActivity.this, R.style.MyAlertDialogTheme);
            // Set title
            builder.setTitle(R.string.select_groups);
            // No cancel
            builder.setCancelable(false);

            builder.setMultiChoiceItems(groupsArray, groupsStates, (dialog, which, isChecked) -> {

                // Get the current item
                String currentGroup = groupsArray[which];
                if (isChecked){ // If its selected then add it to the selected items list
                    selectedGroupsString.add(currentGroup);
                } else { // if not then remove it from the list
                    selectedGroupsString.remove(currentGroup);
                }
            });

            builder.setPositiveButton(R.string.save, (dialog, which) -> {

                binding.classGroup.setText("");
                selectedGroupsInt = new ArrayList<>();

                if (!selectedGroupsString.isEmpty()){
                    groupSelected = true;
                    Collections.sort(selectedGroupsString);

                    for (int i = 0; i< selectedGroupsString.size()-1; i++){
                        // Show the selected groups in the text view
                        binding.classGroup.append(selectedGroupsString.get(i) + ", ");
                        // Save the selected groups as integers
                        selectedGroupsInt.add(Integer.parseInt(selectedGroupsString.get(i)));
                    }
                    binding.classGroup.append(selectedGroupsString.get(selectedGroupsString.size()-1));
                    selectedGroupsInt.add(Integer.parseInt(selectedGroupsString.get(selectedGroupsString.size()-1)));
                } else {
                    groupSelected = false;
                    binding.classGroup.setHint(R.string.group);
                }
            });

            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

            builder.show();

        });

        binding.classDay.setOnItemClickListener((parent, view12, position, id) -> {
            // Get selected item
            daySelected = true;
            dayName = days.get(position);
            day = position+1;
            binding.classDayTextLayout.setError(null);
        });

        // Time pickers
        binding.btnSelectStartTime.setOnClickListener(v -> {
            MaterialTimePicker picker;
            if (startTime != null){ // If we have already a selected time
                picker = openTimePicker("Select Start time", startTime.getHour(), startTime.getMinute());
            } else { // Else: make 8:00 the default time
                picker = openTimePicker("Select Start time", 8, 0);
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

        binding.btnSelectEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker picker;
                if (startTime != null){ // If we have already a selected time
                    picker = openTimePicker("Select End time", endTime.getHour(), endTime.getMinute());
                } else { // Else: make 8:00 the default time
                    picker = openTimePicker("Select End time", 8, 0);
                }
                picker.addOnPositiveButtonClickListener(v12 -> {
                    int hour = picker.getHour();
                    int minute = picker.getMinute();
                    endTime = LocalTime.of(hour, minute);
                    binding.btnSelectEndTime.setText(endTime.toString());
                    binding.btnSelectEndTime.setError(null);
                });
                picker.addOnCancelListener(dialog -> picker.dismiss());
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

    private void fillFields(int id) {

        // Get the session
        for (Session s: testAPI.getSessions()){
            if (s.getId() == id){
                session = s;
            }
        }

        if (session != null){

            // Sections
            String sectionCode = session.getAssignment().getSectionCode();
            getSection(sectionCode);
            // Set selected item
            binding.classSection.setText(sectionCode, false);

            // Module
            String moduleCode = session.getAssignment().getModuleCode();
            getModule(moduleCode);
            // Fill the spinner
            getModules(currentTeacher.getId(), sectionCode);
            binding.classModuleLayout.setEnabled(true);
            // Set selected item
            binding.classModule.setText(moduleCode, false);

            // Class type
            String classType = session.getAssignment().getModuleType();
            // Fill the spinner
            getModuleTypes(currentTeacher.getId(), sectionCode, moduleCode);
            binding.classTypeTextLayout.setEnabled(true);
            // Set selected item
            binding.classType.setText(classType, false);

            // Groups
            getGroups(currentTeacher.getId(), section);
            // Fill the selected groups
            setSelectedGroups(session.getConcernedGroups());
            binding.classGroup.setEnabled(true);

            // Set the selected groups to the text view
            int nbGroups = session.getConcernedGroups().size();
            if (nbGroups == 1){ // If there is only one group
                binding.classGroup.setText(String.valueOf(session.getConcernedGroups().get(0)));
            } else {
                binding.classGroup.setText("");
                for (int i = 0; i< session.getConcernedGroups().size()-1; i++){
                    // Show the selected groups in the text view
                    binding.classGroup.append(session.getConcernedGroups().get(i) + ", ");
                }
                binding.classGroup.append(String.valueOf(session.getConcernedGroups().get(nbGroups-1)));
            }



            // Day
            initDays();
            binding.classDay.setText(days.get(session.getDay()-1));

            // Time
            startTime = session.getStartTime();
            binding.btnSelectStartTime.setText(startTime.toString());
            endTime = session.getEndTime();
            binding.btnSelectEndTime.setText(endTime.toString());

            // Meeting info
            binding.txtMeetingLink.getEditText().setText(session.getMeetingLink());

            // If this session has other meeting info
            if (session.getMeetingNumber() != null && session.getMeetingPassword() != null){
                binding.meetingFieldsGroup.setVisibility(View.VISIBLE);
                binding.txtMeetingNumber.getEditText().setText(session.getMeetingNumber());
                binding.txtMeetingPassword.getEditText().setText(session.getMeetingPassword());
            }
        }

    }

    // Get the section object from its code (From the API)
    private void getSection(String sectionCode) {
        for (Section s:testAPI.getSections()){
            if (s.getCode().equals(sectionCode)){
                section = s;
            }
        }
    }

    // Get the module object from its code (From the API)
    private void getModule(String moduleCode) {
        for (Module m:testAPI.getModules()){
            if (m.getCode().equals(moduleCode))
                module = m;
        }
    }

    private void fillSpinners(){
        // Sections
        getSections(currentTeacher.getId());
        // Days
        initDays();
    }

    private void initDays() {
        days = Arrays.asList(getResources().getStringArray(R.array.days));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddClassActivity.this, R.layout.dropdown_item, days);
        binding.classDay.setAdapter(arrayAdapter);
    }

    // Get the groups that belongs to the selected section and they are taught by the current teacher
    private void getGroups(int teacherId, Section section) {
        groupsArray = new String[section.getNbGroups()];
        for (int grp=0; grp<section.getNbGroups(); grp++){
            groupsArray[grp] = String.valueOf(grp+1);
        }
        groupsStates = new boolean[groupsArray.length];
        selectedGroupsString = new ArrayList<>();
    }

    // Set the selected groups in case of update
    private void setSelectedGroups(List<Integer> concernedGroups) {

        for (int grp:concernedGroups){
            groupsStates[grp-1] = true;
        }

    }

    // Get the module's types depending on the teacher and the section
    // Example: Teacher 1 teaches Section ISIL B the module GL2 -> Types = TD, TP
    private void getModuleTypes(int teacherId, String sectionCode, String moduleCode) {
        classTypes = module.getTypes();

        if (!classTypes.isEmpty()) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddClassActivity.this, R.layout.dropdown_item, classTypes);
            binding.classType.setAdapter(arrayAdapter);
        }
    }

    // Get the modules taught by the current teacher in the selected section (From the API)
    private void getModules(int teacherId, String sectionCode) {
        modules = testAPI.getModules();

        // Get only names
        List<String> modulesNames = new ArrayList<>();
        for (Module module : modules) {
            modulesNames.add(module.getCode());
        }
        if (!modulesNames.isEmpty()) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddClassActivity.this, R.layout.dropdown_item, modulesNames);
            binding.classModule.setAdapter(arrayAdapter);
        }
    }

    // Get only the teacher's sections
    private void getSections(int teacherId){
        sections = testAPI.getSections();

        // Get only names
        List<String> sectionsNames = new ArrayList<>();
        for (Section section : sections) {
            sectionsNames.add(section.getCode());
        }
        if (!sectionsNames.isEmpty()) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddClassActivity.this, R.layout.dropdown_item, sectionsNames);
            binding.classSection.setAdapter(arrayAdapter);
        }

    }

    public boolean validateMeetingLink(){
        meetingLink = binding.txtMeetingLink.getEditText().getText().toString().trim();

        if (meetingLink.isEmpty()){
            binding.txtMeetingLink.setError(getString(R.string.link_msg));
            return false;
        } else {
            binding.txtMeetingLink.setError(null);
            return true;
        }
    }

}