package com.salmi.bouchelaghem.studynet.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.Adapters.TeachersAdapter;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.databinding.FragmentSubjectsBinding;
import com.salmi.bouchelaghem.studynet.databinding.FragmentTeachersBinding;

import java.util.ArrayList;
import java.util.List;

public class TeachersFragment extends Fragment {

    private FragmentTeachersBinding binding;

    // Recycler view
    private List<Teacher> teachers;
    private TeachersAdapter adapter;

    private Student currentStudent;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTeachersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        currentStudent = CurrentUser.getInstance().getCurrentStudent();
        initRecView();

        // Hide filter button
        NavigationActivity context = (NavigationActivity) getActivity();
        assert context != null;
        context.btnFilter.setVisibility(View.GONE);

        return view;
    }

    private void initRecView() {
        teachers = new ArrayList<>();
        binding.teachersRecView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeachersAdapter(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (teachers != null && teachers.isEmpty()) {
            // if its the first time we launch the activity so go ahead and get the data from the database
            getTeachers(currentStudent.getSection().getCode());
        } else { // if we already retrieved the data from the database, just keep using them
            adapter.setTeachers(teachers);
            binding.teachersRecView.setAdapter(adapter);
            binding.teachersRecView.setVisibility(View.VISIBLE);
            binding.emptyMsg.setVisibility(View.GONE);
        }
    }

    // Get the current section's teachers
    private void getTeachers(String section){
        teachers = TestAPI.getInstance().getTeachers();
        if (!teachers.isEmpty()) {
            adapter.setTeachers(teachers);
            binding.teachersRecView.setAdapter(adapter);
            binding.teachersRecView.setVisibility(View.VISIBLE);
            binding.emptyMsg.setVisibility(View.GONE);
        } else {
            binding.teachersRecView.setVisibility(View.GONE);
            binding.emptyMsg.setVisibility(View.VISIBLE);
        }
    }
}