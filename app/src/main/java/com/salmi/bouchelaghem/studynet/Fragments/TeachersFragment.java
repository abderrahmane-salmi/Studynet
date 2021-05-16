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
import com.salmi.bouchelaghem.studynet.Activities.AddAssignmentActivity;
import com.salmi.bouchelaghem.studynet.Activities.AddTeacherActivity;
import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.Adapters.TeachersAdapter;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentTeachersBinding;

import java.util.ArrayList;
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
    private List<Teacher> teachers;
    private TeachersAdapter adapter;

    // Filter
    private Dialog dialog;
    private boolean sectionSelected = false;
    private String selectedSection;
    private boolean filterApplied = false;
    private List<Section> sections;
    private List<String> sectionsNames;

    private final CurrentUser currentUser = CurrentUser.getInstance();
    private String userType;

    TestAPI testAPI = TestAPI.getInstance();

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

        if (userType.equals(Utils.ADMIN_ACCOUNT)){
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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view12 = View.inflate(context, R.layout.popup_teacher_timetable_filter, null);
                // Init Views
                ImageView btnCloseFilter = view12.findViewById(R.id.btnCloseFilter);
                AutoCompleteTextView filterTimetableSection = view12.findViewById(R.id.filterTimetableSection);
                MaterialButton btnApplyFilter = view12.findViewById(R.id.btnApplyFilter);

                // Init sections spinner
                if (!sectionsNames.isEmpty()) {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, sectionsNames);
                    filterTimetableSection.setAdapter(arrayAdapter);
                }

                if (filterApplied){
                    restoreFilterState(filterTimetableSection); // set the filter values to the last filter applied
                }

                // Init Buttons
                btnApplyFilter.setOnClickListener(v13 -> {

                    if (sectionSelected) {

                        getTeachers(selectedSection);
                        binding.selectSectionMsg.setVisibility(View.GONE);
                        dialog.dismiss();
                        filterApplied = true;

                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_filter_msg), Toast.LENGTH_SHORT).show();
                    }

                });

                btnCloseFilter.setOnClickListener(v14 -> dialog.dismiss());

                filterTimetableSection.setOnItemClickListener((parent, view121, position, id) -> {
                    sectionSelected = true;
                    selectedSection = sectionsNames.get(position);
                });

                builder.setView(view12);
                dialog = builder.create(); // creating our dialog
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                // Show rounded corners
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                dialog.getWindow().setAttributes(params);
            });
        } else if (userType.equals(Utils.STUDENT_ACCOUNT)){
            // Hide filter button
            context.btnFilter.setVisibility(View.GONE);
        }

        return view;
    }

    private void initRecView() {
        teachers = new ArrayList<>();
        binding.teachersRecView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TeachersAdapter(getContext(), userType);

        // Swipe to action in rec view
        if (userType.equals(Utils.ADMIN_ACCOUNT)){
            // If its a teacher then show delete + edit buttons
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adminCallBack);
            itemTouchHelper.attachToRecyclerView(binding.teachersRecView);
        }
    }

    private void restoreFilterState(AutoCompleteTextView filter) {
        filter.setText(selectedSection, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (teachers != null && teachers.isEmpty()) {
            // if its the first time we launch the activity so go ahead and get the data from the database
            if (Utils.STUDENT_ACCOUNT.equals(userType)) {// Get the current student
                Student currentStudent = CurrentUser.getInstance().getCurrentStudent();
                getTeachers(currentStudent.getSection().getCode());
            } else if (Utils.ADMIN_ACCOUNT.equals(userType)){
                // If its an admin go ahead and get all sections for the spinner
                getAllSections();
            }
        } else { // if we already retrieved the data from the database, just keep using it
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

    private void getAllSections(){
        Call<List<Section>> call = api.getAllSections();
        call.enqueue(new Callback<List<Section>>() {
            @Override
            public void onResponse(@NonNull Call<List<Section>> call, @NonNull Response<List<Section>> response) {
                if (response.isSuccessful()){
                    sections = response.body();
                    sectionsNames = new ArrayList<>();
                    if (sections != null){
                        // Get the sections names only
                        for (Section section : sections) {
                            sectionsNames.add(section.getCode());
                        }
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.error)+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Section>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Swipe to delete and edit in the recycler view
    ItemTouchHelper.SimpleCallback adminCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            Teacher currentTeacher = adapter.getTeachers().get(position);

            switch (direction){
                case ItemTouchHelper.LEFT: // Swipe left to right <- : Delete item

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.are_you_sure);
                    builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                        teachers.remove(currentTeacher);
                        adapter.getTeachers().remove(currentTeacher);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(getContext(), getString(R.string.teacher_deleted_msg), Toast.LENGTH_SHORT).show();
                    });
                    builder.setNegativeButton(R.string.no, (dialog, which) -> {
                        // Do Nothing
                        adapter.notifyItemChanged(position); // To reset the item on the screen
                    });
                    builder.create().show();
                    break;
                case ItemTouchHelper.RIGHT: // Swipe right to left -> : Edit item
                    Intent intent = new Intent(getContext(), AddTeacherActivity.class);
                    intent.putExtra(Utils.ACTION, Utils.ACTION_UPDATE);
                    intent.putExtra(Utils.TEACHER, currentTeacher);
                    startActivity(intent);
                    adapter.notifyItemChanged(position); // To reset the item on the screen
                    break;
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

}