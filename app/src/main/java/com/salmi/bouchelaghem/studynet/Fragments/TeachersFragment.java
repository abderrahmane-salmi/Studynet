package com.salmi.bouchelaghem.studynet.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.databinding.FragmentSubjectsBinding;
import com.salmi.bouchelaghem.studynet.databinding.FragmentTeachersBinding;

public class TeachersFragment extends Fragment {

    private FragmentTeachersBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTeachersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Hide filter button
        NavigationActivity context = (NavigationActivity) getActivity();
        assert context != null;
        context.btnFilter.setVisibility(View.GONE);

        binding.included.teacherMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_teachers_to_teacherDetailsFragment);
            }
        });

        return view;
    }
}