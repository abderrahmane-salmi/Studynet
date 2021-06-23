package com.salmi.bouchelaghem.studynet.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.Adapters.ModulesAdapter;
import com.salmi.bouchelaghem.studynet.Models.Module;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentSubjectsBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubjectsFragment extends Fragment {

    private FragmentSubjectsBinding binding;

    // Recycler view
    private List<Module> modules;
    private ModulesAdapter adapter;

    private Student currentStudent;

    // Studynet Api
    private StudynetAPI api;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSubjectsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api
        api = retrofit.create(StudynetAPI.class);

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
        adapter = new ModulesAdapter(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (modules.isEmpty()) {
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
        // Start loading animation
        binding.loadingAnimation.setVisibility(View.VISIBLE);
        binding.loadingAnimation.playAnimation();
        Call<List<Module>> call = api.getSectionModules(section);
        call.enqueue(new Callback<List<Module>>() {
            @Override
            public void onResponse(@NonNull Call<List<Module>> call, @NonNull Response<List<Module>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        modules = new ArrayList<>(response.body());
                        if (!modules.isEmpty()) {
                            adapter.setModules(modules);
                            binding.subjectRecView.setAdapter(adapter);
                            binding.subjectRecView.setVisibility(View.VISIBLE);
                            binding.emptyMsg.setVisibility(View.GONE);
                        } else {
                            binding.subjectRecView.setVisibility(View.GONE);
                            binding.emptyMsg.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.subjectRecView.setVisibility(View.GONE);
                        binding.emptyMsg.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
                }
                // Stop loading animation
                binding.loadingAnimation.setVisibility(View.GONE);
                binding.loadingAnimation.cancelAnimation();
            }

            @Override
            public void onFailure(@NonNull Call<List<Module>> call, @NonNull Throwable t) {
                // Stop loading animation
                binding.loadingAnimation.setVisibility(View.GONE);
                binding.loadingAnimation.cancelAnimation();
                Toast.makeText(getContext(), getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}