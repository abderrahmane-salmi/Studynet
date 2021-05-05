package com.salmi.bouchelaghem.studynet.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.salmi.bouchelaghem.studynet.Models.Module;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityAddAssignmentBinding;
import com.salmi.bouchelaghem.studynet.databinding.ActivityAddHomeworkBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddAssignmentActivity extends AppCompatActivity {

    private ActivityAddAssignmentBinding binding;

    private int teacherId;

    private ArrayList<String> sections;
    private String section;
    private boolean sectionSelected = false;

    private ArrayList<Module> modules;
    private Module module;
    private boolean moduleSelected = false;

    private ArrayList<String> moduleTypes;
    private String moduleType;
    private boolean moduleTypeSelected = false;

    private String[] groupsArray; // All groups as an array
    private List<String> selectedGroupsString; // The groups selected by the user (as a string)
    private ArrayList<Integer> selectedGroupsInt; // The groups selected by the user (as a int)
    private boolean[] groupsStates; // We need this just for the dialog
    private boolean groupSelected = false;

    TestAPI testAPI = TestAPI.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAssignmentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        String action = intent.getStringExtra(Utils.ACTION);
        teacherId = intent.getIntExtra(Utils.ID, -1);
        sections = intent.getStringArrayListExtra(Utils.SECTIONS);

        setupSectionsSpinner();

        switch (action){
            case Utils.ACTION_ADD:
                break;
            case Utils.ACTION_UPDATE:
                break;
        }

        // Spinners
        binding.txtSectionSpinner.setOnItemClickListener((parent, view1, position, id) -> {
            // Get selected item
            sectionSelected = true;
            section = sections.get(position);
            binding.txtSectionTextLayout.setError(null);

            // Disable other spinners
            binding.txtModuleSpinner.setText("", false);
            moduleSelected = false;

            binding.txtModuleTypeTextLayout.setEnabled(false);
            binding.txtModuleTypeSpinner.setText("", false);
            moduleTypeSelected = false;

            binding.txtGroup.setEnabled(false);
            binding.txtGroup.setText("");
            binding.txtGroup.setHint(R.string.groups);
            groupSelected = false;

            // Setup the next spinner
            binding.txtModuleLayout.setEnabled(true);
            getModules(teacherId, section);
        });

        binding.txtModuleSpinner.setOnItemClickListener((parent, view12, position, id) -> {
            // Get selected item
            moduleSelected = true;
            module = modules.get(position);
            binding.txtModuleLayout.setError(null);

            // Disable other spinners
            binding.txtModuleTypeSpinner.setText("", false);
            moduleTypeSelected = false;

            binding.txtGroup.setEnabled(false);
            binding.txtGroup.setText("");
            binding.txtGroup.setHint(R.string.groups);
            groupSelected = false;

            // Setup the next spinner
            binding.txtModuleTypeTextLayout.setEnabled(true);
            getModuleTypes(module);
        });

        binding.txtModuleTypeSpinner.setOnItemClickListener((parent, view13, position, id) -> {
            // Get selected item
            moduleTypeSelected = true;
            moduleType = moduleTypes.get(position);
            binding.txtModuleTypeTextLayout.setError(null);

            // Disable other spinners
            binding.txtGroup.setText("");
            binding.txtGroup.setHint(R.string.groups);
            groupSelected = false;

            // Setup the next spinner
            binding.txtGroup.setEnabled(true);
            getGroups(teacherId, section);
        });

        binding.txtGroup.setOnClickListener(v -> {
            binding.txtGroup.setError(null);
            // Init builder
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AddAssignmentActivity.this, R.style.MyAlertDialogTheme);
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

                binding.txtGroup.setText("");
                selectedGroupsInt = new ArrayList<>();

                if (!selectedGroupsString.isEmpty()) {
                    groupSelected = true;
                    Collections.sort(selectedGroupsString);

                    for (int i = 0; i < selectedGroupsString.size() - 1; i++) {
                        // Show the selected groups in the text view
                        binding.txtGroup.append(selectedGroupsString.get(i) + ", ");
                        // Save the selected groups as integers
                        selectedGroupsInt.add(Integer.parseInt(selectedGroupsString.get(i)));
                    }
                    binding.txtGroup.append(selectedGroupsString.get(selectedGroupsString.size() - 1));
                    selectedGroupsInt.add(Integer.parseInt(selectedGroupsString.get(selectedGroupsString.size() - 1)));
                } else {
                    groupSelected = false;
                    binding.txtGroup.setHint(R.string.group);
                }
            });

            builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

            builder.show();

        });
    }

    private void getGroups(int teacherId, String sectionCode) {
        // Get section object using its code
        Section section = getSection(sectionCode);

        groupsArray = new String[section.getNbGroups()];
        for (int grp = 0; grp < section.getNbGroups(); grp++) {
            groupsArray[grp] = String.valueOf(grp + 1);
        }
        groupsStates = new boolean[groupsArray.length];
        selectedGroupsString = new ArrayList<>();
    }

    private Section getSection(String code) {
        return testAPI.getSections().get(0);
    }

    private void getModuleTypes(Module module) {
        moduleTypes = new ArrayList<>(module.getTypes());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddAssignmentActivity.this, R.layout.dropdown_item, moduleTypes);
        binding.txtModuleTypeSpinner.setAdapter(arrayAdapter);
    }

    private void getModules(int teacherId, String section) {
        modules = new ArrayList<>(testAPI.getModules());
        // Get only names
        List<String> modulesNames = new ArrayList<>();
        for (Module module : modules) {
            modulesNames.add(module.getCode());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddAssignmentActivity.this, R.layout.dropdown_item, modulesNames);
        binding.txtModuleSpinner.setAdapter(arrayAdapter);
    }

    private void setupSectionsSpinner() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddAssignmentActivity.this, R.layout.dropdown_item, sections);
        binding.txtSectionSpinner.setAdapter(arrayAdapter);
    }
}