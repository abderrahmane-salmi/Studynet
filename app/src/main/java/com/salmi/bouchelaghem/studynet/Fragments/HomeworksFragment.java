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
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.salmi.bouchelaghem.studynet.Activities.AddHomeworkActivity;
import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.Adapters.HomeworkAdapter;
import com.salmi.bouchelaghem.studynet.Models.Homework;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentHomeworksBinding;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeworksFragment extends Fragment {

    private FragmentHomeworksBinding binding;

    // Recycler View
    private List<Homework> homeworks = new ArrayList<>();
    private final List<Homework> filteredHomeworks = new ArrayList<>();
    private HomeworkAdapter adapter;

    // Filter
    private Dialog dialog;
    private boolean sectionSelected = false;
    private String selectedSection;
    private boolean filterApplied = false;
    private List<String> allSections;
    private int filteredDatePosition;
    private String filteredDate;
    private boolean dateSelected = false;

    // Current User
    private final CurrentUser currentUser = CurrentUser.getInstance();
    private final String currentUserType = currentUser.getUserType();

    // Studynet Api
    private StudynetAPI api;

    //Loading dialog
    private CustomLoadingDialog loadingDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeworksBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api
        api = retrofit.create(StudynetAPI.class);

        initRecView();

        //Init loading dialog
        loadingDialog = new CustomLoadingDialog(requireContext());
        // Show the filter button
        NavigationActivity context = (NavigationActivity) getActivity();
        assert context != null;
        context.btnFilter.setVisibility(View.VISIBLE);

        switch (currentUserType) {
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

                // setup the filter
                context.btnFilter.setOnClickListener(v -> {
                    // Show the dialog only once
                    if (dialog == null || !dialog.isShowing()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View view1 = View.inflate(context, R.layout.popup_sections_filter, null);
                        // Init Views
                        ImageView btnCloseFilter = view1.findViewById(R.id.btnCloseFilter);
                        AutoCompleteTextView filterTimetableSection = view1.findViewById(R.id.filterTimetableSection);
                        MaterialButton btnApplyFilter = view1.findViewById(R.id.btnApplyFilter);

                        // Init sections list
                        // Get all the sections
                        List<String> sections = new ArrayList<>(teacher.getSections());
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, sections);
                        filterTimetableSection.setAdapter(arrayAdapter);

                        // set the filter values to the last filter applied
                        if (filterApplied) {
                            restoreSectionsFilterState(filterTimetableSection);
                        }

                        // Apply button
                        btnApplyFilter.setOnClickListener(v1 -> {
                            if (sectionSelected) {
                                filterApplied = true;
                                getHomeworks(selectedSection);
                                binding.selectSectionMsg.setVisibility(View.GONE);
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.no_filter_msg), Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Close
                        btnCloseFilter.setOnClickListener(v12 -> dialog.dismiss());

                        // Sections spinner
                        filterTimetableSection.setOnItemClickListener((parent, view11, position, id) -> {
                            sectionSelected = true;
                            selectedSection = sections.get(position);
                        });

                        builder.setView(view1);
                        dialog = builder.create(); // creating our dialog
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        // Show rounded corners
                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                        dialog.getWindow().setAttributes(params);
                    }
                });
                break;
            case Utils.ADMIN_ACCOUNT:
                // Show the select section msg
                binding.selectSectionMsg.setVisibility(View.VISIBLE);

                // Setup filter
                context.btnFilter.setOnClickListener(v -> {
                    if (allSections != null) {
                        if (dialog == null || !dialog.isShowing()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View view12 = View.inflate(context, R.layout.popup_sections_filter, null);
                            // Init Views
                            ImageView btnCloseFilter = view12.findViewById(R.id.btnCloseFilter);
                            AutoCompleteTextView filterTimetableSection = view12.findViewById(R.id.filterTimetableSection);
                            MaterialButton btnApplyFilter = view12.findViewById(R.id.btnApplyFilter);

                            // Init sections spinner
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, allSections);
                            filterTimetableSection.setAdapter(arrayAdapter);

                            // set the filter values to the last filter applied
                            if (filterApplied) {
                                restoreSectionsFilterState(filterTimetableSection);
                            }

                            // Init Buttons
                            btnApplyFilter.setOnClickListener(v13 -> {
                                if (sectionSelected) {
                                    getHomeworks(selectedSection);
                                    binding.selectSectionMsg.setVisibility(View.GONE);
                                    dialog.dismiss();
                                    filterApplied = true;
                                } else {
                                    Toast.makeText(getActivity(), getString(R.string.no_filter_msg), Toast.LENGTH_SHORT).show();
                                }

                            });

                            // Close button
                            btnCloseFilter.setOnClickListener(v14 -> dialog.dismiss());

                            // Sections spinner
                            filterTimetableSection.setOnItemClickListener((parent, view121, position, id) -> {
                                sectionSelected = true;
                                selectedSection = allSections.get(position);
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
                break;
            case Utils.STUDENT_ACCOUNT:
                // Init filter
                context.btnFilter.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view1 = View.inflate(context, R.layout.popup_student_homworks_filter, null);
                    // Init Views
                    ImageView btnCloseFilter = view1.findViewById(R.id.btnCloseFilter);
                    AutoCompleteTextView filterHomeworksDate = view1.findViewById(R.id.filterHomeworksDate);
                    MaterialButton btnApplyFilter = view1.findViewById(R.id.btnApplyFilter);

                    // Init filter values
                    List<String> filterValues = Arrays.asList(getResources().getStringArray(R.array.homework_filter_values));
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, filterValues);
                    filterHomeworksDate.setAdapter(arrayAdapter);

                    restoreDateFilterState(filterHomeworksDate); // set the filter values to the last filter applied

                    // Init Buttons
                    btnApplyFilter.setOnClickListener(v15 -> {
                        if (dateSelected){
                            switch (filteredDatePosition){
                                case 0:
                                    //Get all homeworks
                                    adapter.setHomeworks(homeworks);
                                    binding.homeworksRecView.setAdapter(adapter);
                                    break;
                                case 1:
                                    //Get yesterday's homeworks
                                    filterHomeworksPlusDays(-1);
                                    break;
                                case 2:
                                    //Get today's homeworks
                                    filterHomeworksPlusDays(0);
                                    break;
                                case 3:
                                    //Get tomorrow's homeworks
                                    filterHomeworksPlusDays(1);
                                    break;
                                case 4:
                                    //Get this week's homeworks
                                    filterHomeworksThisWeek();
                                    break;
                            }
                            filterApplied = true;
                            dialog.dismiss();
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.no_filter_msg), Toast.LENGTH_SHORT).show();
                        }
                    });

                    btnCloseFilter.setOnClickListener(v12 -> dialog.dismiss());

                    filterHomeworksDate.setOnItemClickListener((parent, view2, position, id) -> {
                        filteredDatePosition = position;
                        filteredDate = filterValues.get(position);
                        dateSelected = true;
                    });

                    builder.setView(view1);
                    dialog = builder.create(); // creating our dialog
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    // Show rounded corners
                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    dialog.getWindow().setAttributes(params);
                });
                break;
        }

        return view;
    }

    private void getHomeworks(String section) {
        // Start loading animation
        binding.loadingAnimation.setVisibility(View.VISIBLE);
        binding.loadingAnimation.playAnimation();
        Call<List<Homework>> getHomeworksCall = api.getSectionHomeworks(section, "Token " + currentUser.getToken());
        getHomeworksCall.enqueue(new Callback<List<Homework>>() {
            @Override
            public void onResponse(@NonNull Call<List<Homework>> call, @NonNull Response<List<Homework>> response) {
                if (response.code() == Utils.HttpResponses.HTTP_200_OK) {
                    homeworks = response.body();
                    if (homeworks != null) {
                        if (!homeworks.isEmpty()) {
                            adapter.setHomeworks(homeworks);
                            binding.homeworksRecView.setAdapter(adapter);
                            binding.homeworksRecView.setVisibility(View.VISIBLE);
                            binding.emptyMsg.setVisibility(View.GONE);
                        } else {
                            binding.homeworksRecView.setVisibility(View.GONE);
                            binding.emptyMsg.setVisibility(View.VISIBLE);
                        }
                    } else {
                        binding.homeworksRecView.setVisibility(View.GONE);
                        binding.emptyMsg.setVisibility(View.VISIBLE);
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                }
                // Stop loading animation
                binding.loadingAnimation.setVisibility(View.GONE);
                binding.loadingAnimation.cancelAnimation();
            }

            @Override
            public void onFailure(@NonNull Call<List<Homework>> call, @NonNull Throwable t) {
                // Stop loading animation
                binding.loadingAnimation.setVisibility(View.GONE);
                binding.loadingAnimation.cancelAnimation();
                Toast.makeText(getActivity(), getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void restoreSectionsFilterState(AutoCompleteTextView filter) {
        filter.setText(selectedSection, false);
    }

    private void restoreDateFilterState(AutoCompleteTextView filter) {
        filter.setText(filteredDate, false);
    }

    private void initRecView() {
        binding.homeworksRecView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.homeworksRecView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayout.VERTICAL));
        adapter = new HomeworkAdapter(getContext());

        // Swipe to action in rec view
        if (currentUserType.equals(Utils.TEACHER_ACCOUNT)) {
            // If its a teacher then show delete + edit buttons
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(teacherCallBack);
            itemTouchHelper.attachToRecyclerView(binding.homeworksRecView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Swipe to delete and edit in the recycler view
    ItemTouchHelper.SimpleCallback teacherCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Homework currentHomework = adapter.getHomeworks().get(position);
            switch (direction) {
                case ItemTouchHelper.LEFT: // Swipe left to right <- : Delete item
                    if (currentHomework.getTeacherEmail().equals(currentUser.getCurrentTeacher().getEmail())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(R.string.are_you_sure);
                        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                            //Delete the homework
                            Call<ResponseBody> deleteHomeworkCall = api.deleteHomework(currentHomework.getId(),"Token " + currentUser.getToken());
                            loadingDialog.show();
                            deleteHomeworkCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                                    if(response.code() == Utils.HttpResponses.HTTP_204_NO_CONTENT)
                                    {
                                        //Homework successfully deleted
                                        Toast.makeText(getActivity(), getString(R.string.homework_deleted_msg), Toast.LENGTH_SHORT).show();
                                        homeworks.remove(currentHomework);
                                        adapter.getHomeworks().remove(currentHomework);
                                        adapter.notifyItemRemoved(position);
                                        Toast.makeText(getContext(), getString(R.string.homework_deleted_msg), Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getActivity(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                                        adapter.notifyItemRemoved(position);
                                    }
                                    loadingDialog.dismiss();
                                }
                                @Override
                                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                                    Toast.makeText(getActivity(), getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
                                    adapter.notifyItemRemoved(position);
                                    loadingDialog.dismiss();
                                }
                            });

                        });
                        builder.setNegativeButton(R.string.no, (dialog, which) -> {
                            // Do Nothing
                            adapter.notifyItemChanged(position); // To reset the item on the screen
                        });
                        builder.create().show();
                        break;
                    }
                    else {
                        Toast.makeText(getContext(), getString(R.string.delete_your_homework_msg), Toast.LENGTH_SHORT).show();
                        adapter.notifyItemChanged(position); // To reset the item on the screen
                    }

                case ItemTouchHelper.RIGHT: // Swipe right to left -> : Edit item
                    if (currentHomework.getTeacherEmail().equals(currentUser.getCurrentTeacher().getEmail())) {
                        Intent intent = new Intent(getContext(), AddHomeworkActivity.class);
                        intent.putExtra(Utils.ACTION, Utils.ACTION_UPDATE);
                        intent.putExtra(Utils.HOMEWORK, currentHomework);
                        startActivity(intent);
                        adapter.notifyItemChanged(position); // To reset the item on the screen
                        break;
                    }
                    else
                    {
                        Toast.makeText(getContext(), getString(R.string.edit_your_homework_msg), Toast.LENGTH_SHORT).show();
                        adapter.notifyItemChanged(position); // To reset the item on the screen
                    }
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

    @Override
    public void onStart() {
        super.onStart();
        // If its an admin then get all sections to setup the filter
        if (currentUserType.equals(Utils.ADMIN_ACCOUNT)) {
            Call<List<Section>> call = api.getAllSections();
            call.enqueue(new Callback<List<Section>>() {
                @Override
                public void onResponse(@NonNull Call<List<Section>> call, @NonNull Response<List<Section>> response) {
                    if (response.isSuccessful()) {
                        List<Section> sectionsList = response.body();
                        allSections = new ArrayList<>();
                        if (sectionsList != null) {
                            // Get names
                            for (Section sec : sectionsList) {
                                allSections.add(sec.getCode());
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.error) + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Section>> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (currentUserType.equals(Utils.STUDENT_ACCOUNT)) {
            Student student = currentUser.getCurrentStudent();
            // Get the student's sessions
            getHomeworks(student.getSection().getCode());
        } else if (currentUserType.equals(Utils.TEACHER_ACCOUNT) || currentUserType.equals(Utils.ADMIN_ACCOUNT)) {
            if (filterApplied) {
                getHomeworks(selectedSection);
            }
        }
    }

    private void filterHomeworksPlusDays(int days)
    {
        LocalDate today = LocalDate.now();
        filteredHomeworks.clear();
        for(Homework homework:homeworks)
        {
            if(homework.getLocalDateDueDate().equals(today.plusDays(days)))
            {
                filteredHomeworks.add(homework);
            }
        }
        adapter.setHomeworks(filteredHomeworks);
        binding.homeworksRecView.setAdapter(adapter);
    }

    private void filterHomeworksThisWeek()
    {
        LocalDate today = LocalDate.now();
        filteredHomeworks.clear();
        for(Homework homework:homeworks)
        {
            if(homework.getLocalDateDueDate().isBefore(today.plusWeeks(1)) && homework.getLocalDateDueDate().isAfter(today.minusDays(1)))
            {
                filteredHomeworks.add(homework);
            }
        }
        adapter.setHomeworks(filteredHomeworks);
        binding.homeworksRecView.setAdapter(adapter);
    }
}