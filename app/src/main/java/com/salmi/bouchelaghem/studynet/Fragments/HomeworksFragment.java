package com.salmi.bouchelaghem.studynet.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.salmi.bouchelaghem.studynet.Activities.AddHomeworkActivity;
import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.Adapters.HomeworkAdapter;
import com.salmi.bouchelaghem.studynet.Models.Admin;
import com.salmi.bouchelaghem.studynet.Models.Homework;
import com.salmi.bouchelaghem.studynet.Models.Module;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentHomeworksBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeworksFragment extends Fragment {

    private FragmentHomeworksBinding binding;
    private CurrentUser currentUser = CurrentUser.getInstance();

    // Recycler View
    private List<Homework> homeworks;
    private List<Homework> filteredHomeworks;
    private HomeworkAdapter adapter;

    // Filter
    private Dialog dialog;
    // Fields
    private boolean filterApplied = false;
    private String filteredSection, filteredModule, filteredDate;
    // Lists
    private List<String> modulesNames, sectionsNames;

    // Test api
    TestAPI testAPI = TestAPI.getInstance();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeworksBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Get current user type
        String currentUserType = currentUser.getUserType();
        initRecView();

        // Show the filter button
        NavigationActivity context = (NavigationActivity) getActivity();
        assert context != null;
        context.btnFilter.setVisibility(View.VISIBLE);

        switch (currentUserType){
            case Utils.TEACHER_ACCOUNT:
                // Get the current teacher from the app API
                Teacher teacher = currentUser.getCurrentTeacher();

                // Show the select section msg
                binding.selectSectionMsg.setVisibility(View.VISIBLE);

                // Show the add button
                binding.btnAdd.setVisibility(View.VISIBLE);
                binding.btnAdd.setOnClickListener(v -> {
                    Intent intent = new Intent(context, AddHomeworkActivity.class);
                    intent.putExtra(Utils.ACTION, Utils.ACTION_ADD);
                    startActivity(intent);
                });

                // Init filter
                context.btnFilter.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view12 = View.inflate(context, R.layout.popup_teacher_homeworks_filter, null);
                    // Init Views
                    ImageView btnCloseFilter = view12.findViewById(R.id.btnCloseFilter);
                    AutoCompleteTextView filterTimetableSection = view12.findViewById(R.id.filterHomeworksSection);
                    AutoCompleteTextView filterHomeworksModule = view12.findViewById(R.id.filterHomeworksSubject);
                    MaterialButton btnApplyFilter = view12.findViewById(R.id.btnApplyFilter);
                    TextInputLayout filterHomeworksSubjectLayout = view12.findViewById(R.id.filterHomeworksSubjectLayout);

                    // Init sections & modules list
                    // Init sections list
                    sectionsNames = getTeacherSections(teacher.getId());
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, sectionsNames);
                    filterTimetableSection.setAdapter(arrayAdapter);

                    // TODO: restoreFilterState(); // set the filter values to the last filter applied

                    // Init Buttons
                    btnApplyFilter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    btnCloseFilter.setOnClickListener(v1 -> dialog.dismiss());

                    filterTimetableSection.setOnItemClickListener((parent, view13, position, id) -> {
                        // Get the selected section
                        filteredSection = sectionsNames.get(position);
                        // When the user chooses a section we will fill the modules spinner for him
                        modulesNames = getTeacherModules(teacher.getId(), filteredSection);
                        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(context, R.layout.dropdown_item, modulesNames);
                        filterHomeworksModule.setAdapter(arrayAdapter2);
                        filterHomeworksSubjectLayout.setEnabled(true);
                    });

                    filterHomeworksModule.setOnItemClickListener((parent, view14, position, id) -> filteredModule = modulesNames.get(position));

                    builder.setView(view12);
                    dialog = builder.create(); // creating our dialog
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    // Show rounded corners
                    WindowManager.LayoutParams params =   dialog.getWindow().getAttributes();
                    dialog.getWindow().setAttributes(params);
                });
                break;
            case Utils.ADMIN_ACCOUNT:
                // Get the current teacher from the app API
                Admin admin = currentUser.getCurrentAdmin();
                // Show the select section msg
                binding.selectSectionMsg.setVisibility(View.VISIBLE);

                // Init filter
                context.btnFilter.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view12 = View.inflate(context, R.layout.popup_teacher_homeworks_filter, null);
                    // Init Views
                    ImageView btnCloseFilter = view12.findViewById(R.id.btnCloseFilter);
                    AutoCompleteTextView filterTimetableSection = view12.findViewById(R.id.filterHomeworksSection);
                    AutoCompleteTextView filterHomeworksModule = view12.findViewById(R.id.filterHomeworksSubject);
                    MaterialButton btnApplyFilter = view12.findViewById(R.id.btnApplyFilter);
                    TextInputLayout filterHomeworksSubjectLayout = view12.findViewById(R.id.filterHomeworksSubjectLayout);

                    // Init sections & modules list
                    // Init sections list
                    sectionsNames = getAllSections();
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, sectionsNames);
                    filterTimetableSection.setAdapter(arrayAdapter);

                    // TODO: restoreFilterState(); // set the filter values to the last filter applied

                    // Init Buttons
                    btnApplyFilter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    btnCloseFilter.setOnClickListener(v1 -> dialog.dismiss());

                    filterTimetableSection.setOnItemClickListener((parent, view13, position, id) -> {
                        // Get the selected section
                        filteredSection = sectionsNames.get(position);
                        // When the user chooses a section we will fill the modules spinner for him
                        modulesNames = getSectionModules(filteredSection);
                        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(context, R.layout.dropdown_item, modulesNames);
                        filterHomeworksModule.setAdapter(arrayAdapter2);
                        filterHomeworksSubjectLayout.setEnabled(true);
                    });

                    filterHomeworksModule.setOnItemClickListener((parent, view14, position, id) -> filteredModule = modulesNames.get(position));

                    builder.setView(view12);
                    dialog = builder.create(); // creating our dialog
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    // Show rounded corners
                    WindowManager.LayoutParams params =   dialog.getWindow().getAttributes();
                    dialog.getWindow().setAttributes(params);
                });
                break;
            case Utils.STUDENT_ACCOUNT:
                // Get current student
                Student student = currentUser.getCurrentStudent();
                // Show all homeworks by default
                getAllHomeworks(student.getSection().getCode());

                // Init filter
                context.btnFilter.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view1 = View.inflate(context, R.layout.popup_student_homworks_filter, null);
                    // Init Views
                    ImageView btnCloseFilter = view1.findViewById(R.id.btnCloseFilter);
                    AutoCompleteTextView filterHomeworksDate = view1.findViewById(R.id.filterHomeworksDate);
                    MaterialButton btnApplyFilter = view1.findViewById(R.id.btnApplyFilter);

                    // Init filter values
                    // TODO: Get the values in english (not local language)
                    List<String> filterValues = Arrays.asList(getResources().getStringArray(R.array.homework_filter_values));
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, filterValues);
                    filterHomeworksDate.setAdapter(arrayAdapter);

                    // TODO: restoreFilterState(); // set the filter values to the last filter applied

                    // Init Buttons
                    btnApplyFilter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    btnCloseFilter.setOnClickListener(v12 -> dialog.dismiss());

                    filterHomeworksDate.setOnItemClickListener((parent, view2, position, id) -> filteredDate = filterValues.get(position));

                    builder.setView(view1);
                    dialog = builder.create(); // creating our dialog
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    // Show rounded corners
                    WindowManager.LayoutParams params =   dialog.getWindow().getAttributes();
                    dialog.getWindow().setAttributes(params);
                });
                break;
        }

        return view;
    }

    private void getAllHomeworks(String section) {
        homeworks = testAPI.getHomework();
        if (!homeworks.isEmpty()){
            adapter.setHomeworks(homeworks);
            binding.homeworksRecView.setAdapter(adapter);
            binding.homeworksRecView.setVisibility(View.VISIBLE);
            binding.emptyMsg.setVisibility(View.GONE);
        } else {
            binding.homeworksRecView.setVisibility(View.GONE);
            binding.emptyMsg.setVisibility(View.VISIBLE);
        }
    }

    // Get all the selected section's modules
    private List<String> getSectionModules(String section) {
        // Get all the subjects
        List<Module> modules = testAPI.getModules();

        // Get only the teacher's sections
        List<String> modulesNames = new ArrayList<>();
        for (Module module : modules) {
            modulesNames.add(module.getCode());
        }
        return modulesNames;
    }

    // Get all sections
    private List<String> getAllSections() {
        // Get all the sections
        List<Section> sections = testAPI.getSections();

        // Get only the teacher's sections
        List<String> sectionsNames = new ArrayList<>();
        for (Section section : sections) {
            sectionsNames.add(section.getCode());
        }
        return sectionsNames;
    }

    private List<String> getTeacherModules(int teacherId, String section) {
        // Get all the subjects
        List<Module> modules = testAPI.getModules();

        // Get only the teacher's sections
        List<String> modulesNames = new ArrayList<>();
        for (Module module : modules) {
            modulesNames.add(module.getCode());
        }
        return modulesNames;
    }

    private List<String> getTeacherSections(int teacherId) {
        // Get all the sections
        List<Section> sections = testAPI.getSections();

        // Get only the teacher's sections
        List<String> sectionsNames = new ArrayList<>();
        for (Section section : sections) {
            sectionsNames.add(section.getCode());
        }
        return sectionsNames;
    }

    private void initRecView() {
        homeworks = new ArrayList<>();
        filteredHomeworks = new ArrayList<>();
        binding.homeworksRecView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.homeworksRecView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayout.VERTICAL));
        adapter = new HomeworkAdapter(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}