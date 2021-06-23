package com.salmi.bouchelaghem.studynet.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.salmi.bouchelaghem.studynet.Models.Module;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.databinding.FragmentSubjectDetailsBinding;

public class SubjectDetailsFragment extends BottomSheetDialogFragment {

    private FragmentSubjectDetailsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        for (int i = 0; i < module.getTypes().size() - 1; i++) {
            binding.txtSubjectTypes.append(module.getTypes().get(i) + ", ");
        }
        binding.txtSubjectTypes.append(module.getTypes().get(module.getTypes().size() - 1));

        if (module.getTeachers().size() > 1) {
            binding.textView1.setText(getString(R.string.teachers));
        }
        if (module.getTeachers().size() > 0) {
            binding.txtSubjectTeacher.setText("");
            for (int i = 0; i < module.getTeachers().size() - 1; i++) {
                binding.txtSubjectTeacher.append(module.getTeachers().get(i) + '\n');
            }
            binding.txtSubjectTeacher.append(module.getTeachers().get(module.getTeachers().size() - 1));
        } else {
            binding.txtSubjectTeacher.setText(getString(R.string.no_teachers_msg));
        }
    }
}