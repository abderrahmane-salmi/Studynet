package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.salmi.bouchelaghem.studynet.Models.Module;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityAddHomeworkBinding;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddHomeworkActivity extends AppCompatActivity {

    private ActivityAddHomeworkBinding binding;

    // Fields
    private Section section;
    private List<Section> sections;
    private boolean sectionSelected = false;

    private Module module;
    private List<Module> modules;
    private boolean moduleSelected = false;

    private String[] groupsArray; // All groups as an array
    private List<String> selectedGroupsString; // The groups selected by the user (as a string)
    private ArrayList<Integer> selectedGroupsInt; // The groups selected by the user (as a int)
    private boolean[] groupsStates; // We need this just for the dialog
    private boolean groupSelected = false;

    private String dayName;
    private int day;
    private List<String> days;
    private boolean daySelected = false;

    private LocalDate dueDate;
    private LocalTime dueTime;

    TestAPI testAPI = TestAPI.getInstance();
    private Teacher currentTeacher;
    private final CurrentUser currentUser = CurrentUser.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddHomeworkBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Initialize the timezone information
        AndroidThreeTen.init(this);
        /* For more info : https://github.com/JakeWharton/ThreeTenABP */

        // Get current teacher
        currentTeacher = currentUser.getCurrentTeacher();

        // Get the action type (Add/Update)
        Intent intent = getIntent();
        String action = intent.getStringExtra(Utils.ACTION);

        setupSectionsSpinner(currentTeacher.getId());

        switch (action) {
            case Utils.ACTION_ADD:
                // When the user clicks on save we create a new homework
                binding.btnSave.setOnClickListener(v -> {

                });
                break;
            case Utils.ACTION_UPDATE:
                break;
        }

        // Spinners
        binding.classSection.setOnItemClickListener((parent, view1, position, id) -> {
            // Get selected item
            sectionSelected = true;
            section = sections.get(position);
            binding.txtHomeworkSection.setError(null);

            // Disable other spinners
            binding.classModule.setText("");
            moduleSelected = false;

            binding.txtHomeworkGroup.setEnabled(false);
            binding.txtHomeworkGroup.setText("");
            binding.txtHomeworkGroup.setHint(R.string.group);
            groupSelected = false;

            // Setup the next spinner
            binding.txtHomeworkSubject.setEnabled(true);
            getModules(currentTeacher.getId(), section.getCode());
        });

        binding.classModule.setOnItemClickListener((parent, view12, position, id) -> {
            // Get selected item
            moduleSelected = true;
            module = modules.get(position);
            binding.txtHomeworkSubject.setError(null);

            // Disable other spinners
            binding.txtHomeworkGroup.setText("");
            binding.txtHomeworkGroup.setHint(R.string.group);
            groupSelected = false;

            // Setup the next spinner
            binding.txtHomeworkGroup.setEnabled(true);
            getGroups(currentTeacher.getId(), section);
        });

        binding.txtHomeworkGroup.setOnClickListener(v -> {
            binding.txtHomeworkGroup.setError(null);
            // Init builder
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AddHomeworkActivity.this, R.style.MyAlertDialogTheme);
            // Set title
            builder.setTitle(R.string.select_groups);
            // No cancel
            builder.setCancelable(false);

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
                    binding.txtHomeworkGroup.setHint(R.string.group);
                }
            });

            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

            builder.show();

        });

        binding.btnSelectDueDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> picker;
            if (dueDate != null) { // If we have already a selected date
                picker = openDatePicker(dueDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toEpochSecond()* 1000L);
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

    private void getGroups(int id, Section section) {
        groupsArray = new String[section.getNbGroups()];
        for (int grp = 0; grp < section.getNbGroups(); grp++) {
            groupsArray[grp] = String.valueOf(grp + 1);
        }
        groupsStates = new boolean[groupsArray.length];
        selectedGroupsString = new ArrayList<>();
    }

    private void getModules(int id, String code) {
        modules = testAPI.getModules();

        // Get only names
        List<String> modulesNames = new ArrayList<>();
        for (Module module : modules) {
            modulesNames.add(module.getCode());
        }
        if (!modulesNames.isEmpty()) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddHomeworkActivity.this, R.layout.dropdown_item, modulesNames);
            binding.classModule.setAdapter(arrayAdapter);
        }
    }

    private void setupSectionsSpinner(int id) {
        sections = testAPI.getSections();

        // Get only names
        List<String> sectionsNames = new ArrayList<>();
        for (Section section : sections) {
            sectionsNames.add(section.getCode());
        }
        if (!sectionsNames.isEmpty()) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddHomeworkActivity.this, R.layout.dropdown_item, sectionsNames);
            binding.classSection.setAdapter(arrayAdapter);
        }
    }
}