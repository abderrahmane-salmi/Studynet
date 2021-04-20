package com.salmi.bouchelaghem.studynet.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.salmi.bouchelaghem.studynet.Activities.AddClassActivity;
import com.salmi.bouchelaghem.studynet.Activities.NavigationActivity;
import com.salmi.bouchelaghem.studynet.Adapters.SessionsAdapter;
import com.salmi.bouchelaghem.studynet.Models.Admin;
import com.salmi.bouchelaghem.studynet.Models.Section;
import com.salmi.bouchelaghem.studynet.Models.Session;
import com.salmi.bouchelaghem.studynet.Models.Student;
import com.salmi.bouchelaghem.studynet.Models.Teacher;
import com.salmi.bouchelaghem.studynet.R;
import com.salmi.bouchelaghem.studynet.Utils.CurrentUser;
import com.salmi.bouchelaghem.studynet.Utils.TestAPI;
import com.salmi.bouchelaghem.studynet.Utils.Utils;
import com.salmi.bouchelaghem.studynet.databinding.FragmentTimetableBinding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TimetableFragment extends Fragment {

    private FragmentTimetableBinding binding;
    private final CurrentUser currentUser = CurrentUser.getInstance();

    private Dialog dialog;

    // Days
    private int currentDay = 1;
    private List<String> days;

    // Rec view
    private List<Session> sessions;
    private SessionsAdapter adapter;

    // Filter
    private boolean sectionSelected = false;
    private String selectedSection;

    // Test api
    TestAPI testAPI;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentTimetableBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initRecView();

        // Test api
        testAPI = TestAPI.getInstance();

        NavigationActivity context = (NavigationActivity) getActivity();
        assert context != null;

        switch (currentUser.getUserType()) {
            case Utils.TEACHER_ACCOUNT:  // If the user is a teacher

                binding.selectSectionMsg.setVisibility(View.VISIBLE);

                // Get the current teacher from the app API
                Teacher teacher = currentUser.getCurrentTeacher();

                // Show the add button
                binding.btnAdd.setVisibility(View.VISIBLE);
                binding.btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(context, AddClassActivity.class));
                    }
                });

                // Show and setup the filter
                context.btnFilter.setVisibility(View.VISIBLE);
                context.btnFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View view = View.inflate(context, R.layout.popup_teacher_timetable_filter, null);
                        // Init Views
                        ImageView btnCloseFilter = view.findViewById(R.id.btnCloseFilter);
                        AutoCompleteTextView filterTimetableSection = view.findViewById(R.id.filterTimetableSection);
                        MaterialButton btnApplyFilter = view.findViewById(R.id.btnApplyFilter);

                        // Init sections list
                        // Get all the sections
                        List<Section> sections = testAPI.getSections();

                        // Get only the teacher's sections
                        List<String> sectionsNames = new ArrayList<>();
                        for (Section section : sections) {
//                            if (section.getTeachers().contains(currentUser.getCurrentTeacher())) {
//                                sectionsNames.add(section.getCode());
//                            }
                            sectionsNames.add(section.getCode());
                        }
                        if (!sectionsNames.isEmpty()) {
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.dropdown_item, sectionsNames);
                            filterTimetableSection.setAdapter(arrayAdapter);
                        }

                        // TODO: restoreFilterState(); // set the filter values to the last filter applied

                        // Init Buttons
                        btnApplyFilter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (sectionSelected) {

                                    getSessions(selectedSection);

                                } else {
                                    Toast.makeText(getActivity(), "There is no filter applied!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        btnCloseFilter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        filterTimetableSection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                sectionSelected = true;
                                selectedSection = sectionsNames.get(position);
                            }
                        });

                        builder.setView(view);
                        dialog = builder.create(); // creating our dialog
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        // Show rounded corners
                        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                        dialog.getWindow().setAttributes(params);
                    }
                });

                break;
            case Utils.ADMIN_ACCOUNT:  // If the user is an admin

                binding.selectSectionMsg.setVisibility(View.VISIBLE);

                // Get the current admin from the app api
                Admin admin = currentUser.getCurrentAdmin();

                // Show the signal button on the sessions

                // Show and setup the filter
                context.btnFilter.setVisibility(View.VISIBLE);
                context.btnFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        View view = View.inflate(context, R.layout.popup_teacher_timetable_filter, null);
                        // Init Views
                        ImageView btnCloseFilter = view.findViewById(R.id.btnCloseFilter);
                        AutoCompleteTextView filterTimetableSection = view.findViewById(R.id.filterTimetableSection);
                        MaterialButton btnApplyFilter = view.findViewById(R.id.btnApplyFilter);

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

                        // TODO: restoreFilterState(); // set the filter values to the last filter applied

                        // Init Buttons
                        btnApplyFilter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (sectionSelected) {

                                    getSessions(selectedSection);

                                } else {
                                    Toast.makeText(getActivity(), "There is no filter applied!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                        btnCloseFilter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        filterTimetableSection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                sectionSelected = true;
                                selectedSection = sectionsNames.get(position);
                            }
                        });

                        builder.setView(view);
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

                // Get the current student
                Student student = currentUser.getCurrentStudent();

                // Get the student's sessions
                getSessions(student.getSection().getCode());

                break;
        }

        // Init days
        days = Arrays.asList(getResources().getStringArray(R.array.days));

        // Change the day by clicking on the wanted day
        binding.day1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDay1();
            }
        });

        binding.day2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDay2();
            }
        });

        binding.day3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDay3();
            }
        });

        binding.day4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDay4();
            }
        });

        binding.day5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDay5();
            }
        });

        binding.day6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDay6();
            }
        });

        // If its a student show him today's classes
        if (currentUser.getUserType().equals(Utils.STUDENT_ACCOUNT)){

            // Used ViewTreeObserver to wait for the UI to be sized and then we can get the the view's width
            binding.day2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    binding.day2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    // Show today's classes by default
                    Calendar calendar = Calendar.getInstance();
                    switch (calendar.get(Calendar.DAY_OF_WEEK)){
                        case 0:
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

//        binding.included.classMainLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), ClassDetailsActivity.class));
//            }
//        });

//        binding.included.classMainLayout.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
//
//            @SuppressLint("ClickableViewAccessibility")
//            public void onSwipeRight() {
//                switch (currentDay){
//                    case 1:
//                        goToDay6();
//                        break;
//                    case 2:
//                        goToDay1();
//                        break;
//                    case 3:
//                        goToDay2();
//                        break;
//                    case 4:
//                        goToDay3();
//                        break;
//                    case 5:
//                        goToDay4();
//                        break;
//                    case 6:
//                        goToDay5();
//                        break;
//                }
//            }
//
//            public void onSwipeLeft() {
//                switch (currentDay){
//                    case 1:
//                        goToDay2();
//                        break;
//                    case 2:
//                        goToDay3();
//                        break;
//                    case 3:
//                        goToDay4();
//                        break;
//                    case 4:
//                        goToDay5();
//                        break;
//                    case 5:
//                        goToDay6();
//                        break;
//                    case 6:
//                        goToDay1();
//                        break;
//                }
//            }
//        });

        return view;
    }

    private void getSessions(String sectionCode) {
        // Get all the sessions
        List<Session> allSessions = testAPI.getSessions();

        // Get only the section's sessions
        sessions.clear();
        for (Session session : allSessions) {
            if (session.getAssignment().getSectionCode().equals(sectionCode)) {
                sessions.add(session);
            }
        }

        if (!sessions.isEmpty()) {

            adapter.setSessions(sessions);
            binding.classesRecView.setAdapter(adapter);
            binding.classesRecView.setVisibility(View.VISIBLE);
            binding.emptyMsg.setVisibility(View.GONE);

        } else {
            binding.classesRecView.setVisibility(View.GONE);
            binding.emptyMsg.setVisibility(View.VISIBLE);
        }
    }

    private void initRecView() {
        sessions = new ArrayList<>();
        binding.classesRecView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SessionsAdapter(getContext());
    }

//    private int getDayFromDate(int date){
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        return c.get(Calendar.DAY_OF_WEEK)-1;
//    }

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

        // Show today's classes
        showTodaySessions(1);

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

        // Show today's classes
        showTodaySessions(2);
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

        // Show today's classes
        showTodaySessions(3);
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

        // Show today's classes
        showTodaySessions(4);
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

        // Show today's classes
        showTodaySessions(5);
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

        // Show today's classes
        showTodaySessions(6);
    }

}