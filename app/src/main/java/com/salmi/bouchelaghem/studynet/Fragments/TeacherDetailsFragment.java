package com.salmi.bouchelaghem.studynet.Fragments;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.databinding.FragmentTeacherDetailsBinding;

@SuppressLint("IntentReset")
public class TeacherDetailsFragment extends BottomSheetDialogFragment {

    private FragmentTeacherDetailsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTeacherDetailsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Get the selected teacher
        Teacher teacher = TeacherDetailsFragmentArgs.fromBundle(requireArguments()).getTeacher();

        fillFields(teacher);

        binding.btnContactTeacher.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            // The intent does not have a URI, so declare the "text/plain" MIME type
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{teacher.getEmail()}); // recipient
            try {
                startActivity(emailIntent);
            } catch (ActivityNotFoundException exception) {
                // There is no third app to open our intent, so do nothing
            }
        });

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void fillFields(Teacher teacher) {
        binding.txtTeacherName.setText(teacher.getFirstName() + " " + teacher.getLastName());
        binding.txtTeacherGrade.setText(teacher.getGrade());
        binding.txtTeacherEmail.setText(teacher.getEmail());
    }
}