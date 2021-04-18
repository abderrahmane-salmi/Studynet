package com.salmi.bouchelaghem.studynet.Fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.salmi.bouchelaghem.studynet.databinding.FragmentSubjectDetailsBinding;
import com.salmi.bouchelaghem.studynet.databinding.FragmentTeacherDetailsBinding;

public class TeacherDetailsFragment extends BottomSheetDialogFragment {

    private FragmentTeacherDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTeacherDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }
}