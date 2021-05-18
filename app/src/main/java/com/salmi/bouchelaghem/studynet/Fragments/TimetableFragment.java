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
import com.salmi.bouchelaghem.studynet.Activities.AddTeacherActivity;
import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.Adapters.SessionsAdapter;
import com.salmi.bouchelaghem.studynet.Models.Admin;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Session;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.StudynetAPI;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentTimetableBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TimetableFragment extends Fragment {

    private FragmentTimetableBinding binding;
    private final CurrentUser currentUser = CurrentUser.getInstance();

    private Dialog dialog;

    // Days
    // TODO: use it or delete it
    private int currentDay = 1;
    private List<String> days;

    // Rec view
    private List<Session> sessions;
    private SessionsAdapter adapter;

    // Filter
    private boolean sectionSelected = false;
    private String selectedSection;
    private boolean filterApplied = false;

    // Test api
    TestAPI testAPI;
    private String currentUserType;

    // Studynet Api
    private StudynetAPI api;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTimetableBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Get current user type
        currentUserType = currentUser.getUserType();
        initRecView();

        // Init retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Init our api
        api = retrofit.create(StudynetAPI.class);

        // Test api
        testAPI = TestAPI.getInstance();

        NavigationActivity context = (NavigationActivity) getActivity();
        assert context != null;

        switch (currentUserType) {
            case Utils.TEACHER_ACCOUNT:  // If the user is a teacher

                binding.selectSectionMsg.setVisibility(View.VISIBLE);

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
                    View view1 = View.inflate(context, R.layout.popup_teacher_timetable_filter, null);
                    // Init Views
                    ImageView btnCloseFilter = view1.findViewById(R.id.btnCloseFilter);
                    AutoCompleteTextView filterTimetableSection = view1.findViewById(R.id.filterTimetableSection);
                    MaterialButton btnApplyFilter = view1.findViewById(R.id.btnApplyFilter);

                    // Init sections list
                    // Get all the sections
                    List<Section> sections = testAPI.getSections();

                    // Get only the teacher's sections
                    List<String> sectionsNames = new ArrayList<>();
                    for (Section section : sections) {
                        sectionsNames.add(section.getCode());
                    }
                    if (!sectionsNames.isEmpty()) {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, sectionsNames);
                        filterTimetableSection.setAdapter(arrayAdapter);
                    }

                    if (filterApplied){
                        restoreFilterState(filterTimetableSection); // set the filter values to the last filter applied
                    }

                    // Init Buttons
                    btnApplyFilter.setOnClickListener(v1 -> {

                        if (sectionSelected) {

                            filterApplied = true;
                            getSessions(selectedSection);
                            goToDay1();
                            binding.selectSectionMsg.setVisibility(View.GONE);
                            dialog.dismiss();

                        } else {
                            Toast.makeText(getActivity(), getString(R.string.no_filter_msg), Toast.LENGTH_SHORT).show();
                        }
                    });

                    btnCloseFilter.setOnClickListener(v12 -> dialog.dismiss());

                    filterTimetableSection.setOnItemClickListener((parent, view11, position, id) -> {
                        sectionSelected = true;
                        selectedSection = sectionsNames.get(position);
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

                binding.selectSectionMsg.setVisibility(View.VISIBLE);

                // Get the current admin from the app api
                Admin admin = currentUser.getCurrentAdmin();

                // Show the signal button on the sessions

                // Show and setup the filter
                context.btnFilter.setVisibility(View.VISIBLE);
                context.btnFilter.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view12 = View.inflate(context, R.layout.popup_teacher_timetable_filter, null);
                    // Init Views
                    ImageView btnCloseFilter = view12.findViewById(R.id.btnCloseFilter);
                    AutoCompleteTextView filterTimetableSection = view12.findViewById(R.id.filterTimetableSection);
                    MaterialButton btnApplyFilter = view12.findViewById(R.id.btnApplyFilter);

                    // Init sections list
                    // Get all the sections
                    List<Section> sections = testAPI.getSections();

                    // If its an admin get him all the sections
                    List<String> sectionsNames = new ArrayList<>();
                    for (Section section : sections) {
                        sectionsNames.add(section.getCode());
                    }
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

                            getSessions(selectedSection);
                            goToDay1();
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

                break;
            case Utils.STUDENT_ACCOUNT: // If the user is a student

                // Get the current student
                Student student = currentUser.getCurrentStudent();

                // Hide the filter button
                context.btnFilter.setVisibility(View.GONE);

                // Get the student's sessions
                getSessions(student.getSection().getCode());

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

        // If its a student show him today's classes
        if (currentUserType.equals(Utils.STUDENT_ACCOUNT)){

            // Used ViewTreeObserver to wait for the UI to be sized and then we can get the the view's width
            binding.day2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    binding.day2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    // Show today's classes by default
                    Calendar calendar = Calendar.getInstance();
                    switch (calendar.get(Calendar.DAY_OF_WEEK)){
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

        return view;
    }

    private void restoreFilterState(AutoCompleteTextView filter) {
        filter.setText(selectedSection, false);
    }

    private void getSessions(String sectionCode) {
        // Get all the sessions
        //TODO: Get sessions from the real API.
        Call<List<Session>> getSectionSessionsCall = api.getSectionSessions(sectionCode, "Token " + currentUser.getToken());
        getSectionSessionsCall.enqueue(new getSectionSessionsCallback());
//        List<Session> allSessions = testAPI.getSessions();
//
//        // Get only the section's sessions
//        sessions.clear();
//        for (Session session : allSessions) {
//            if (session.getSection().equals(sectionCode)) {
//                sessions.add(session);
//            }
//        }
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
        } else if (currentUserType.equals(Utils.ADMIN_ACCOUNT)){
            // If its an admin then show report button
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(adminCallBack);
            itemTouchHelper.attachToRecyclerView(binding.classesRecView);
        }
    }

    private void showTodaySessions(int today){

        List<Session> todaySessions = new ArrayList<>();
        if (!sessions.isEmpty()){
            int sessionsCount = 0;
            for (Session session:sessions){
                // Get only today's sessions
                if (session.getDay() == today){
                    todaySessions.add(session);
                    sessionsCount ++;
                }
            }

            if (!todaySessions.isEmpty()){
                // Show the sessions in the rec view
                adapter.setSessions(todaySessions);
                binding.classesRecView.setAdapter(adapter);
                binding.classesRecView.setVisibility(View.VISIBLE);
                binding.emptyMsg.setVisibility(View.GONE);
            } else {
                binding.classesRecView.setVisibility(View.GONE);
                binding.emptyMsg.setVisibility(View.VISIBLE);
            }
            // Show the sessions counter
            if (sessionsCount == 1){ // if its 1 then show the word "class" not "classes"
                binding.textView4.setText(getString(R.string.class_1));
            } else {
                binding.textView4.setText(getString(R.string.classes));
            }
            binding.txtClassesCount.setText(String.valueOf(sessionsCount));

        } else {
            binding.classesRecView.setVisibility(View.GONE);
            binding.emptyMsg.setVisibility(View.VISIBLE);
        }
    }

    private void goToDay1(){
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
        if(currentUserType.equals(Utils.STUDENT_ACCOUNT) || filterApplied) {
            // Show today's classes
            showTodaySessions(1);
        }
    }

    private void goToDay2(){
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
        if(currentUserType.equals(Utils.STUDENT_ACCOUNT) || filterApplied) {
            // Show today's classes
            showTodaySessions(2);
        }
    }

    private void goToDay3(){
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
        if(currentUserType.equals(Utils.STUDENT_ACCOUNT) || filterApplied) {
            // Show today's classes
            showTodaySessions(3);
        }
    }

    private void goToDay4(){
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
        if(currentUserType.equals(Utils.STUDENT_ACCOUNT) || filterApplied) {
            // Show today's classes
            showTodaySessions(4);
        }
    }

    private void goToDay5(){
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
        if(currentUserType.equals(Utils.STUDENT_ACCOUNT) || filterApplied) {
            // Show today's classes
            showTodaySessions(5);
        }
    }

    private void goToDay6(){
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
        if(currentUserType.equals(Utils.STUDENT_ACCOUNT) || filterApplied) {
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

            switch (direction){
                case ItemTouchHelper.LEFT: // Swipe left to right <- : Delete item

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.are_you_sure);
                    builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                        sessions.remove(currentSession);
                        adapter.getSessions().remove(position);
                        adapter.notifyItemRemoved(position);
                        Toast.makeText(getContext(), getString(R.string.session_deleted_msg), Toast.LENGTH_SHORT).show();
                    });
                    builder.setNegativeButton(R.string.no, (dialog, which) -> {
                        // Do Nothing
                        adapter.notifyItemChanged(position); // To reset the item on the screen
                    });
                    builder.create().show();
                    break;
                case ItemTouchHelper.RIGHT: // Swipe right to left -> : Edit item
                    Intent intent = new Intent(getContext(), AddClassActivity.class);
                    intent.putExtra(Utils.ACTION, Utils.ACTION_UPDATE);
                    intent.putExtra(Utils.ID, currentSession.getId());
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

    ItemTouchHelper.SimpleCallback adminCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();
            Session currentSession = adapter.getSessions().get(position);

            if (direction == ItemTouchHelper.LEFT){ // Swipe left to right <- : Report session
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

    private class getSectionSessionsCallback implements Callback<List<Session>>
    {

        @Override
        public void onResponse(Call<List<Session>> call, Response<List<Session>> response) {
            if(response.code() == Utils.HttpResponses.HTTP_200_OK)
            {
                sessions = response.body();
                Toast.makeText(getActivity(), "Retrieved sessions successfully.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity(), getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<List<Session>> call, Throwable t) {

        }
    }
}