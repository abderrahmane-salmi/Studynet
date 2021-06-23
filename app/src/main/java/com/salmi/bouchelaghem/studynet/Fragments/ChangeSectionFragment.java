package com.salmi.bouchelaghem.studynet.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentChangeSectionBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangeSectionFragment extends BottomSheetDialogFragment {

    private FragmentChangeSectionBinding binding;

    // Studynet Api
    private StudynetAPI api;
    private final CurrentUser currentUser = CurrentUser.getInstance();

    //Loading dialog
    private CustomLoadingDialog loadingDialog;

    // Section
    private final List<Section> sectionObjects = new ArrayList<>();
    private final List<String> sections = new ArrayList<>();
    private String section;
    private boolean sectionSelected = false;

    // Groups
    private final List<String> groups = new ArrayList<>();
    private String group;
    private boolean groupSelected = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChangeSectionBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api
        api = retrofit.create(StudynetAPI.class);

        //Init loading dialog
        loadingDialog = new CustomLoadingDialog(requireContext());

        // Init sections list
        getAllSections();

        // Save button
        binding.btnSave.setOnClickListener(v -> {
            if (sectionSelected && groupSelected) {

                JsonObject sectionJson = new JsonObject();
                sectionJson.addProperty("section", section);
                sectionJson.addProperty("group", group);
                Call<Section> changeSectionCall = api.changeSection(sectionJson, "Token " + currentUser.getToken());
                loadingDialog.show();
                changeSectionCall.enqueue(new Callback<Section>() {
                    @Override
                    public void onResponse(@NonNull Call<Section> call, @NonNull Response<Section> response) {
                        if (response.body() != null && response.code() == Utils.HttpResponses.HTTP_200_OK) {
                            //Unsubscribe this device from the old section's notifications.
                            String currentSection = currentUser.getCurrentStudent().getSection().getCode();
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(currentSection.replace(' ', '_'));
                            //Subscribe this device to the new section's notifications.
                            FirebaseMessaging.getInstance().subscribeToTopic(response.body().getCode().replace(' ', '_'));
                            //Update the data in memory.
                            currentUser.getCurrentStudent().setSection(response.body());
                            Toast.makeText(requireContext(), getString(R.string.section_changed_successfully), Toast.LENGTH_SHORT).show();
                            //Update the section and the group locally.
                            currentUser.getCurrentStudent().setSection(response.body());
                            currentUser.getCurrentStudent().setGroup(Integer.parseInt(group));
                            loadingDialog.dismiss();
                            dismiss();
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Section> call, @NonNull Throwable t) {
                        Toast.makeText(requireContext(), getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                if (!sectionSelected) {
                    binding.txtSectionLayout.setError(getString(R.string.empty_section_msg));
                }
                if (!groupSelected) {
                    binding.txtGroupLayout.setError(getString(R.string.empty_group_msg));
                }
            }
        });

        // Spinner
        binding.txtSectionLayout.setOnClickListener(v -> {
            if (sections.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.no_sections), Toast.LENGTH_SHORT).show();
            }
        });

        // When the user chooses a section
        binding.txtSectionSpinner.setOnItemClickListener((parent, view1, position, id) -> {
            // Get the selected item
            binding.txtSectionLayout.setError(null);
            sectionSelected = true;
            section = sections.get(position);

            // Disable other spinners
            binding.txtGroupSpinner.setText("", false);
            groupSelected = false;

            // Set up the groups spinner
            binding.txtGroupLayout.setEnabled(true);
            setupGroupsSpinner(sectionObjects.get(position).getNbGroups());
        });

        // Groups spinner
        binding.txtGroupSpinner.setOnItemClickListener((parent, view12, position, id) -> {
            binding.txtGroupLayout.setError(null);
            group = groups.get(position);
            groupSelected = true;
        });

        return view;
    }

    private void setupGroupsSpinner(int nbGroups) {
        groups.clear();
        if (nbGroups > 0) {
            for (int i = 1; i <= nbGroups; i++) {
                groups.add(String.valueOf(i));
            }
        }
        // Set up the spinner
        ArrayAdapter<String> groupsAdapter = new ArrayAdapter<>(getContext(), R.layout.dropdown_item, groups);
        binding.txtGroupSpinner.setAdapter(groupsAdapter);
    }

    private void getAllSections() {
        Call<List<Section>> getAllSectionsCall = api.getAllSections();
        getAllSectionsCall.enqueue(new Callback<List<Section>>() {
            @Override
            public void onResponse(@NonNull Call<List<Section>> call, @NonNull Response<List<Section>> response) {
                if (response.code() == Utils.HttpResponses.HTTP_200_OK) {
                    if (response.body() != null) {
                        // Get the sections objects
                        sectionObjects.clear();
                        sectionObjects.addAll(response.body());
                        sections.clear();
                        if (!sectionObjects.isEmpty()) {
                            // Get names
                            for (Section sec : sectionObjects) {
                                sections.add(sec.getCode());
                            }
                        }
                        Section studentSection = currentUser.getCurrentStudent().getSection();
                        // Set up spinner
                        ArrayAdapter<String> sectionsAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, sections);
                        binding.txtSectionSpinner.setAdapter(sectionsAdapter);
                        binding.txtSectionSpinner.setText(studentSection.getCode(), false);
                        sectionSelected = true;
                        section = studentSection.getCode();
                        //Get section groups
                        groups.clear();
                        if (studentSection.getNbGroups() > 0) {
                            for (int i = 1; i <= studentSection.getNbGroups(); i++) {
                                groups.add(String.valueOf(i));
                            }
                            ArrayAdapter<String> groupsAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, groups);
                            binding.txtGroupSpinner.setAdapter(groupsAdapter);
                            binding.txtGroupSpinner.setText(String.valueOf(currentUser.getCurrentStudent().getGroup()), false);
                            groupSelected = true;
                            group = String.valueOf(currentUser.getCurrentStudent().getGroup());
                        }

                    } else {
                        Toast.makeText(getContext(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                        loadingDialog.dismiss();
                    }
                } else {
                    Toast.makeText(requireContext(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Section>> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }
}