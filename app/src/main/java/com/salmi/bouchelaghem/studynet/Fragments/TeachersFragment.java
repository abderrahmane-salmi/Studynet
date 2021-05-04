package com.salmi.bouchelaghem.studynet.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.salmi.bouchelaghem.studynet.Activities.AddTeacherActivity;
import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.Adapters.TeachersAdapter;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentTeachersBinding;

import java.util.ArrayList;
import java.util.List;

public class TeachersFragment extends Fragment {

    private FragmentTeachersBinding binding;

    // Recycler view
    private List<Teacher> teachers;
    private TeachersAdapter adapter;

    private final CurrentUser currentUser = CurrentUser.getInstance();
    private String userType;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTeachersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        userType = currentUser.getUserType();
        initRecView();

        // Hide filter button
        NavigationActivity context = (NavigationActivity) getActivity();
        assert context != null;
        context.btnFilter.setVisibility(View.GONE);

        if (userType.equals(Utils.ADMIN_ACCOUNT)){
            // Show add button
            binding.btnAdd.setVisibility(View.VISIBLE);
            binding.btnAdd.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AddTeacherActivity.class);
                intent.putExtra(Utils.ACTION, Utils.ACTION_ADD);
                startActivity(intent);
            });
        }

        return view;
    }

    private void initRecView() {
        teachers = new ArrayList<>();
        binding.teachersRecView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeachersAdapter(getContext(), userType);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (teachers != null && teachers.isEmpty()) {
            // if its the first time we launch the activity so go ahead and get the data from the database
            switch (userType){
                case Utils.STUDENT_ACCOUNT:
                    // Get the current student
                    Student currentStudent = CurrentUser.getInstance().getCurrentStudent();
                    getMyTeachers(currentStudent.getSection().getCode());
                    break;
                case Utils.ADMIN_ACCOUNT:
                    getAllTeachers();
                    break;
            }
        } else { // if we already retrieved the data from the database, just keep using it
            adapter.setTeachers(teachers);
            binding.teachersRecView.setAdapter(adapter);
            binding.teachersRecView.setVisibility(View.VISIBLE);
            binding.emptyMsg.setVisibility(View.GONE);
        }
    }

    // Get the current section's teachers
    private void getMyTeachers(String section){
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

    private void getAllTeachers() {
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