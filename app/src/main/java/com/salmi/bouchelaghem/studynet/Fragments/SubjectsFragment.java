package com.salmi.bouchelaghem.studynet.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.Adapters.ModulesAdapter;
import com.salmi.bouchelaghem.studynet.Models.Module;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.databinding.FragmentSubjectsBinding;

import java.util.ArrayList;
import java.util.List;

public class SubjectsFragment extends Fragment {

    private FragmentSubjectsBinding binding;

    // Recycler view
    private List<Module> modules;
    private ModulesAdapter adapter;

    private Student currentStudent;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSubjectsBinding.inflate(inflater, container, false);
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
        modules = new ArrayList<>();
        binding.subjectRecView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ModulesAdapter(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (modules != null && modules.isEmpty()) {
            // if its the first time we launch the activity so go ahead and get the data from the database
            getModules(currentStudent.getSection().getCode());
        } else { // if we already retrieved the data from the database, just keep using them
            adapter.setModules(modules);
            binding.subjectRecView.setAdapter(adapter);
            binding.subjectRecView.setVisibility(View.VISIBLE);
            binding.emptyMsg.setVisibility(View.GONE);
        }
    }

    // Get the current section's modules
    private void getModules(String section) {
        modules = TestAPI.getInstance().getModules();
        if (!modules.isEmpty()) {
            adapter.setModules(modules);
            binding.subjectRecView.setAdapter(adapter);
            binding.subjectRecView.setVisibility(View.VISIBLE);
            binding.emptyMsg.setVisibility(View.GONE);
        } else {
            binding.subjectRecView.setVisibility(View.GONE);
            binding.emptyMsg.setVisibility(View.VISIBLE);
        }
    }
}