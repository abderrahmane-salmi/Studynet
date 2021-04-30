package com.salmi.bouchelaghem.studynet.Fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.salmi.bouchelaghem.studynet.Models.Module;
import com.salmi.bouchelaghem.studynet.databinding.FragmentSubjectDetailsBinding;
import com.salmi.bouchelaghem.studynet.databinding.FragmentSubjectsBinding;

public class SubjectDetailsFragment extends BottomSheetDialogFragment {

    private FragmentSubjectDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSubjectDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Get the selected module
        Module module = SubjectDetailsFragmentArgs.fromBundle(requireArguments()).getModule();

        fillFields(module);

        return view;
    }

    private void fillFields(Module module) {
        binding.txtSubjectName.setText(module.getName());
        binding.txtSubjectTypes.setText("");
        for (int i=0; i<module.getTypes().size()-1; i++){
            binding.txtSubjectTypes.append(module.getTypes().get(i) + ", ");
        }
        binding.txtSubjectTypes.append(module.getTypes().get(module.getTypes().size()-1));

        // TODO: get the teacher's name from the api
    }
}