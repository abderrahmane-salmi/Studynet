package com.salmi.bouchelaghem.studynet.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentChangeSectionBinding;

import java.util.ArrayList;
import java.util.List;

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
    private final List<String> sections = new ArrayList<>();
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
                // TODO: Change section
                Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show();
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
        // TODO: Get all sections
    }
}