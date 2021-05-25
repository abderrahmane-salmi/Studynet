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
import android.view.ViewTreeObserver;
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
import com.salmi.bouchelaghem.studynet.Activities.AddClassActivity;
import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.Adapters.SessionsAdapter;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Session;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.CustomLoadingDialog;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentTimetableBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TimetableFragment extends Fragment {

    private FragmentTimetableBinding binding;

    // Days
    private boolean firstTime = true;
    private int currentDay = 1;
    private List<String> days;

    // Rec view
    private List<Session> sessions;
    private SessionsAdapter adapter;

    // Filter
    private Dialog dialog;
    private boolean sectionSelected = false;
    private String selectedSection;
    private boolean filterApplied = false;
    private List<String> allSections;

    // Current user
    private final CurrentUser currentUser = CurrentUser.getInstance();
    private final String currentUserType = currentUser.getUserType();

    // Studynet Api
    private StudynetAPI api;

    private CustomLoadingDialog loadingDialog;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTimetableBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

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

        //Init loading dialog
        loadingDialog = new CustomLoadingDialog(requireContext());
        switch (currentUserType) {
            case Utils.TEACHER_ACCOUNT:  // If the user is a teacher

                if (!filterApplied){
                    binding.selectSectionMsg.setVisibility(View.VISIBLE);
                } else {
                    binding.selectSectionMsg.setVisibility(View.GONE);
                }

                // Get the current teacher from the app API
                Teacher teacher = currentUser.getCurrentTeacher();

                // Show the add button
                binding.btnAdd.setVisibility(View.VISIBLE);
                binding.btnAdd.setOnClickListener(v -> {
                    Intent intent = new Intent(context, AddClassActivity.class);
                    intent.putExtra(Utils.ACTION, Utils.ACTION_ADD);
                    startActivity(intent);
                });

                // Show and setup the filter
                context.btnFilter.setVisibility(View.VISIBLE);
                context.btnFilter.setOnClickListener(v -> {
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

                    if (filterApplied) {
                        restoreFilterState(filterTimetableSection); // set the filter values to the last filter applied
                    }

                    // Init Buttons
                    btnApplyFilter.setOnClickListener(v1 -> {

                        if (sectionSelected) {

                            filterApplied = true;
                            getSessions(selectedSection);
                            binding.selectSectionMsg.setVisibility(View.GONE);
                            dialog.dismiss();

                        } else {
                            Toast.makeText(getActivity(), getString(R.string.no_filter_msg), Toast.LENGTH_SHORT).show();
                        }
                    });

                    btnCloseFilter.setOnClickListener(v12 -> dialog.dismiss());

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
                });

                break;
            case Utils.ADMIN_ACCOUNT:  // If the user is an admin

                if (!filterApplied){
                    binding.selectSectionMsg.setVisibility(View.VISIBLE);
                } else {
                    binding.selectSectionMsg.setVisibility(View.GONE);
                }

                // Show and setup the filter
                context.btnFilter.setVisibility(View.VISIBLE);
                context.btnFilter.setOnClickListener(v -> {
                    if (allSections != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View view12 = View.inflate(context, R.layout.popup_sections_filter, null);
                        // Init Views
                        ImageView btnCloseFilter = view12.findViewById(R.id.btnCloseFilter);
                        AutoCompleteTextView filterTimetableSection = view12.findViewById(R.id.filterTimetableSection);
                        MaterialButton btnApplyFilter = view12.findViewById(R.id.btnApplyFilter);

                        // Init sections spinner
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, allSections);
                        filterTimetableSection.setAdapter(arrayAdapter);

                        if (filterApplied) {
                            restoreFilterState(filterTimetableSection); // set the filter values to the last filter applied
                        }

                        // Init Buttons
                        btnApplyFilter.setOnClickListener(v13 -> {

                            if (sectionSelected) {

                                getSessions(selectedSection);
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
                });

                break;
            case Utils.STUDENT_ACCOUNT: // If the user is a student

                // Hide the filter button
                context.btnFilter.setVisibility(View.GONE);

                break;
        }

        // Init days
        days = Arrays.asList(getResources().getStringArray(R.array.days));

        // Change the day by clicking on the wanted day
        binding.day1.setOnClickListener(v -> goToDay1());

        binding.day2.setOnClickListener(v -> goToDay2());

        binding.day3.setOnClickListener(v -> goToDay3());

        binding.day4.setOnClickListener(v -> goToDay4());

        binding.day5.setOnClickListener(v -> goToDay5());

        binding.day6.setOnClickListener(v -> goToDay6());

        return view;
    }

    private void restoreFilterState(AutoCompleteTextView filter) {
        filter.setText(selectedSection, false);
    }

    private void getSessions(String sectionCode) {
        // Start loading animation
        binding.loadingAnimation.setVisibility(View.VISIBLE);
        binding.loadingAnimation.playAnimation();
        // Get all the sessions for this section
        Call<List<Session>> getSectionSessionsCall = api.getSectionSessions(sectionCode, "Token " + currentUser.getToken());
        getSectionSessionsCall.enqueue(new getSectionSessionsCallback());
    }

    private void initRecView() {
        sessions = new ArrayList<>();
        binding.classesRecView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.classesRecView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayout.VERTICAL));
        adapter = new SessionsAdapter(getContext());

        // Swipe to action in rec view
        if (currentUserType.equals(Utils.TEACHER_ACCOUNT)) {
            // If its a teacher then show delete + edit buttons
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(teacherCallBack);
            itemTouchHelper.attachToRecyclerView(binding.classesRecView);
        } else if (currentUserType.equals(Utils.ADMIN_ACCOUNT)) {
            // If its an admin then show report button
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adminCallBack);
            itemTouchHelper.attachToRecyclerView(binding.classesRecView);
        }
    }

    private void showTodaySessions(int today) {
        List<Session> todaySessions = new ArrayList<>();
        int sessionsCount = 0;
        if (!sessions.isEmpty()) {
            for (Session session : sessions) {
                // Get only today's sessions
                if (session.getDay() == today) {
                    todaySessions.add(session);
                    sessionsCount++;
                }
            }

            if (!todaySessions.isEmpty()) {
                // Show the sessions in the rec view
                adapter.setSessions(todaySessions);
                binding.classesRecView.setAdapter(adapter);
                binding.classesRecView.setVisibility(View.VISIBLE);
                binding.emptyMsg.setVisibility(View.GONE);
            } else {
                binding.classesRecView.setVisibility(View.GONE);
                binding.emptyMsg.setVisibility(View.VISIBLE);
            }
        } else {
            binding.classesRecView.setVisibility(View.GONE);
            binding.emptyMsg.setVisibility(View.VISIBLE);
        }

        // Show the sessions counter
        if (sessionsCount == 1) { // if its 1 then show the word "class" not "classes"
            binding.textView4.setText(getString(R.string.class_1));
        } else {
            binding.textView4.setText(getString(R.string.classes));
        }
        binding.txtClassesCount.setText(String.valueOf(sessionsCount));
    }

    private void goToToday() {
        // Used ViewTreeObserver to wait for the UI to be sized and then we can get the the view's width
        binding.day2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.day2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Show today's classes by default
                Calendar calendar = Calendar.getInstance();
                switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                    case 0:
                    case 6: // If its the weekend then show him the classes of the first day of next week
                        goToDay1();
                        break;
                    case 1:
                        goToDay2();
                        break;
                    case 2:
                        goToDay3();
                        break;
                    case 3:
                        goToDay4();
                        break;
                    case 4:
                        goToDay5();
                        break;
                    case 5:
                        goToDay6();
                        break;
                }
            }
        });
    }

    private void goToDay1() {
        currentDay = 1;
        // Adding an animation
        binding.selectedDay.animate().x(0).setDuration(100);
        // Changing text colors
        binding.day1.setTextColor(Color.WHITE);
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(0));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (currentUserType.equals(Utils.STUDENT_ACCOUNT) || filterApplied) {
            // Show today's classes
            showTodaySessions(1);
        }
    }

    private void goToDay2() {
        currentDay = 2;
        int size = binding.day2.getWidth();
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(Color.WHITE);
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(1));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (currentUserType.equals(Utils.STUDENT_ACCOUNT) || filterApplied) {
            // Show today's classes
            showTodaySessions(2);
        }
    }

    private void goToDay3() {
        currentDay = 3;
        int size = binding.day2.getWidth() * 2;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(Color.WHITE);
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(2));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (currentUserType.equals(Utils.STUDENT_ACCOUNT) || filterApplied) {
            // Show today's classes
            showTodaySessions(3);
        }
    }

    private void goToDay4() {
        currentDay = 4;
        int size = binding.day2.getWidth() * 3;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(Color.WHITE);
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(3));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (currentUserType.equals(Utils.STUDENT_ACCOUNT) || filterApplied) {
            // Show today's classes
            showTodaySessions(4);
        }
    }

    private void goToDay5() {
        currentDay = 5;
        int size = binding.day2.getWidth() * 4;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(Color.WHITE);
        binding.day6.setTextColor(getResources().getColor(R.color.secondary_text_color, null));

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(4));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (currentUserType.equals(Utils.STUDENT_ACCOUNT) || filterApplied) {
            // Show today's classes
            showTodaySessions(5);
        }
    }

    private void goToDay6() {
        currentDay = 6;
        int size = binding.day2.getWidth() * 5;
        binding.selectedDay.animate().x(size).setDuration(100);
        binding.day1.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day2.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day3.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day4.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day5.setTextColor(getResources().getColor(R.color.secondary_text_color, null));
        binding.day6.setTextColor(Color.WHITE);

        // Setting the day's name
        binding.txtSelectedDay.setText(days.get(5));

        // Show today's sessions only if the user is a student or the teacher/admin applied a filter
        if (currentUserType.equals(Utils.STUDENT_ACCOUNT) || filterApplied) {
            // Show today's classes
            showTodaySessions(6);
        }
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
            Session currentSession = adapter.getSessions().get(position);

            switch (direction) {
                case ItemTouchHelper.LEFT: // Swipe left to right <- : Delete item
                    if (currentSession.getTeacherEmail().equals(currentUser.getCurrentTeacher().getEmail())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(R.string.are_you_sure);
                        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                            //Delete the session from the api
                            Call<ResponseBody> deleteSessionCall = api.deleteSession(currentSession.getId(),"Token " + currentUser.getToken());
                            loadingDialog.show();
                            deleteSessionCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                                    if(response.code() == Utils.HttpResponses.HTTP_204_NO_CONTENT)
                                    {
                                        //The session has been removed from the api, remove it locally
                                        sessions.remove(currentSession);
                                        adapter.getSessions().remove(position);
                                        adapter.notifyItemRemoved(position);
                                        //Decrease the number of sessions by 1
                                        int sessionsCount = Integer.parseInt(binding.txtClassesCount.getText().toString());
                                        sessionsCount--;
                                        binding.txtClassesCount.setText(String.valueOf(sessionsCount));
                                        if (sessionsCount == 1) { // if its 1 then show the word "class" not "classes"
                                            binding.textView4.setText(getString(R.string.class_1));
                                        } else {
                                            binding.textView4.setText(getString(R.string.classes));
                                        }
                                        if(sessionsCount == 0)
                                        {
                                            binding.emptyMsg.setVisibility(View.VISIBLE);
                                        }
                                        Toast.makeText(getContext(), getString(R.string.session_deleted_msg), Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getContext(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
                                    }
                                    loadingDialog.dismiss();
                                }

                                @Override
                                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                                    Toast.makeText(getContext(), getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();
                                }
                            });

                        });
                        builder.setNegativeButton(R.string.no, (dialog, which) -> {
                            // Do Nothing
                            adapter.notifyItemChanged(position); // To reset the item on the screen
                        });
                        builder.create().show();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.delete_your_session_msg), Toast.LENGTH_SHORT).show();
                        adapter.notifyItemChanged(position); // To reset the item on the screen
                    }
                    break;
                case ItemTouchHelper.RIGHT: // Swipe right to left -> : Edit item
                    // To reset the item on the screen
                    if (currentSession.getTeacherEmail().equals(currentUser.getCurrentTeacher().getEmail())) {
                        Intent intent = new Intent(getContext(), AddClassActivity.class);
                        intent.putExtra(Utils.ACTION, Utils.ACTION_UPDATE);
                        intent.putExtra(Utils.SESSION, currentSession);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.edit_your_session_msg), Toast.LENGTH_SHORT).show();
                    }
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

    ItemTouchHelper.SimpleCallback adminCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            Session currentSession = adapter.getSessions().get(position);

            if (direction == ItemTouchHelper.LEFT) { // Swipe left to right <- : Report session
                Toast.makeText(getContext(), "Report", Toast.LENGTH_SHORT).show();
                adapter.notifyItemChanged(position); // To reset the item on the screen
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_report)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private class getSectionSessionsCallback implements Callback<List<Session>> {

        @Override
        public void onResponse(@NonNull Call<List<Session>> call, Response<List<Session>> response) {
            if (response.code() == Utils.HttpResponses.HTTP_200_OK) {
                sessions = response.body();

                if (firstTime){
                    // If its the first time we launch the fragment and its a student then show him today's classes
                    if (currentUserType.equals(Utils.STUDENT_ACCOUNT)) {
                        goToToday();
                    } else if (currentUserType.equals(Utils.ADMIN_ACCOUNT) || currentUserType.equals(Utils.TEACHER_ACCOUNT)){
                        // If its the first time we launch the fragment and its a teacher or an admin then show him first day's classes
                        goToDay1();
                    }
                    firstTime = false;
                } else {
                    // Show the user the classes he last checked
                    switch (currentDay){
                        case 1:
                        case 7: // If its the weekend then show him the classes of the first day of next week
                            goToDay1();
                            break;
                        case 2:
                            goToDay2();
                            break;
                        case 3:
                            goToDay3();
                            break;
                        case 4:
                            goToDay4();
                            break;
                        case 5:
                            goToDay5();
                            break;
                        case 6:
                            goToDay6();
                            break;
                    }
                }

            } else {
                Toast.makeText(getActivity(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
            // Stop loading animation
            binding.loadingAnimation.setVisibility(View.GONE);
            binding.loadingAnimation.cancelAnimation();
        }

        @Override
        public void onFailure(@NonNull Call<List<Session>> call, @NonNull Throwable t) {
            // Stop loading animation
            binding.loadingAnimation.setVisibility(View.GONE);
            binding.loadingAnimation.cancelAnimation();
            Toast.makeText(getContext(), getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // If its an admin then get all sections to setup the filter
        if (currentUserType.equals(Utils.ADMIN_ACCOUNT) && firstTime) {
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

        if (currentUserType.equals(Utils.STUDENT_ACCOUNT)){
            Student student = currentUser.getCurrentStudent();
            // Get the student's sessions
            getSessions(student.getSection().getCode());
        } else if (currentUserType.equals(Utils.TEACHER_ACCOUNT) || currentUserType.equals(Utils.ADMIN_ACCOUNT)){
            if (filterApplied){
                getSessions(selectedSection);
            }
        }
    }
}