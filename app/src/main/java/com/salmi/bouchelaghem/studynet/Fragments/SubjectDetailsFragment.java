package com.salmi.bouchelaghem.studynet.Fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.salmi.bouchelaghem.studynet.databinding.FragmentSubjectDetailsBinding;
import com.salmi.bouchelaghem.studynet.databinding.FragmentSubjectsBinding;

public class SubjectDetailsFragment extends BottomSheetDialogFragment {

    private FragmentSubjectDetailsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSubjectDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }
}