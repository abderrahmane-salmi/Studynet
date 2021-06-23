package com.salmi.bouchelaghem.studynet.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.salmi.bouchelaghem.studynet.Activities.AddTeacherActivity;
import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.Adapters.TeachersAdapter;
import com.salmi.bouchelaghem.studynet.Models.Department;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.Serializers;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentTeachersBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TeachersFragment extends Fragment {

    private FragmentTeachersBinding binding;

    // Recycler view
    private static List<Teacher> teachers;
    private TeachersAdapter adapter;

    // Filter
    private Dialog dialog;
    private boolean departmentSelected = false;
    private static String selectedDepartment;
    private boolean filterApplied = false;
    private List<Department> departments;
    private List<String> departmentsCodes;

    private final CurrentUser currentUser = CurrentUser.getInstance();
    private String userType;

    // Studynet Api
    private StudynetAPI api;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTeachersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        userType = currentUser.getUserType();
        initRecView();

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api
        api = retrofit.create(StudynetAPI.class);

        NavigationActivity context = (NavigationActivity) getActivity();
        assert context != null;

        if (userType.equals(Utils.ADMIN_ACCOUNT)) {
            // Show the select section msg
            binding.selectSectionMsg.setVisibility(View.VISIBLE);

            // Show the add button
            binding.btnAdd.setVisibility(View.VISIBLE);
            binding.btnAdd.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AddTeacherActivity.class);
                intent.putExtra(Utils.ACTION, Utils.ACTION_ADD);
                startActivity(intent);
            });

            // Show and setup the filter
            context.btnFilter.setVisibility(View.VISIBLE);
            context.btnFilter.setOnClickListener(v -> {
                if (departments != null) {
                    // Show the filter just once
                    if (dialog == null || !dialog.isShowing()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View view12 = View.inflate(context, R.layout.popup_admin_teachers_filter, null);
                        // Init Views
                        ImageView btnCloseFilter = view12.findViewById(R.id.btnCloseFilter);
                        AutoCompleteTextView filterDepartmentsSpinner = view12.findViewById(R.id.filterTimetableSection);
                        MaterialButton btnApplyFilter = view12.findViewById(R.id.btnApplyFilter);

                        // Init departments spinner
                        if (!departmentsCodes.isEmpty()) {
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, departmentsCodes);
                            filterDepartmentsSpinner.setAdapter(arrayAdapter);
                        }

                        // set the filter values to the last filter applied
                        if (filterApplied) {
                            restoreFilterState(filterDepartmentsSpinner);
                        }

                        // Init Buttons
                        btnApplyFilter.setOnClickListener(v13 -> {
                            if (departmentSelected) {
                                getDepartmentTeachers(selectedDepartment);
                                binding.selectSectionMsg.setVisibility(View.GONE);
                                dialog.dismiss();
                                filterApplied = true;
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.no_filter_msg), Toast.LENGTH_SHORT).show();
                            }

                        });

                        btnCloseFilter.setOnClickListener(v14 -> dialog.dismiss());

                        filterDepartmentsSpinner.setOnItemClickListener((parent, view121, position, id) -> {
                            departmentSelected = true;
                            selectedDepartment = departmentsCodes.get(position);
                        });

                        builder.setView(view12);
                        dialog = builder.create(); // creating our dialog
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        // Show rounded corners
                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                        dialog.getWindow().setAttributes(params);
                    }
                }
            });
        } else if (userType.equals(Utils.STUDENT_ACCOUNT)) {
            // Hide filter button
            context.btnFilter.setVisibility(View.GONE);
        }

        return view;
    }

    private void initRecView() {
        teachers = new ArrayList<>();
        binding.teachersRecView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeachersAdapter(getActivity());

        // Swipe to action in rec view
        if (userType.equals(Utils.ADMIN_ACCOUNT)) {
            // If its a teacher then show delete + edit buttons
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adminCallBack);
            itemTouchHelper.attachToRecyclerView(binding.teachersRecView);
        }
    }

    private void restoreFilterState(AutoCompleteTextView filter) {
        filter.setText(selectedDepartment, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (teachers != null && teachers.isEmpty()) {
            // if its the first time we launch the activity so go ahead and get the data from the database
            if (Utils.STUDENT_ACCOUNT.equals(userType)) {// Get the current student
                Student currentStudent = CurrentUser.getInstance().getCurrentStudent();
                getSectionTeachers(currentStudent.getSection().getCode());
            } else if (Utils.ADMIN_ACCOUNT.equals(userType)) {
                // If its an admin go ahead and get all sections for the spinner
                getAllDepartments();
            }
        } else { // if we already retrieved the data from the database, just keep using it
            adapter.setTeachers(teachers);
            binding.teachersRecView.setAdapter(adapter);
            binding.teachersRecView.setVisibility(View.VISIBLE);
            binding.emptyMsg.setVisibility(View.GONE);
        }
    }

    // Get the current section's teachers
    private void getSectionTeachers(String section) {
        // Start loading animation
        binding.loadingAnimation.setVisibility(View.VISIBLE);
        binding.loadingAnimation.playAnimation();
        Call<JsonArray> call = api.getSectionTeachers("Token " + currentUser.getToken(), section);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                if (response.code() == Utils.HttpResponses.HTTP_200_OK) {

                    teachers = new ArrayList<>();
                    JsonArray teachersJsonArray = response.body();
                    if (teachersJsonArray != null) {
                        for (int i = 0; i < teachersJsonArray.size(); ++i) {
                            teachers.add(Serializers.TeacherDeserializer(teachersJsonArray.get(i).getAsJsonObject()));
                        }
                        if (!teachers.isEmpty()) {
                            //Sort the teacher list by their names.
                            Collections.sort(teachers, (o1, o2) -> o1.getLastName().compareToIgnoreCase(o2.getLastName()));
                            //Display the teachers list.
                            adapter.setTeachers(teachers);
                            binding.teachersRecView.setAdapter(adapter);
                            binding.teachersRecView.setVisibility(View.VISIBLE);
                            binding.emptyMsg.setVisibility(View.GONE);
                        } else {
                            binding.teachersRecView.setVisibility(View.GONE);
                            binding.emptyMsg.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.error) + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    binding.teachersRecView.setVisibility(View.GONE);
                    binding.emptyMsg.setVisibility(View.VISIBLE);
                }
                // Stop loading animation
                binding.loadingAnimation.setVisibility(View.GONE);
                binding.loadingAnimation.cancelAnimation();
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                // Stop loading animation
                binding.loadingAnimation.setVisibility(View.GONE);
                binding.loadingAnimation.cancelAnimation();
                Toast.makeText(getContext(), getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Get the current section's teachers
    private void getDepartmentTeachers(String department) {
        // Start loading animation
        binding.loadingAnimation.setVisibility(View.VISIBLE);
        binding.loadingAnimation.playAnimation();
        Call<JsonArray> call = api.getDepartmentTeachers("Token " + currentUser.getToken(), department);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                if (response.code() == Utils.HttpResponses.HTTP_200_OK) {
                    teachers = new ArrayList<>();
                    JsonArray teachersJsonArray = response.body();
                    if (teachersJsonArray != null) {
                        for (int i = 0; i < teachersJsonArray.size(); ++i) {
                            teachers.add(Serializers.TeacherDeserializer(teachersJsonArray.get(i).getAsJsonObject()));
                        }
                        if (!teachers.isEmpty()) {
                            //Sort the teacher list by their names.
                            Collections.sort(teachers, (o1, o2) -> o1.getLastName().compareToIgnoreCase(o2.getLastName()));
                            //Display the teachers list.
                            adapter.setTeachers(teachers);
                            binding.teachersRecView.setAdapter(adapter);
                            binding.teachersRecView.setVisibility(View.VISIBLE);
                            binding.emptyMsg.setVisibility(View.GONE);
                        } else {
                            binding.teachersRecView.setVisibility(View.GONE);
                            binding.emptyMsg.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.error) + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    binding.teachersRecView.setVisibility(View.GONE);
                    binding.emptyMsg.setVisibility(View.VISIBLE);
                }
                // Stop loading animation
                binding.loadingAnimation.setVisibility(View.GONE);
                binding.loadingAnimation.cancelAnimation();
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                // Stop loading animation
                binding.loadingAnimation.setVisibility(View.GONE);
                binding.loadingAnimation.cancelAnimation();
                Toast.makeText(getContext(), getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAllDepartments() {
        // Get the departments from the api
        Call<List<Department>> call = api.getDepartments();
        call.enqueue(new Callback<List<Department>>() {
            @Override
            public void onResponse(@NonNull Call<List<Department>> call, @NonNull Response<List<Department>> response) {
                if (response.isSuccessful()) {
                    departments = response.body();
                    departmentsCodes = new ArrayList<>();
                    if (departments != null) {
                        // Get the departments codes only
                        for (Department section : departments) {
                            departmentsCodes.add(section.getCode());
                        }
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.error) + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Department>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), getString(R.string.error) + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Swipe to delete and edit in the recycler view
    ItemTouchHelper.SimpleCallback adminCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            Teacher currentTeacher = adapter.getTeachers().get(position);

            if (direction == ItemTouchHelper.RIGHT) { // Swipe right to left -> : Edit item
                Intent intent = new Intent(getContext(), AddTeacherActivity.class);
                intent.putExtra(Utils.ACTION, Utils.ACTION_UPDATE);
                intent.putExtra(Utils.TEACHER, currentTeacher);
                startActivity(intent);
                adapter.notifyItemChanged(position); // To reset the item on the screen
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                    .addSwipeRightActionIcon(R.drawable.ic_modify)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    public static List<Teacher> getTeachers() {
        return teachers;
    }

    public static String getSelectedDepartment() {
        return selectedDepartment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        teachers = null;
        selectedDepartment = null;
    }
}