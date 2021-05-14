package com.salmi.bouchelaghem.studynet.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.salmi.bouchelaghem.studynet.Models.Assignment;
import com.salmi.bouchelaghem.studynet.Models.Module;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Specialty;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.ActivityAddAssignmentBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    // Studynet Api
    private StudynetAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAssignmentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api
        api = retrofit.create(StudynetAPI.class);

        Intent intent = getIntent();
        String action = intent.getStringExtra(Utils.ACTION);
        teacherId = intent.getIntExtra(Utils.ID, -1);
        sections = intent.getStringArrayListExtra(Utils.SECTIONS);

        setupSectionsSpinner();

        switch (action){
            case Utils.ACTION_ADD:
                binding.btnSave.setOnClickListener(v -> {
                    if (sectionSelected && moduleSelected && moduleTypeSelected && groupSelected){
                        Assignment assignment = new Assignment();
                        assignment.setId(-1);
                        assignment.setSectionCode(section);
                        assignment.setModuleCode(module.getCode());
                        assignment.setModuleName(module.getName());
                        assignment.setModuleType(moduleType);
                        assignment.setConcernedGroups(selectedGroupsInt);

                        Toast.makeText(AddAssignmentActivity.this, "Save", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!sectionSelected){
                            binding.txtSectionTextLayout.setError(getString(R.string.empty_section_msg));
                        }
                        if (!moduleSelected){
                            binding.txtModuleLayout.setError(getString(R.string.empty_module_msg));
                        }
                        if (!moduleTypeSelected){
                            binding.txtModuleTypeTextLayout.setError(getString(R.string.empty_type_msg));
                        }
                        if (!groupSelected){
                            binding.txtGroup.setError(getString(R.string.empty_group_msg));
                        }
                    }
                });
                break;
            case Utils.ACTION_UPDATE:
                // Change title
                binding.title.setText(R.string.update_assignment);
                // Get assignment
                Assignment assignment = intent.getParcelableExtra(Utils.ASSIGNMENT);
                fillFields(assignment);
                binding.btnSave.setOnClickListener(v -> {
                    if (sectionSelected && moduleSelected && moduleTypeSelected && groupSelected){
                        // Check if the user changed anything
                        if (!assignment.getSectionCode().equals(section) ||
                        !assignment.getModuleCode().equals(module.getCode()) ||
                                !assignment.getModuleType().equals(moduleType) ||
                                assignment.getConcernedGroups() != selectedGroupsInt){
                            
                            assignment.setSectionCode(section);
                            assignment.setModuleCode(module.getCode());
                            assignment.setModuleName(module.getName());
                            assignment.setModuleType(moduleType);
                            assignment.setConcernedGroups(selectedGroupsInt);

                            Toast.makeText(AddAssignmentActivity.this, "Save", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, getString(R.string.no_changes_msg), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (!sectionSelected){
                            binding.txtSectionTextLayout.setError(getString(R.string.empty_section_msg));
                        }
                        if (!moduleSelected){
                            binding.txtModuleLayout.setError(getString(R.string.empty_module_msg));
                        }
                        if (!moduleTypeSelected){
                            binding.txtModuleTypeTextLayout.setError(getString(R.string.empty_type_msg));
                        }
                        if (!groupSelected){
                            binding.txtGroup.setError(getString(R.string.empty_group_msg));
                        }
                    }
                });
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
            setupModulesSpinner(section);
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
            setupModuleTypesSpinner(module);
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

    private void fillFields(Assignment assignment) {
        // Section
        sectionSelected = true;
        section = assignment.getSectionCode();
        binding.txtSectionSpinner.setText(section, false);

        // Module
        // Get module
        binding.txtModuleLayout.setEnabled(true);
        moduleSelected = true;
        module = getModule(assignment.getModuleCode());
        assert module != null;
        // Setup spinner
        setupModulesSpinner(section);
        binding.txtModuleSpinner.setText(module.getCode(), false);

        // Module types
        binding.txtModuleTypeTextLayout.setEnabled(true);
        moduleTypeSelected = true;
        moduleType = assignment.getModuleType();
        // Setup spinner
        setupModuleTypesSpinner(module);
        binding.txtModuleTypeSpinner.setText(moduleType, false);

        // Groups
        binding.txtGroup.setEnabled(true);
        groupSelected = true;
        getGroups(teacherId, section);
        // Fill the selected groups
        setSelectedGroups(assignment.getConcernedGroups());

        // Set the selected groups to the text view
        int nbGroups = assignment.getConcernedGroups().size();
        if (nbGroups == 1){ // If there is only one group
            binding.txtGroup.setText(String.valueOf(assignment.getConcernedGroups().get(0)));
        } else {
            binding.txtGroup.setText("");
            for (int i = 0; i< assignment.getConcernedGroups().size()-1; i++){
                // Show the selected groups in the text view
                binding.txtGroup.append(assignment.getConcernedGroups().get(i) + ", ");
            }
            binding.txtGroup.append(String.valueOf(assignment.getConcernedGroups().get(nbGroups-1)));
        }


    }

    // Set the selected groups in case of update
    private void setSelectedGroups(List<Integer> concernedGroups) {
        selectedGroupsString = new ArrayList<>();
        for (int grp:concernedGroups){
            groupsStates[grp-1] = true;
            selectedGroupsString.add(String.valueOf(grp));
        }
    }

    // Get the module object from its code (From the API)
    private Module getModule(String moduleCode) {
        for (Module m:testAPI.getModules()){
            if (m.getCode().equals(moduleCode)){
                return m;
            }
        }
        return null;
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

    private void setupModuleTypesSpinner(Module module) {
        moduleTypes = new ArrayList<>(module.getTypes());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddAssignmentActivity.this, R.layout.dropdown_item, moduleTypes);
        binding.txtModuleTypeSpinner.setAdapter(arrayAdapter);
    }

    private void setupModulesSpinner(String section) {
        Call<List<Module>> call = api.getSectionModules(section);
        call.enqueue(new Callback<List<Module>>() {
            @Override
            public void onResponse(@NonNull Call<List<Module>> call, @NonNull Response<List<Module>> response) {
                if (response.isSuccessful()){
                    assert response.body() != null;
                    modules = new ArrayList<>(response.body());

                    // Get only names
                    List<String> modulesNames = new ArrayList<>();
                    for (Module module : modules) {
                        modulesNames.add(module.getCode());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddAssignmentActivity.this, R.layout.dropdown_item, modulesNames);
                    binding.txtModuleSpinner.setAdapter(arrayAdapter);
                } else {
                    Toast.makeText(AddAssignmentActivity.this, getString(R.string.error)+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Module>> call, @NonNull Throwable t) {
                Toast.makeText(AddAssignmentActivity.this, getString(R.string.error)+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSectionsSpinner() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddAssignmentActivity.this, R.layout.dropdown_item, sections);
        binding.txtSectionSpinner.setAdapter(arrayAdapter);
    }
}