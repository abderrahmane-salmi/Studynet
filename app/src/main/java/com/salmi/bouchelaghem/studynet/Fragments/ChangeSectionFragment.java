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

import okhttp3.ResponseBody;
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
    private List<String> sections = new ArrayList<>();
    private String section;
    private boolean sectionSelected = false;

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
            if (sectionSelected){
                JsonObject sectionJson = new JsonObject();
                sectionJson.addProperty("section",section);
                Call<Section> changeSectionCall = api.changeSection(sectionJson,"Token " + currentUser.getToken());
                loadingDialog.show();
                changeSectionCall.enqueue(new Callback<Section>() {
                    @Override
                    public void onResponse(@NonNull Call<Section> call, @NonNull Response<Section> response) {
                        if(response.code() == Utils.HttpResponses.HTTP_200_OK)
                        {
                            //Unsubscribe this device from the old section's notifications.
                            String currentSection =  currentUser.getCurrentStudent().getSection().getCode();
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(currentSection.replace(' ', '_'));
                            //Subscribe this device to the new section's notifications.
                            FirebaseMessaging.getInstance().subscribeToTopic(response.body().getCode().replace(' ','_'));
                            //Update the data in memory.
                            currentUser.getCurrentStudent().setSection(response.body());
                            Toast.makeText(requireContext(),getString(R.string.section_changed_successfully),Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                            dismiss();
                        }
                        else
                        {
                            Toast.makeText(requireContext(),getString(R.string.unknown_error),Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Section> call, @NonNull Throwable t) {
                        Toast.makeText(requireContext(),getString(R.string.connection_failed),Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                binding.txtSectionLayout.setError(getString(R.string.empty_section_msg));
            }
        });

        // Spinner
        binding.txtSectionLayout.setOnClickListener(v -> {
            if (sections.isEmpty()){
                Toast.makeText(requireContext(), getString(R.string.no_sections), Toast.LENGTH_SHORT).show();
            }
        });

        binding.txtSectionSpinner.setOnItemClickListener((parent, view1, position, id) -> {
            binding.txtSectionLayout.setError(null);
            sectionSelected = true;
            section = sections.get(position);
        });

        return view;
    }

    private void getAllSections() {

        Call<List<Section>> getAllSectionsCall = api.getAllSections();
        getAllSectionsCall.enqueue(new Callback<List<Section>>() {
            @Override
            public void onResponse(@NonNull Call<List<Section>> call,@NonNull  Response<List<Section>> response) {
                if(response.code()==Utils.HttpResponses.HTTP_200_OK)
                {
                    List<Section> sectionsList = response.body();
                    sections.clear();
                    if (sectionsList != null) {
                        // Get names
                        for (Section sec : sectionsList) {
                            sections.add(sec.getCode());
                        }
                    }
                    // Set up spinner
                    ArrayAdapter<String> sectionsAdapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item, sections);
                    binding.txtSectionSpinner.setAdapter(sectionsAdapter);
                }
                else
                {
                    Toast.makeText(requireContext(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Section>> call,@NonNull  Throwable t) {
                Toast.makeText(requireContext(), getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }
}